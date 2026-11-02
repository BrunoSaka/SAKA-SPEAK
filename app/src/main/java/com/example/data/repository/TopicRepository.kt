package com.example.data.repository

import com.example.data.local.PracticeDao
import com.example.data.model.PracticeSession
import com.example.data.model.SavedTopicEntity
import com.example.data.model.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.random.Random

class TopicRepository(private val practiceDao: PracticeDao) {

    val allSessions: Flow<List<PracticeSession>> = practiceDao.getAllSessions()
    val savedTopicsFlow: Flow<List<SavedTopicEntity>> = practiceDao.getAllSavedTopics()

    // Base Curated Topics directly matching and expanding the user's HTML project
    val curatedTopics: List<Topic> = listOf(
        Topic(
            id = "curated_1",
            category = "LINGKUNGAN",
            title = "Karhutla & Transisi Energi",
            quote = "Api tidak selalu muncul karena alam. Kadang, keputusan manusia menjadi percikan yang dampaknya jauh lebih besar.",
            points = listOf(
                "Kebakaran hutan berdampak langsung pada kualitas udara global.",
                "Kerusakan ekosistem membutuhkan waktu puluhan tahun untuk pulih.",
                "Dilema antara ekspansi industri vs kelestarian lingkungan."
            ),
            perspectives = listOf("Lingkungan dan ekosistem", "Kesehatan masyarakat", "Pertumbuhan Ekonomi"),
            challenge = "Dalam 2 menit, jelaskan mengapa pencegahan kebakaran hutan harus menjadi prioritas di atas keuntungan ekonomi jangka pendek."
        ),
        Topic(
            id = "curated_2",
            category = "TEKNOLOGI & AI",
            title = "Otomatisasi Pekerjaan & Otak Manusia",
            quote = "Teknologi tidak menggantikan manusia. Manusia yang memanfaatkan teknologi lah yang memimpin masa depan.",
            points = listOf(
                "AI mengikis keterampilan kritis jika manusia terlalu bergantung.",
                "Banyak profesi tradisional yang mulai digantikan algoritma.",
                "Etika dan privasi data semakin sulit dikontrol."
            ),
            perspectives = listOf("Pendidikan & Talenta Muda", "Dunia Kerja & Bisnis", "Etika & Regulasi"),
            challenge = "Apakah AI membuat manusia semakin cerdas atau justru malas berpikir? Sampaikan argumenmu secara tegas dalam 2 menit!"
        ),
        Topic(
            id = "curated_3",
            category = "SOSIAL & MEDIA",
            title = "Budaya Fast Content & Fenomena Brainrot",
            quote = "Di dunia yang menghargai kecepatan konten, kedalaman berpikir menjadi barang mewah yang langka.",
            points = listOf(
                "Rentang perhatian (attention span) manusia menurun drastis.",
                "Informasi dangkal lebih mudah viral dibanding analisis mendalam.",
                "Dampak isolasi sosial di balik layar smartphone."
            ),
            perspectives = listOf("Psikologi Remaja", "Industri Kreatif", "Kesehatan Mental"),
            challenge = "Bagaimana cara generasi muda membentengi diri dari paparan konten dangkal agar tetap memiliki daya pikir kritis?"
        ),
        Topic(
            id = "curated_4",
            category = "FILSAFAT & ETIKA",
            title = "Kebebasan Berpendapat vs Hate Speech",
            quote = "Batas kebebasanmu adalah ketika kebebasan itu mulai merampas hak dan kenyamanan orang lain.",
            points = listOf(
                "Batas tipis antara kritik membangun dan ujaran kebencian.",
                "Buzzer dan manipulasi opini publik di media sosial.",
                "Sensorship pemerintah vs kebebasan berekspresi individu."
            ),
            perspectives = listOf("Hukum & Hak Asasi", "Sosiologi Digital", "Moralitas Komunitas"),
            challenge = "Apakah semua pendapat layak untuk didengarkan di ruang publik? Sampaikan sudut pandangmu dalam 2 menit."
        ),
        Topic(
            id = "curated_5",
            category = "EKONOMI & FINANSIAL",
            title = "FOMO Finansial & Gaya Hidup Instan",
            quote = "Kekayaan sejati dibangun dari konsistensi, bukan dari dorongan impulsif mengejar pengakuan instan.",
            points = listOf(
                "Kemudahan akses pinjol dan tren flexing merusak finansial anak muda.",
                "Tekanan sosial untuk terlihat sukses di media sosial.",
                "Pentingnya literasi keuangan sejak tingkat sekolah."
            ),
            perspectives = listOf("Ekonomi Makro", "Kesehatan Mental", "Pendidikan Keluarga"),
            challenge = "Apa langkah paling konkret yang harus dilakukan seorang anak muda untuk keluar dari jebakan finansial modern?"
        ),
        Topic(
            id = "curated_6",
            category = "PENDIDIKAN MODERN",
            title = "Kecerdasan Emosional vs Standarisasi Nilai",
            quote = "Pendidikan bukan tentang mengisi bejana yang kosong, melainkan menyalakan api rasa ingin tahu.",
            points = listOf(
                "Nilai akademis tidak lagi menjadi jaminan sukses di era kolaboratif.",
                "Kesehatan mental pelajar kerap terabaikan demi ranking.",
                "Kebutuhan soft skill, komunikasi, dan resiliensi moral."
            ),
            perspectives = listOf("Sistem Kurikulum", "Psikologi Anak", "Dunia Industri"),
            challenge = "Jika Anda menjadi Menteri Pendidikan selama sehari, regulasi apa yang paling pertama Anda rombak demi kesehatan mental pelajar?"
        ),
        Topic(
            id = "curated_7",
            category = "PSIKOLOGI MANUSIA",
            title = "Burnout & Glorifikasi Sibuk",
            quote = "Berhenti sejenak bukanlah kekalahan; itu adalah persiapan untuk melangkah lebih jauh tanpa kehilangan diri sendiri.",
            points = listOf(
                "Hustle culture sering memicu ilusi produktivitas semu.",
                "Sulitnya menetapkan batasan antara jam kerja dan istirahat.",
                "Dampak fisik dan emosional akibat kelelahan kronis."
            ),
            perspectives = listOf("Keseimbangan Hidup", "Kultur Perusahaan", "Keluarga & Relasi"),
            challenge = "Bagaimana Anda meyakinkan rekan kerja bahwa istirahat adalah bagian esensial dari kinerja profesional?"
        ),
        Topic(
            id = "curated_8",
            category = "BUDAYA GLOBAL",
            title = "Identitas Lokal di Tengah Globalisasi",
            quote = "Pohon yang akarnya kokoh tidak akan pernah gentar diterpa angin dari arah manapun.",
            points = listOf(
                "Ancaman kepunahan bahasa daerah dan kesenian tradisional.",
                "Peluang memperkenalkan warisan budaya lewat platform digital.",
                "Keseimbangan antara modernitas dan penghormatan leluhur."
            ),
            perspectives = listOf("Generasi Muda", "Pelestarian Budaya", "Diplomasi Internasional"),
            challenge = "Mengapa generasi muda tetap harus bangga memegang tradisi lokal saat bersaing di kancah internasional?"
        )
    )

