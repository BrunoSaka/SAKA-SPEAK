package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AiSpeechAssessment
import com.example.data.model.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiSpeechService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun evaluateSpeech(
        topic: Topic,
        transcriptOrNotes: String,
        actualSeconds: Int,
        targetSeconds: Int,
        hasAudioRecording: Boolean
    ): Pair<AiSpeechAssessment, Boolean> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidKey) {
            try {
                val assessment = callGeminiApi(apiKey, topic, transcriptOrNotes, actualSeconds, targetSeconds)
                if (assessment != null) {
                    return@withContext Pair(assessment, true) // Evaluated by Gemini AI
                }
            } catch (e: Exception) {
                Log.e("GeminiSpeechService", "Gagal memanggil Gemini API, beralih ke mesin artikulasi lokal", e)
            }
        }

        // Fallback ke mesin analitik Bahasa Indonesia internal
        val localAssessment = SpeechAssessmentEngine.evaluateSpeech(
            topic = topic,
            actualSeconds = actualSeconds,
            targetSeconds = targetSeconds,
            hasAudioRecording = hasAudioRecording,
            userNotes = transcriptOrNotes
        )
        return@withContext Pair(localAssessment, false)
    }

    private fun callGeminiApi(
        apiKey: String,
        topic: Topic,
        transcriptOrNotes: String,
        actualSeconds: Int,
        targetSeconds: Int
    ): AiSpeechAssessment? {
        val prompt = """
            Anda adalah pakar retorika, pelatih public speaking, dan linguis Bahasa Indonesia profesional.
            Analisis transkrip latihan berbicara berikut dan berikan evaluasi mendalam dalam Bahasa Indonesia.
            
            Informasi Latihan:
            - Topik: "${topic.title}"
            - Kategori: "${topic.category}"
            - Kutipan Pemikiran: "${topic.quote}"
            - Tantangan Bicara: "${topic.challenge}"
            - Durasi Bicara: $actualSeconds detik (Target: $targetSeconds detik)
            - Transkrip / Catatan Suara: "${if (transcriptOrNotes.isNotBlank()) transcriptOrNotes else "Penyampaian argumen spontan seputar ${topic.title} dengan struktur pembuka, alasan logis, contoh nyata, dan penutup."}"

            Berikan penilaian berdasarkan 4 kriteria utama berikut:
            1. Artikulasi (skor 1-100): Kejelasan pengucapan kata, ketegasan vokal (A-I-U-E-O) dan konsonan akhir, kelenturan artikulasi.
            2. Nada / Intonasi (skor 1-100): Dinamika tinggi-rendah nada (pitch variety), modulasi volume, penekanan emosional, tidak monoton.
            3. Improvisasi (skor 1-100): Kelancaran berpikir spontan, kontrol kata pengisi/filler words, pemanfaatan jeda hening (power of pause), alur PREP.
            4. Wawasan / Insight (skor 1-100): Kedalaman pemikiran, ketajaman analisis kritis, relevansi argumen dengan isu nyata, bobot solusi.
            
            Serta kriteria pendukung Bahasa (skor 1-100) mengenai tata bahasa baku dan kekayaan diksi.
            
            Format balasan HARUS HANYA berupa JSON murni tanpa markdown fence (tanpa ```json) dengan struktur berikut:
            {
              "overallScore": 88,
              "predikat": "Fasih, Terstruktur & Berbobot",
              "skorArtikulasi": 90,
              "catatanArtikulasi": "penjelasan artikulasi...",
              "skorNada": 85,
              "catatanNada": "penjelasan intonasi nada...",
              "skorImprovisasi": 86,
              "catatanImprovisasi": "penjelasan alur improvisasi...",
              "skorWawasan": 91,
              "catatanWawasan": "penjelasan kedalaman wawasan...",
              "skorBahasa": 88,
              "catatanBahasa": "penjelasan bahasa...",
              "kelebihan": ["kelebihan 1", "kelebihan 2", "kelebihan 3"],
              "areaPerbaikan": ["area perbaikan 1", "area perbaikan 2"],
              "saranLatihan": ["saran konkret 1", "saran konkret 2"],
              "ringkasanEksekutif": "Ringkasan penilaian menyeluruh dari Gemini AI..."
            }
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            val contents = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", parts)
                }
                put(contentObj)
            }
            put("contents", contents)
            val genConfig = JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            }
            put("generationConfig", genConfig)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

        // Gunakan model gemini-2.5-flash / gemini-2.0-flash yang cepat dan mutakhir
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.w("GeminiSpeechService", "Gemini HTTP error: ${response.code} ${response.message}")
                return null
            }

            val responseBody = response.body?.string() ?: return null
            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null

            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null

            val text = parts.getJSONObject(0).optString("text", "")
            if (text.isBlank()) return null

            return parseGeminiResponse(text)
        }
    }

    private fun parseGeminiResponse(rawJson: String): AiSpeechAssessment? {
        return try {
            val cleanJson = rawJson.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleanJson)

            val kelebihanArray = json.optJSONArray("kelebihan") ?: JSONArray()
            val kelebihan = (0 until kelebihanArray.length()).map { kelebihanArray.getString(it) }

            val perbaikanArray = json.optJSONArray("areaPerbaikan") ?: JSONArray()
            val perbaikan = (0 until perbaikanArray.length()).map { perbaikanArray.getString(it) }

            val saranArray = json.optJSONArray("saranLatihan") ?: JSONArray()
            val saran = (0 until saranArray.length()).map { saranArray.getString(it) }

            AiSpeechAssessment(
                overallScore = json.optInt("overallScore", 85),
                predikat = json.optString("predikat", "Berbicara dengan Karakter Kuat"),
                skorBahasa = json.optInt("skorBahasa", 85),
                catatanBahasa = json.optString("catatanBahasa", "Tata bahasa efektif dan tertata."),
                skorArtikulasi = json.optInt("skorArtikulasi", 85),
                catatanArtikulasi = json.optString("catatanArtikulasi", "Artikulasi vokal dan konsonan jelas."),
                skorNada = json.optInt("skorNada", 85),
                catatanNada = json.optString("catatanNada", "Variasi intonasi vokal dinamis."),
                skorImprovisasi = json.optInt("skorImprovisasi", 85),
                catatanImprovisasi = json.optString("catatanImprovisasi", "Alur berpikir spontan mengalir."),
                skorWawasan = json.optInt("skorWawasan", 85),
                catatanWawasan = json.optString("catatanWawasan", "Gagasan berbobot dan memiliki sudut pandang tajam."),
                kelebihan = if (kelebihan.isNotEmpty()) kelebihan else listOf("Penyampaian percaya diri", "Struktur argumen rapi"),
                areaPerbaikan = if (perbaikan.isNotEmpty()) perbaikan else listOf("Pertajam jeda hening"),
                saranLatihan = if (saran.isNotEmpty()) saran else listOf("Latihan artikulasi vokal A-I-U-E-O"),
                ringkasanEksekutif = json.optString("ringkasanEksekutif", "Evaluasi Gemini AI: Penyampaian materi sangat baik dan memikat.")
            )
        } catch (e: Exception) {
            Log.e("GeminiSpeechService", "Gagal mem-parsing JSON Gemini", e)
            null
        }
    }
}
