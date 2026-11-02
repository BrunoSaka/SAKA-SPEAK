package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpeechFrameworks
import com.example.data.model.SpeechTip
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
import com.example.ui.theme.SakaMatchaDark
import com.example.ui.theme.SakaMatchaLight
import com.example.ui.theme.SakaTagBg

@Composable
fun TipsScreen(
    modifier: Modifier = Modifier
) {
    val frameworks = SpeechFrameworks.list

    Box(modifier = modifier.fillMaxSize()) {
        MegaMendungBackground(alpha = 0.04f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Text(
                text = "PANDUAN & ILMU RETORIKA",
                color = SakaMatchaLight,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kerangka Berpikir & Bicara",
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                color = SakaGoldLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Gunakan struktur ini untuk mengubah pemikiran acak menjadi argumen yang terarah, tajam, dan memukau.",
                color = SakaCreamMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("tips_list"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(frameworks, key = { it.id }) { framework ->
                    SpeechTipCard(tip = framework)
                }
            }
        }
    }
}

@Composable
fun SpeechTipCard(tip: SpeechTip) {
    var isExpanded by remember { mutableStateOf(tip.id == "prep") } // PREP is open by default

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SakaBorderGold.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SakaDarkCard)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SakaTagBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = SakaGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = tip.title,
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakaGoldLight
                        )
                        Text(
                            text = tip.subtitle,
                            color = SakaMatchaLight,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Tutup" else "Buka",
                    tint = SakaCreamDim
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
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    tip.steps.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SakaGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = step.letter,
                                    color = SakaDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.title,
                                    color = SakaCream,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = step.description,
                                    color = SakaCreamDim,
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SakaDarkSoft)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Contoh: ${step.example}",
                                        color = SakaGoldLight,
                                        fontSize = 12.sp,
                                        fontStyle = FontStyle.Italic,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SakaMatchaDark.copy(alpha = 0.35f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 ${tip.summary}",
                            color = SakaCream,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