    // Procedural Generator Components (from user's HTML/JS logic)
    private val proceduralCategories = listOf(
        "FILSAFAT ETIKA", "TEKNOLOGI AI", "EKONOMI SOSIAL", "POLITIK HUKUM",
        "PSIKOLOGI MANUSIA", "PENDIDIKAN MODERN", "BUDAYA GLOBAL", "KESEHATAN MENTAL"
    )

    private val proceduralSubjects = listOf(
        "Gen Z & Dunia Kerja", "Sistem Pendidikan Berbasis Nilai", "Penggunaan AI di Sekolah",
        "Budaya FOMO & Materialisme", "Krisis Iklim & Industri Hijau", "Privasi Data Pribadi",
        "Standardisasi Kecantikan Digital", "Kebebasan Akademik & Debat", "Ketergantungan Game Online",
        "Pengaruh Media Sosial pada Remaja", "Urbanisasi & Ketimpangan Sosial", "Cancel Culture & Rekonsiliasi",
        "Kecerdasan Buatan dalam Seni", "Etika Kerja Fleksibel & WFH", "Krisis Empati di Ruang Publik"
    )

    private val quotesDb = listOf(
        "Kita tidak bisa memecahkan masalah dengan tingkat berpikir yang sama saat kita menciptakannya.",
        "Yang paling berbahaya bukanlah kebodohan, melainkan ilusi bahwa kita sudah tahu segalanya.",
        "Keberanian bukan berarti tidak takut, melainkan bertindak meskipun rasa takut itu ada.",
        "Suara yang paling nyaring belum tentu membawa kebenaran yang paling jernih.",
        "Kecepatan tanpa arah adalah awal dari kehancuran.",
        "Kata-kata yang diucapkan dengan integritas mampu mengubah jalannya sejarah.",
        "Mendengarkan dengan sungguh-sungguh adalah bentuk penghormatan tertinggi dalam berbicara."
    )

