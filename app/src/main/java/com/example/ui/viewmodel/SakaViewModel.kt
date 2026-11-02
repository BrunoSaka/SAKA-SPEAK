package com.example.ui.viewmodel

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioRecorderManager
import com.example.data.ai.SpeechAssessmentEngine
import com.example.data.local.SakaDatabase
import com.example.data.model.AiSpeechAssessment
import com.example.data.model.PracticeSession
import com.example.data.model.SavedTopicEntity
import com.example.data.model.Topic
import com.example.data.repository.TopicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TimerPhase {
    IDLE,
    PREPARATION, // 30s prep time to structure thoughts
    SPEAKING,    // Main speech countdown
    PAUSED,
    COMPLETED
}

data class CompletedPracticeInfo(
    val topicTitle: String,
    val category: String,
    val targetSeconds: Int,
    val actualSeconds: Int,
    val audioFilePath: String?,
    val aiAssessment: AiSpeechAssessment?
)

class SakaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TopicRepository
    val audioRecorderManager = AudioRecorderManager(application)

    init {
        val dao = SakaDatabase.getDatabase(application).practiceDao()
        repository = TopicRepository(dao)
    }

    val savedTopics: StateFlow<List<Topic>> = repository.savedTopicsFlow
        .combine(MutableStateFlow(Unit)) { entities, _ ->
            entities.map { it.toTopic() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val practiceSessions: StateFlow<List<PracticeSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current topic displayed on practice card (matching HTML initial topic)
    private val _currentTopic = MutableStateFlow(repository.curatedTopics[0])
    val currentTopic: StateFlow<Topic> = _currentTopic.asStateFlow()

    // Selected category filter
    private val _selectedCategory = MutableStateFlow("SEMUA")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories: List<String> = repository.getAllCategories()
    val allCuratedTopics: List<Topic> = repository.curatedTopics

    // Timer and Speech Practice State
    private val _timerPhase = MutableStateFlow(TimerPhase.IDLE)
    val timerPhase: StateFlow<TimerPhase> = _timerPhase.asStateFlow()

    private val _targetSpeechSeconds = MutableStateFlow(120) // Default 2 minutes
    val targetSpeechSeconds: StateFlow<Int> = _targetSpeechSeconds.asStateFlow()

    private val _prepSeconds = MutableStateFlow(30) // 30s prep time
    val prepSeconds: StateFlow<Int> = _prepSeconds.asStateFlow()

    private val _isPrepEnabled = MutableStateFlow(false)
    val isPrepEnabled: StateFlow<Boolean> = _isPrepEnabled.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(120)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    // Voice Recording State within the Timer Screen
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _recordedAudioPath = MutableStateFlow<String?>(null)
    val recordedAudioPath: StateFlow<String?> = _recordedAudioPath.asStateFlow()

    private val _isAudioRecordEnabled = MutableStateFlow(true) // Default to enabled so recording is easily available
    val isAudioRecordEnabled: StateFlow<Boolean> = _isAudioRecordEnabled.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0)
    val audioAmplitude: StateFlow<Int> = _audioAmplitude.asStateFlow()

    private var timerJob: Job? = null
    private var amplitudeJob: Job? = null
    private var actualSecondsSpoken = 0

    private val geminiSpeechService = com.example.data.ai.GeminiSpeechService()

    // AI Speech Articulation Assessment State (Gemini API Integration)
    private val _aiAssessment = MutableStateFlow<AiSpeechAssessment?>(null)
    val aiAssessment: StateFlow<AiSpeechAssessment?> = _aiAssessment.asStateFlow()

    private val _isAnalyzingAi = MutableStateFlow(false)
    val isAnalyzingAi: StateFlow<Boolean> = _isAnalyzingAi.asStateFlow()

    private val _isEvaluatedByGemini = MutableStateFlow(false)
    val isEvaluatedByGemini: StateFlow<Boolean> = _isEvaluatedByGemini.asStateFlow()

    private val _speechTranscript = MutableStateFlow("")
    val speechTranscript: StateFlow<String> = _speechTranscript.asStateFlow()

    // Evaluation modal state
    private val _completedPracticeInfo = MutableStateFlow<CompletedPracticeInfo?>(null)
    val completedPracticeInfo: StateFlow<CompletedPracticeInfo?> = _completedPracticeInfo.asStateFlow()

    // Audio Playback state
    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _currentPlayingPath = MutableStateFlow<String?>(null)
    val currentPlayingPath: StateFlow<String?> = _currentPlayingPath.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private var playbackJob: Job? = null

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun getNextRandomTopic(): Topic {
        val cat = if (_selectedCategory.value == "SEMUA") null else _selectedCategory.value
        return repository.getRandomTopic(cat, savedTopics.value)
    }

    fun randomizeTopic() {
        val cat = if (_selectedCategory.value == "SEMUA") null else _selectedCategory.value
        val newTopic = repository.getRandomTopic(cat, savedTopics.value)
        _currentTopic.value = newTopic
    }

    fun selectSpecificTopic(topic: Topic) {
        _currentTopic.value = topic
    }

    fun toggleFavorite(topic: Topic) {
        viewModelScope.launch {
            val isFav = savedTopics.value.any { it.id == topic.id && it.isFavorite }
            repository.toggleFavorite(topic, isFav)
            if (_currentTopic.value.id == topic.id) {
                _currentTopic.value = _currentTopic.value.copy(isFavorite = !isFav)
            }
        }
    }

    fun addCustomTopic(
        category: String,
        title: String,
        quote: String,
        points: List<String>,
        perspectives: List<String>,
        challenge: String
    ) {
        viewModelScope.launch {
            val newTopic = Topic(
                id = "custom_${System.currentTimeMillis()}",
                category = category.uppercase(),
                title = title,
                quote = quote,
                points = points,
                perspectives = perspectives,
                challenge = challenge,
                isFavorite = true,
                isCustom = true
            )
            repository.saveCustomTopic(newTopic)
            _currentTopic.value = newTopic
        }
    }

    fun deleteSavedTopic(topicId: String) {
        viewModelScope.launch {
            repository.deleteSavedTopic(topicId)
        }
    }

    // Speech Timer controls
    fun setTargetSpeechDuration(seconds: Int) {
        _targetSpeechSeconds.value = seconds
        if (_timerPhase.value == TimerPhase.IDLE) {
            _remainingSeconds.value = seconds
        }
    }

    fun togglePrepEnabled(enabled: Boolean) {
        _isPrepEnabled.value = enabled
    }

    fun toggleAudioRecordEnabled(enabled: Boolean) {
        _isAudioRecordEnabled.value = enabled
    }

    // Direct Voice Recording Controls in Timer Screen
    fun startVoiceRecording() {
        stopAllAudioPlayback()
        val path = audioRecorderManager.startRecording()
        if (path != null) {
            _recordedAudioPath.value = path
            _isRecordingVoice.value = true
            startAmplitudeTracking()
        }
    }

    fun stopVoiceRecording(): String? {
        val path = audioRecorderManager.stopRecording()
        _isRecordingVoice.value = false
        _audioAmplitude.value = 0
        amplitudeJob?.cancel()
        if (path != null) {
            _recordedAudioPath.value = path
        }
        return path
    }

    fun resetVoiceRecording() {
        stopAllAudioPlayback()
        if (_isRecordingVoice.value) {
            audioRecorderManager.stopRecording()
            _isRecordingVoice.value = false
        }
        _recordedAudioPath.value = null
        _aiAssessment.value = null
    }

    fun startSpeechTimer() {
        stopAllAudioPlayback()
        actualSecondsSpoken = 0
        _aiAssessment.value = null

        if (_isPrepEnabled.value) {
            _timerPhase.value = TimerPhase.PREPARATION
            _remainingSeconds.value = _prepSeconds.value
            runPrepTimer()
        } else {
            startSpeakingPhase()
        }
    }

    private fun runPrepTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0 && _timerPhase.value == TimerPhase.PREPARATION) {
                delay(1000)
                _remainingSeconds.value -= 1
            }
            if (_timerPhase.value == TimerPhase.PREPARATION && _remainingSeconds.value <= 0) {
                vibrateDevice(short = true)
                startSpeakingPhase()
            }
        }
    }

    private fun startSpeakingPhase() {
        _timerPhase.value = TimerPhase.SPEAKING
        _remainingSeconds.value = _targetSpeechSeconds.value

        // Auto-start audio recording if enabled
        if (_isAudioRecordEnabled.value) {
            startVoiceRecording()
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0 && _timerPhase.value == TimerPhase.SPEAKING) {
                delay(1000)
                _remainingSeconds.value -= 1
                actualSecondsSpoken += 1

                if (_remainingSeconds.value == 10) {
                    vibrateDevice(short = true)
                }
            }
            if (_timerPhase.value == TimerPhase.SPEAKING && _remainingSeconds.value <= 0) {
                finishSpeechSession(completedNormally = true)
            }
        }
    }

    private fun startAmplitudeTracking() {
        amplitudeJob?.cancel()
        amplitudeJob = viewModelScope.launch {
            while (audioRecorderManager.isRecording) {
                _audioAmplitude.value = audioRecorderManager.getMaxAmplitude()
                delay(100)
            }
            _audioAmplitude.value = 0
        }
    }

    fun pauseTimer() {
        if (_timerPhase.value == TimerPhase.SPEAKING || _timerPhase.value == TimerPhase.PREPARATION) {
            _timerPhase.value = TimerPhase.PAUSED
            timerJob?.cancel()
        }
    }

    fun resumeTimer() {
        if (_timerPhase.value == TimerPhase.PAUSED) {
            _timerPhase.value = TimerPhase.SPEAKING
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_remainingSeconds.value > 0 && _timerPhase.value == TimerPhase.SPEAKING) {
                    delay(1000)
                    _remainingSeconds.value -= 1
                    actualSecondsSpoken += 1
                }
                if (_remainingSeconds.value <= 0) {
                    finishSpeechSession(completedNormally = true)
                }
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        amplitudeJob?.cancel()
        if (_isRecordingVoice.value) {
            stopVoiceRecording()
        }
        _timerPhase.value = TimerPhase.IDLE
        _remainingSeconds.value = _targetSpeechSeconds.value
        actualSecondsSpoken = 0
    }

    fun finishSpeechSession(completedNormally: Boolean) {
        timerJob?.cancel()
        amplitudeJob?.cancel()
        vibrateDevice(short = false)

        val recordedPath = if (_isRecordingVoice.value) {
            stopVoiceRecording()
        } else {
            _recordedAudioPath.value
        }

        _timerPhase.value = TimerPhase.COMPLETED

        // Trigger AI Assessment for Indonesian Language, Articulation, Tone, Improvisation, Insight
        val evaluation = SpeechAssessmentEngine.evaluateSpeech(
            topic = _currentTopic.value,
            actualSeconds = actualSecondsSpoken.coerceAtLeast(10),
            targetSeconds = _targetSpeechSeconds.value,
            hasAudioRecording = !recordedPath.isNullOrBlank(),
            userNotes = ""
        )
        _aiAssessment.value = evaluation

        val sessionInfo = CompletedPracticeInfo(
            topicTitle = _currentTopic.value.title,
            category = _currentTopic.value.category,
            targetSeconds = _targetSpeechSeconds.value,
            actualSeconds = actualSecondsSpoken.coerceAtLeast(1),
            audioFilePath = recordedPath,
            aiAssessment = evaluation
        )
        _completedPracticeInfo.value = sessionInfo
    }

    fun updateSpeechTranscript(transcript: String) {
        _speechTranscript.value = transcript
    }

    fun analyzeDirectSpeechWithAi(notes: String = "") {
        viewModelScope.launch {
            _isAnalyzingAi.value = true
            val transcript = if (notes.isNotBlank()) notes else _speechTranscript.value
            val (evaluation, wasGemini) = geminiSpeechService.evaluateSpeech(
                topic = _currentTopic.value,
                transcriptOrNotes = transcript,
                actualSeconds = actualSecondsSpoken.coerceAtLeast(15),
                targetSeconds = _targetSpeechSeconds.value,
                hasAudioRecording = !_recordedAudioPath.value.isNullOrBlank()
            )
            _aiAssessment.value = evaluation
            _isEvaluatedByGemini.value = wasGemini
            _isAnalyzingAi.value = false
        }
    }

    fun get100kCatalogPage(
        page: Int,
        pageSize: Int = 30,
        type: com.example.data.repository.CatalogType = com.example.data.repository.CatalogType.ALL,
        category: String = "SEMUA",
        query: String = ""
    ): List<com.example.data.repository.CatalogItem> {
        return com.example.data.repository.CatalogBank100k.getPage(
            page = page,
            pageSize = pageSize,
            typeFilter = type,
            categoryFilter = category,
            searchQuery = query
        )
    }

    fun closeTimerScreen() {
        resetTimer()
    }

    fun dismissEvaluationDialog() {
        _completedPracticeInfo.value = null
        stopAllAudioPlayback()
        _timerPhase.value = TimerPhase.IDLE
    }

    fun saveCompletedSession(
        ratingStructure: Int,
        ratingClarity: Int,
        ratingPacing: Int,
        ratingConfidence: Int,
        notes: String
    ) {
        val info = _completedPracticeInfo.value ?: return
        val ai = _aiAssessment.value
        viewModelScope.launch {
            val session = PracticeSession(
                topicTitle = info.topicTitle,
                category = info.category,
                targetSeconds = info.targetSeconds,
                actualSeconds = info.actualSeconds,
                audioFilePath = info.audioFilePath,
                ratingStructure = ratingStructure,
                ratingClarity = ratingClarity,
                ratingPacing = ratingPacing,
                ratingConfidence = ratingConfidence,
                notes = notes,
                aiOverallScore = ai?.overallScore,
                aiScoreBahasa = ai?.skorBahasa,
                aiScoreArtikulasi = ai?.skorArtikulasi,
                aiScoreNada = ai?.skorNada,
                aiScoreImprovisasi = ai?.skorImprovisasi,
                aiScoreWawasan = ai?.skorWawasan,
                aiFeedbackSummary = ai?.ringkasanEksekutif
            )
            repository.saveSession(session)
            dismissEvaluationDialog()
        }
    }

    fun deletePracticeSession(session: PracticeSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }

    // Audio Playback
    fun playAudio(path: String) {
        stopAllAudioPlayback()
        _currentPlayingPath.value = path
        val started = audioRecorderManager.startPlaying(path) {
            _isPlayingAudio.value = false
            _playbackProgress.value = 0f
            _currentPlayingPath.value = null
            playbackJob?.cancel()
        }
        if (started) {
            _isPlayingAudio.value = true
            trackPlaybackProgress()
        }
    }

    fun pauseAudio() {
        audioRecorderManager.pausePlaying()
        _isPlayingAudio.value = false
    }

    fun stopAllAudioPlayback() {
        playbackJob?.cancel()
        audioRecorderManager.stopPlaying()
        _isPlayingAudio.value = false
        _currentPlayingPath.value = null
        _playbackProgress.value = 0f
    }

    private fun trackPlaybackProgress() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_isPlayingAudio.value) {
                val (current, duration) = audioRecorderManager.getPlaybackProgress()
                if (duration > 0) {
                    _playbackProgress.value = current.toFloat() / duration.toFloat()
                }
                delay(200)
            }
        }
    }

    private fun vibrateDevice(short: Boolean) {
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(VibratorManager::class.java)
                val vibrator = vibratorManager.defaultVibrator
                val effect = if (short) {
                    VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
                } else {
                    VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 300), -1)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Vibrator::class.java)
                if (short) {
                    vibrator?.vibrate(120)
                } else {
                    vibrator?.vibrate(longArrayOf(0, 200, 100, 300), -1)
                }
            }
        } catch (_: Exception) {
            // Non-critical vibration fallback
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorderManager.release()
    }
}
