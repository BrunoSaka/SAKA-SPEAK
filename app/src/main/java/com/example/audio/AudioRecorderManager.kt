package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

class AudioRecorderManager(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var currentOutputFile: File? = null

    var isRecording: Boolean = false
        private set

    var isPlaying: Boolean = false
        private set

    fun startRecording(): String? {
        stopRecording()
        stopPlaying()

        try {
            val dir = File(context.filesDir, "speech_recordings")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, "speech_${System.currentTimeMillis()}.m4a")
            currentOutputFile = file

            val mr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mr.setAudioSource(MediaRecorder.AudioSource.MIC)
            mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mr.setAudioEncodingBitRate(128000)
            mr.setAudioSamplingRate(44100)
            mr.setOutputFile(file.absolutePath)
            mr.prepare()
            mr.start()

            recorder = mr
            isRecording = true
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Error starting recording", e)
            recorder?.release()
            recorder = null
            isRecording = false
            return null
        }
    }

    fun getMaxAmplitude(): Int {
        return try {
            if (isRecording) recorder?.maxAmplitude ?: 0 else 0
        } catch (e: Exception) {
            0
        }
    }

    fun stopRecording(): String? {
        if (!isRecording) return currentOutputFile?.absolutePath

        val path = currentOutputFile?.absolutePath
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Error stopping recording", e)
        } finally {
            recorder = null
            isRecording = false
        }
        return path
    }

    fun startPlaying(filePath: String, onCompletion: () -> Unit): Boolean {
        stopPlaying()

        val file = File(filePath)
        if (!file.exists()) return false

        try {
            val mp = MediaPlayer()
            mp.setDataSource(filePath)
            mp.prepare()
            mp.setOnCompletionListener {
                isPlaying = false
                onCompletion()
            }
            mp.start()
            player = mp
            isPlaying = true
            return true
        } catch (e: IOException) {
            Log.e("AudioRecorderManager", "Error playing audio", e)
            player?.release()
            player = null
            isPlaying = false
            return false
        }
    }

    fun pausePlaying() {
        try {
            if (isPlaying && player?.isPlaying == true) {
                player?.pause()
                isPlaying = false
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Error pausing playback", e)
        }
    }

    fun resumePlaying() {
        try {
            if (!isPlaying && player != null) {
                player?.start()
                isPlaying = true
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Error resuming playback", e)
        }
    }

    fun stopPlaying() {
        try {
            player?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Error stopping playback", e)
        } finally {
            player = null
            isPlaying = false
        }
    }

    fun getPlaybackProgress(): Pair<Int, Int> {
        val mp = player ?: return Pair(0, 0)
        return try {
            Pair(mp.currentPosition, mp.duration)
        } catch (e: Exception) {
            Pair(0, 0)
        }
    }

    fun seekTo(positionMs: Int) {
        try {
            player?.seekTo(positionMs)
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Error seeking playback", e)
        }
    }

    fun release() {
        stopRecording()
        stopPlaying()
    }
}