    private val pointTemplates = listOf(
        listOf(
            "Mengubah persepsi masyarakat secara fundamental.",
            "Memunculkan dilema moral yang sulit dihindari.",
            "Membutuhkan regulasi dan kesadaran kolektif yang matang."
        ),
        listOf(
            "Dampak jangka panjang bagi generasi mendatang.",
            "Pertentangan antara efisiensi mesin vs nilai kemanusiaan.",
            "Peran individu dalam menentukan arah perubahan moral."
        ),
        listOf(
            "Risiko kepunahan nilai-nilai lokal di era serba cepat.",
            "Tekanan adaptasi di tengah percepatan teknologi modern.",
            "Tanggung jawab etis para pemangku kebijakan publik."
        ),
        listOf(
            "Pentingnya transparansi dan akuntabilitas informasi.",
            "Bahaya polarisasi akibat algoritma ruang gema (echo chamber).",
            "Membangun jembatan komunikasi antar kelompok yang berseberangan."
        )
    )

    private val perspectivesDb = listOf(
        listOf("Perspektif Moral & Etika", "Ekonomi & Keberlanjutan", "Kesejahteraan Sosial"),
        listOf("Hukum & Kebijakan", "Psikologi Individu", "Dampak Lingkungan"),
        listOf("Teknologi & Inovasi", "Pendidikan Karakter", "Identitas Budaya"),
        listOf("Kesehatan Mental", "Komunitas & Solidaritas", "Dunia Profesional")
    )

    private val challengeTemplates = listOf(
        "Bagaimana Anda menyikapi pertentangan ini jika berada di posisi pengambil keputusan utama? Jelaskan argumen kritis Anda!",
        "Jika Anda diberi waktu 2 menit untuk meyakinkan audiens yang tidak setuju dengan Anda, poin utama apa yang akan Anda sampaikan?",
        "Uraikan masalah mendasar dari topik ini serta tawaran solusi inovatif yang konkret dan berdampak nyata!",
        "Sampaikan sebuah analogi kuat dalam 2 menit yang mampu membuka pikiran audiens tentang urgensi masalah ini!"
    )

    fun generateProceduralTopic(categoryFilter: String? = null): Topic {
        val cat = if (!categoryFilter.isNullOrBlank() && categoryFilter != "SEMUA") {
            categoryFilter
        } else {
            proceduralCategories.random()
        }
        val subj = proceduralSubjects.random()
        val quote = quotesDb.random()
        val points = pointTemplates.random()
        val pers = perspectivesDb.random()
        val challengeEnd = challengeTemplates.random()

        return Topic(
            id = "procedural_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}",
            category = cat,
            title = subj,
            quote = quote,
            points = points,
            perspectives = pers,
            challenge = "Topik Kritis: Bicara tentang \"$subj\". $challengeEnd",
            isFavorite = false,
            isCustom = false
        )
    }

    fun getRandomTopic(categoryFilter: String? = null, savedTopics: List<Topic> = emptyList()): Topic {
        // Pool of candidates: curated + custom/saved
        val filteredCurated = if (!categoryFilter.isNullOrBlank() && categoryFilter != "SEMUA") {
            curatedTopics.filter { it.category.equals(categoryFilter, ignoreCase = true) }
        } else {
            curatedTopics
        }

        val filteredSaved = if (!categoryFilter.isNullOrBlank() && categoryFilter != "SEMUA") {
            savedTopics.filter { it.category.equals(categoryFilter, ignoreCase = true) }
        } else {
            savedTopics
        }

        val combined = filteredCurated + filteredSaved

        // 50% probability to use handcrafted/saved, 50% to generate procedural topic
        return if (combined.isNotEmpty() && Random.nextBoolean()) {
            combined.random()
        } else {
            generateProceduralTopic(categoryFilter)
        }
    }

    suspend fun toggleFavorite(topic: Topic, currentlyFavorited: Boolean) {
        val updated = topic.copy(isFavorite = !currentlyFavorited)
        practiceDao.insertSavedTopic(SavedTopicEntity.fromTopic(updated))
    }

    suspend fun saveCustomTopic(topic: Topic) {
        practiceDao.insertSavedTopic(SavedTopicEntity.fromTopic(topic))
    }

    suspend fun deleteSavedTopic(id: String) {
        practiceDao.deleteSavedTopic(id)
    }

    suspend fun saveSession(session: PracticeSession): Long {
        return practiceDao.insertSession(session)
    }

    suspend fun deleteSession(session: PracticeSession) {
        practiceDao.deleteSession(session)
    }

    fun getAllCategories(): List<String> {
        val set = mutableSetOf("SEMUA")
        curatedTopics.forEach { set.add(it.category) }
        proceduralCategories.forEach { set.add(it) }
        return set.toList()
    }
}
