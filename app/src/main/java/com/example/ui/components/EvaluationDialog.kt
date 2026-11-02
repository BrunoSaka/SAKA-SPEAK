package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.SakaBorderGold
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
import com.example.ui.theme.SakaWhite

@Composable
fun EvaluationDialog(
    topicTitle: String,
    category: String,
    actualSeconds: Int,
    targetSeconds: Int,
    audioFilePath: String?,
    aiAssessment: com.example.data.model.AiSpeechAssessment?,
    isPlayingAudio: Boolean,
    audioPlaybackProgress: Float, // 0f to 1f
    onPlayAudio: (String) -> Unit,
    onPauseAudio: () -> Unit,
    onSave: (ratingStructure: Int, ratingClarity: Int, ratingPacing: Int, ratingConfidence: Int, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var ratingStructure by remember { mutableIntStateOf(4) }
    var ratingClarity by remember { mutableIntStateOf(4) }
    var ratingPacing by remember { mutableIntStateOf(4) }
    var ratingConfidence by remember { mutableIntStateOf(4) }
    var notes by remember { mutableStateOf("") }

    val formattedActual = String.format("%02d:%02d", actualSeconds / 60, actualSeconds % 60)
    val formattedTarget = String.format("%02d:%02d", targetSeconds / 60, targetSeconds % 60)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, SakaBorderGold, RoundedCornerShape(24.dp)),
            color = SakaDarkSoft
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "EVALUASI DIRI MANDIRI",
                            color = SakaMatchaLight,
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Latihan Selesai!",
                            color = SakaGoldLight,
                            fontFamily = FontFamily.Serif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_eval_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = SakaCreamDim)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Topic summary card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SakaDarkCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = topicTitle,
                            color = SakaCream,
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kategori: $category",
                                color = SakaMatchaLight,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Durasi: $formattedActual / $formattedTarget",
                                color = SakaGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Audio Playback Section (if recording was enabled)
                if (!audioFilePath.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Dengarkan Rekaman Suara Anda",
                        color = SakaGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SakaDarkCard),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SakaBorderGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SakaGold)
                                    .clickable {
                                        if (isPlayingAudio) {
                                            onPauseAudio()
                                        } else {
                                            onPlayAudio(audioFilePath)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlayingAudio) "Jeda" else "Putar",
                                    tint = SakaDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isPlayingAudio) "Memutar rekaman..." else "Klik putar untuk dengarkan artikulasi",
                                    color = SakaCream,
                                    fontSize = 12.sp
                                )
                                Slider(
                                    value = audioPlaybackProgress,
                                    onValueChange = {},
                                    colors = SliderDefaults.colors(
                                        thumbColor = SakaGold,
                                        activeTrackColor = SakaGold,
                                        inactiveTrackColor = SakaDark
                                    ),
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }
                    }
                }

                if (aiAssessment != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AiAssessmentCard(assessment = aiAssessment)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Rubric Rating Sections
                Text(
                    text = "Refleksi & Penilaian Mandiri (1 - 5 Bintang)",
                    color = SakaCream,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                RatingRow(
                    label = "Struktur & Alur Argumen (PREP)",
                    score = ratingStructure,
                    onScoreChanged = { ratingStructure = it }
                )

                RatingRow(
                    label = "Kejelasan Suara & Artikulasi",
                    score = ratingClarity,
                    onScoreChanged = { ratingClarity = it }
                )

                RatingRow(
                    label = "Manajemen Waktu & Jeda Bicara",
                    score = ratingPacing,
                    onScoreChanged = { ratingPacing = it }
                )

                RatingRow(
                    label = "Ketenangan & Keyakinan Diri",
                    score = ratingConfidence,
                    onScoreChanged = { ratingConfidence = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notes input
                Text(
                    text = "Catatan Pembelajaran Pribadi",
                    color = SakaCream,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = {
                        Text(
                            text = "Misal: 'Pembuka masih kurang nendang, kurangi filler words di menit pertama...'",
                            color = SakaCreamDim.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("reflection_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SakaGold,
                        unfocusedBorderColor = SakaBorderGold.copy(alpha = 0.4f),
                        focusedContainerColor = SakaDarkCard,
                        unfocusedContainerColor = SakaDarkCard,
                        focusedTextColor = SakaCream,
                        unfocusedTextColor = SakaCream
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save button
                Button(
                    onClick = {
                        onSave(ratingStructure, ratingClarity, ratingPacing, ratingConfidence, notes)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_session_button"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SakaGold,
                        contentColor = SakaDark
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIMPAN KE RIWAYAT LATIHAN",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = SakaCreamDim)
                ) {
                    Text("Lewati Tanpa Menyimpan")
                }
            }
        }
    }
}

@Composable
private fun RatingRow(
    label: String,
    score: Int,
    onScoreChanged: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = SakaCreamDim,
                fontSize = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= score) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Rating $i",
                        tint = if (i <= score) SakaGold else SakaCreamDim.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onScoreChanged(i) }
                    )
                }
            }
        }
    }
}
