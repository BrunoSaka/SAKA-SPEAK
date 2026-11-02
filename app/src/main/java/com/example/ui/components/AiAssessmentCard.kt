package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiSpeechAssessment
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
import com.example.ui.theme.SakaTagBg
import com.example.ui.theme.SakaWhite

@Composable
fun AiAssessmentCard(
    assessment: AiSpeechAssessment,
    modifier: Modifier = Modifier,
    isGemini: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SakaBorderGold, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SakaDarkCard)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with AI Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SakaTagBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SakaGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isGemini) "ANALISIS GEMINI AI" else "PENILAIAN ARTIKULASI AI",
                                color = if (isGemini) SakaGoldLight else SakaMatchaLight,
                                fontSize = 11.sp,
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isGemini) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SakaGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Gemini API", color = SakaGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = assessment.predikat,
                            fontFamily = FontFamily.Serif,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakaGoldLight
                        )
                    }
                }

                // Overall Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SakaGold)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${assessment.overallScore}/100",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = SakaDark,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5 Key Metrics: Bahasa, Artikulasi, Nada, Improvisasi, Wawasan
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ScoreMetricRow(
                    label = "1. Bahasa (Tata Bahasa & Diksi)",
                    score = assessment.skorBahasa,
                    description = assessment.catatanBahasa
                )
                ScoreMetricRow(
                    label = "2. Artikulasi (Kejelasan Vokal)",
                    score = assessment.skorArtikulasi,
                    description = assessment.catatanArtikulasi
                )
                ScoreMetricRow(
                    label = "3. Intonasi & Nada (Dinamika Suara)",
                    score = assessment.skorNada,
                    description = assessment.catatanNada
                )
                ScoreMetricRow(
                    label = "4. Improvisasi (Alur Spontan)",
                    score = assessment.skorImprovisasi,
                    description = assessment.catatanImprovisasi
                )
                ScoreMetricRow(
                    label = "5. Wawasan & Insight (Kedalaman Solusi)",
                    score = assessment.skorWawasan,
                    description = assessment.catatanWawasan
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Toggle Expandable Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Sembunyikan Analisis Lengkap" else "Lihat Analisis Lengkap & Rekomendasi",
                    color = SakaGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = SakaGoldLight,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
                ) + fadeIn(animationSpec = tween(220)),
                exit = shrinkVertically(
                    animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
                ) + fadeOut(animationSpec = tween(180))
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Ringkasan Eksekutif
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SakaDarkSoft)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = assessment.ringkasanEksekutif,
                            color = SakaCream,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Kelebihan
                    if (assessment.kelebihan.isNotEmpty()) {
                        Text(
                            text = "Kelebihan Bicaramu:",
                            color = SakaMatchaLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        assessment.kelebihan.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SakaMatchaLight,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item,
                                    color = SakaCreamDim,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Area Perbaikan
                    if (assessment.areaPerbaikan.isNotEmpty()) {
                        Text(
                            text = "Area yang Perlu Ditingkatkan:",
                            color = SakaGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        assessment.areaPerbaikan.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = SakaGold,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item,
                                    color = SakaCreamDim,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Saran Latihan
                    if (assessment.saranLatihan.isNotEmpty()) {
                        Text(
                            text = "Saran Latihan Praktis:",
                            color = SakaCream,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        assessment.saranLatihan.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = SakaGold,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item,
                                    color = SakaCreamDim,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreMetricRow(
    label: String,
    score: Int,
    description: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (score / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "scoreProgress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = SakaCream,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$score/100",
                color = if (score >= 85) SakaGold else SakaCreamDim,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (score >= 85) SakaGold else SakaMatcha,
            trackColor = SakaDarkSoft,
            strokeCap = StrokeCap.Round
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = description,
            color = SakaCreamMuted,
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
    }
}
