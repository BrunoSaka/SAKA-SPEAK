package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PracticeSession
import com.example.ui.components.MegaMendungBackground
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
import com.example.ui.theme.SakaMatchaLight
import com.example.ui.theme.SakaTagBg
import com.example.ui.viewmodel.SakaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: SakaViewModel,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.practiceSessions.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val currentPlayingPath by viewModel.currentPlayingPath.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()

    val totalSessions = sessions.size
    val totalSecondsSpoken = sessions.sumOf { it.actualSeconds }
    val totalMinutes = totalSecondsSpoken / 60
    val avgScore = if (sessions.isNotEmpty()) {
        val totalStars = sessions.sumOf { it.ratingStructure + it.ratingClarity + it.ratingPacing + it.ratingConfidence }
        val count = sessions.size * 4
        String.format(Locale.US, "%.1f", totalStars.toFloat() / count.toFloat())
    } else {
        "0.0"
    }

    Box(modifier = modifier.fillMaxSize()) {
        MegaMendungBackground(alpha = 0.04f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Text(
                text = "LOG & RIWAYAT LATIHAN",
                color = SakaMatchaLight,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Perjalanan Bicaramu",
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                color = SakaGoldLight,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Sesi",
                    value = "$totalSessions",
                    subtitle = "Latihan",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Waktu Bicara",
                    value = "$totalMinutes",
                    subtitle = "Menit",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Rata-rata Skor",
                    value = "$avgScore",
                    subtitle = "Dari 5.0",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sessions List
            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎙️", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Belum ada riwayat latihan.",
                            color = SakaCream,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mulai latihan pertamamu di halaman utama untuk melihat catatan evaluasi.",
                            color = SakaCreamDim,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 30.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("history_session_list"),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(sessions, key = { it.id }) { session ->
                        val isCurrentPlaying = isPlayingAudio && currentPlayingPath == session.audioFilePath
                        SessionHistoryCard(
                            session = session,
                            isPlaying = isCurrentPlaying,
                            playbackProgress = if (isCurrentPlaying) playbackProgress else 0f,
                            onPlayAudio = { path ->
                                if (isCurrentPlaying) {
                                    viewModel.pauseAudio()
                                } else {
                                    viewModel.playAudio(path)
                                }
                            },
                            onDelete = { viewModel.deletePracticeSession(session) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, SakaBorderGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SakaDarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = SakaCreamDim,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SakaGold
            )
            Text(
                text = subtitle,
                color = SakaMatchaLight,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun SessionHistoryCard(
    session: PracticeSession,
    isPlaying: Boolean,
    playbackProgress: Float,
    onPlayAudio: (String) -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(session.timestamp))
    val minutes = session.actualSeconds / 60
    val seconds = session.actualSeconds % 60
    val durationText = String.format("%02d:%02d", minutes, seconds)

    val avgScore = (session.ratingStructure + session.ratingClarity + session.ratingPacing + session.ratingConfidence) / 4f
    val formattedAvg = String.format(Locale.US, "%.1f", avgScore)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SakaBorderGold.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SakaDarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category, Date, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SakaTagBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = session.category,
                            color = SakaMatchaLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedDate,
                        color = SakaCreamMuted,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus Riwayat",
                        tint = SakaCreamDim.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Topic Title
            Text(
                text = session.topicTitle,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SakaGoldLight,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Scores & Duration metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = SakaGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$formattedAvg / 5.0",
                        color = SakaGold,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Durasi: $durationText",
                    color = SakaCreamDim,
                    fontSize = 12.sp
                )
            }

            // Audio Player Bar if recording exists
            if (!session.audioFilePath.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
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
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SakaGold)
                                .clickable { onPlayAudio(session.audioFilePath) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Putar",
                                tint = SakaDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isPlaying) "Memutar rekaman suara..." else "Rekaman Latihan",
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
                                modifier = Modifier.height(18.dp)
                            )
                        }
                    }
                }
            }

            // Speaker's Reflection Notes
            if (session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SakaCream.copy(alpha = 0.04f))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Refleksi: \"${session.notes}\"",
                        color = SakaCreamDim,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // AI Articulation Assessment Highlights
            if (session.aiOverallScore != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SakaDarkSoft)
                        .border(1.dp, SakaBorderGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = SakaGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Evaluasi Artikulasi AI",
                                    color = SakaGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Skor: ${session.aiOverallScore}/100",
                                color = SakaGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 5 parameter pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bahasa: ${session.aiScoreBahasa ?: "-"}", color = SakaCreamDim, fontSize = 10.sp)
                            Text("Artikulasi: ${session.aiScoreArtikulasi ?: "-"}", color = SakaCreamDim, fontSize = 10.sp)
                            Text("Nada: ${session.aiScoreNada ?: "-"}", color = SakaCreamDim, fontSize = 10.sp)
                            Text("Improvisasi: ${session.aiScoreImprovisasi ?: "-"}", color = SakaCreamDim, fontSize = 10.sp)
                            Text("Wawasan: ${session.aiScoreWawasan ?: "-"}", color = SakaCreamDim, fontSize = 10.sp)
                        }

                        if (!session.aiFeedbackSummary.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = session.aiFeedbackSummary,
                                color = SakaCreamMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
