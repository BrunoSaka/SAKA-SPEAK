package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.AiAssessmentCard
import com.example.ui.components.MegaMendungBackground
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.SakaBorderGold
import com.example.ui.theme.SakaBorderSubtle
import com.example.ui.theme.SakaCream
import com.example.ui.theme.SakaCreamDim
import com.example.ui.theme.SakaCreamMuted
import com.example.ui.theme.SakaDark
import com.example.ui.theme.SakaDarkCard
import com.example.ui.theme.SakaDarkSoft
import com.example.ui.theme.SakaGold
import com.example.ui.theme.SakaGoldLight
import com.example.ui.theme.SakaMatcha
import com.example.ui.theme.SakaMatchaDark
import com.example.ui.theme.SakaMatchaLight
import com.example.ui.theme.SakaRed
import com.example.ui.theme.SakaWhite
import com.example.ui.viewmodel.SakaViewModel
import com.example.ui.viewmodel.TimerPhase

@Composable
fun TimerScreen(
    viewModel: SakaViewModel,
    onCloseTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topic by viewModel.currentTopic.collectAsState()
    val timerPhase by viewModel.timerPhase.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val targetSeconds by viewModel.targetSpeechSeconds.collectAsState()
    val prepSeconds by viewModel.prepSeconds.collectAsState()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val recordedAudioPath by viewModel.recordedAudioPath.collectAsState()
    val audioAmplitude by viewModel.audioAmplitude.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()
    val aiAssessment by viewModel.aiAssessment.collectAsState()
    val isAnalyzingAi by viewModel.isAnalyzingAi.collectAsState()

    var showFrameworkHelper by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceRecording()
        }
    }

    BackHandler {
        viewModel.closeTimerScreen()
        onCloseTimer()
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val isDanger = remainingSeconds in 1..10 && timerPhase == TimerPhase.SPEAKING
    val isPreparation = timerPhase == TimerPhase.PREPARATION

    val totalSecondsForPhase = if (isPreparation) prepSeconds else targetSeconds
    val progress = (remainingSeconds.toFloat() / totalSecondsForPhase.toFloat()).coerceIn(0f, 1f)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = SakaDark
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MegaMendungBackground(alpha = 0.05f)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header / Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isRecordingVoice -> SakaRed
                                        timerPhase == TimerPhase.PREPARATION -> SakaMatchaLight
                                        timerPhase == TimerPhase.SPEAKING -> if (isDanger) SakaRed else SakaGold
                                        timerPhase == TimerPhase.PAUSED -> SakaCreamDim
                                        else -> SakaGold
                                    }
                                )
                        )
                        Text(
                            text = when {
                                isRecordingVoice -> "SEDANG MEREKAM SUARA"
                                timerPhase == TimerPhase.PREPARATION -> "FASE PERSIAPAN (BERPIKIR)"
                                timerPhase == TimerPhase.SPEAKING -> "WAKTU BERBICARA"
                                timerPhase == TimerPhase.PAUSED -> "LATIHAN DIJEDA"
                                else -> "SELESAI LATIHAN"
                            },
                            color = SakaCreamDim,
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showFrameworkHelper = !showFrameworkHelper },
                            modifier = Modifier.testTag("framework_helper_button")
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Panduan PREP",
                                tint = SakaGoldLight
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.closeTimerScreen()
                                onCloseTimer()
                            },
                            modifier = Modifier.testTag("close_timer_button")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Tutup Timer",
                                tint = SakaCreamDim
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Topic information
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = topic.category,
                        color = SakaMatchaLight,
                        fontSize = 11.5.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("timer_category")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = topic.title,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SakaGoldLight,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp,
                        modifier = Modifier.testTag("timer_title")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = topic.challenge,
                        color = SakaCream.copy(alpha = 0.85f),
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Timer Display Circle & Giant Digits
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .testTag("timer_circular_display"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 7.dp.toPx()
                        drawCircle(
                            color = SakaDarkCard,
                            style = Stroke(width = strokeWidth)
                        )
                        val arcColor = when {
                            isDanger -> SakaRed
                            isPreparation -> SakaMatchaLight
                            else -> SakaGold
                        }
                        drawArc(
                            color = arcColor,
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formattedTime,
                            fontFamily = FontFamily.Serif,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isDanger -> SakaRed
                                isPreparation -> SakaMatchaLight
                                else -> SakaGold
                            },
                            letterSpacing = 2.sp,
                            modifier = Modifier
                                .testTag("timer_display_text")
                                .alpha(if (isDanger) pulseAlpha else 1f)
                        )

                        Text(
                            text = if (isPreparation) "Persiapkan Argumen" else "Sisa Waktu Bicara",
                            color = SakaCreamDim,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Voice Recording & In-Timer Playback Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isRecordingVoice) SakaRed else SakaBorderGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SakaDarkCard)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Status Bar: Recording indicator / Playback availability
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isRecordingVoice) SakaRed else SakaGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isRecordingVoice) "Sedang Merekam Suara..." else if (recordedAudioPath != null) "Suara Telah Terekam" else "Perekam Suara Latihan",
                                    color = if (isRecordingVoice) SakaRed else SakaGoldLight,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Record / Stop Record Button
                            Button(
                                onClick = {
                                    if (isRecordingVoice) {
                                        viewModel.stopVoiceRecording()
                                    } else {
                                        val hasPermission = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.RECORD_AUDIO
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (hasPermission) {
                                            viewModel.startVoiceRecording()
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecordingVoice) SakaRed else SakaGold,
                                    contentColor = if (isRecordingVoice) SakaWhite else SakaDark
                                ),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("in_timer_record_button")
                            ) {
                                Icon(
                                    imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isRecordingVoice) "Stop Rekam" else "Mulai Rekam",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Live Waveform Visualizer
                        if (isRecordingVoice) {
                            Spacer(modifier = Modifier.height(10.dp))
                            WaveformVisualizer(
                                isRecording = true,
                                amplitude = audioAmplitude,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // In-Timer Audio Playback Bar
                        if (recordedAudioPath != null && !isRecordingVoice) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SakaDarkSoft),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(SakaGold)
                                            .clickable {
                                                if (isPlayingAudio) {
                                                    viewModel.pauseAudio()
                                                } else {
                                                    viewModel.playAudio(recordedAudioPath!!)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlayingAudio) "Jeda" else "Putar",
                                            tint = SakaDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isPlayingAudio) "Memutar rekaman latihan..." else "Dengarkan hasil suaramu",
                                            color = SakaCream,
                                            fontSize = 11.5.sp
                                        )
                                        Slider(
                                            value = playbackProgress,
                                            onValueChange = {},
                                            colors = SliderDefaults.colors(
                                                thumbColor = SakaGold,
                                                activeTrackColor = SakaGold,
                                                inactiveTrackColor = SakaDarkCard
                                            ),
                                            modifier = Modifier.height(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.resetVoiceRecording() },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Refresh,
                                            contentDescription = "Rekam Ulang",
                                            tint = SakaCreamDim,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Field Transkrip / Poin yang diucapkan
                            var localTranscript by remember { mutableStateOf(viewModel.speechTranscript.value) }
                            androidx.compose.material3.OutlinedTextField(
                                value = localTranscript,
                                onValueChange = {
                                    localTranscript = it
                                    viewModel.updateSpeechTranscript(it)
                                },
                                label = { Text("Transkrip / Ringkasan yang Anda Bicarakan", fontSize = 11.5.sp) },
                                placeholder = {
                                    Text(
                                        "Tulis apa yang Anda ucapkan (opsional) agar Gemini AI menilai artikulasi, nada, improvisasi & wawasan lebih tajam...",
                                        fontSize = 11.5.sp,
                                        color = SakaCreamDim.copy(alpha = 0.6f)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                shape = RoundedCornerShape(12.dp),
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SakaGold,
                                    unfocusedBorderColor = SakaBorderGold.copy(alpha = 0.3f),
                                    focusedContainerColor = SakaDarkSoft,
                                    unfocusedContainerColor = SakaDarkSoft,
                                    focusedTextColor = SakaCream,
                                    unfocusedTextColor = SakaCream,
                                    focusedLabelColor = SakaGoldLight,
                                    unfocusedLabelColor = SakaCreamDim
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Tombol Evaluasi AI Artikulasi Bahasa Indonesia (Gemini API)
                            Button(
                                onClick = { viewModel.analyzeDirectSpeechWithAi(localTranscript) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("ai_assess_speech_button"),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SakaMatcha,
                                    contentColor = SakaWhite
                                ),
                                enabled = !isAnalyzingAi
                            ) {
                                if (isAnalyzingAi) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = SakaWhite,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Gemini AI Sedang Menilai Artikulasi...", fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("✦ ANALISIS DENGAN GEMINI AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // AI Assessment Result Card in Timer Screen
                val isEvaluatedByGemini by viewModel.isEvaluatedByGemini.collectAsState()
                if (aiAssessment != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    AiAssessmentCard(
                        assessment = aiAssessment!!,
                        isGemini = isEvaluatedByGemini
                    )
                }

                // Framework Cheat Sheet Helper Card
                AnimatedVisibility(
                    visible = showFrameworkHelper,
                    enter = expandVertically(
                        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeIn(animationSpec = tween(220)),
                    exit = shrinkVertically(
                        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeOut(animationSpec = tween(180))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = SakaDarkCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderGold)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Formula PREP Cepat:",
                                color = SakaGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Point: Sampaikan inti pesanmu langsung.\n2. Reason: Jelaskan alasan utama kenapa hal itu penting.\n3. Example: Beri contoh nyata / analogi sederhana.\n4. Point: Simpulkan dan tutup dengan tegas.",
                                color = SakaCream,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Control Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (timerPhase == TimerPhase.PAUSED) {
                                    viewModel.resumeTimer()
                                } else {
                                    viewModel.pauseTimer()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("toggle_pause_button"),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderGold)
                        ) {
                            Icon(
                                imageVector = if (timerPhase == TimerPhase.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                tint = SakaCream
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (timerPhase == TimerPhase.PAUSED) "Lanjut" else "Jeda",
                                color = SakaCream,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("reset_timer_button"),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderSubtle)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = SakaCreamDim)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", color = SakaCreamDim)
                        }
                    }

                    // Selesai Latihan Button
                    Button(
                        onClick = {
                            viewModel.finishSpeechSession(completedNormally = false)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("finish_speech_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SakaGold,
                            contentColor = SakaDark
                        )
                    ) {
                        Text(
                            text = "✦ SELESAI & SIMPAN LATIHAN",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            fontSize = 13.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
