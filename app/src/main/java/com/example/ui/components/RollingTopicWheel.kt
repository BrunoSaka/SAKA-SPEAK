package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Topic
import com.example.ui.theme.SakaBorderGold
import com.example.ui.theme.SakaBrown
import com.example.ui.theme.SakaCream
import com.example.ui.theme.SakaCreamDim
import com.example.ui.theme.SakaDark
import com.example.ui.theme.SakaDarkCard
import com.example.ui.theme.SakaDarkSoft
import com.example.ui.theme.SakaGold
import com.example.ui.theme.SakaGoldLight
import com.example.ui.theme.SakaMatcha
import com.example.ui.theme.SakaMatchaLight
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Komponen Animasi Reel / Wheel Acak Topik Mendatar Menggulung ke Bawah (Horizontal-Axis Downward Wheel)
 *
 * Menghadirkan silinder 3D mekanikal yang berputar mendatar ke bawah dengan:
 * - Percepatan awal dan deselerasi inersia fisik sangat halus (CubicBezier + spring bounce)
 * - Proyeksi 3D silinder (rotationX, scale, dan fade dinamis)
 * - Penunjuk golden frame tengah dengan indikator panah presisi
 * - Penyesuaian responsif dan fluiditas 60fps
 */
@Composable
fun RollingTopicWheel(
    isRolling: Boolean,
    candidateTopics: List<Topic>,
    targetTopic: Topic,
    onRollFinished: () -> Unit,
    modifier: Modifier = Modifier,
    onTriggerRoll: (() -> Unit)? = null
) {
    val density = LocalDensity.current

    val viewportHeightDp = 136.dp
    val itemHeightDp = 52.dp

    // Bangun daftar reel berurutan: topik-topik acak yang diakhiri oleh targetTopic
    val reelList = remember(candidateTopics, targetTopic) {
        val pool = (if (candidateTopics.isNotEmpty()) candidateTopics else listOf(targetTopic)).shuffled()
        val items = mutableListOf<Topic>()
        repeat(5) {
            items.addAll(pool.take(4))
        }
        items.add(targetTopic)
        items
    }

    val totalItems = reelList.size
    val itemHeightPx = with(density) { itemHeightDp.toPx() }
    val viewportHeightPx = with(density) { viewportHeightDp.toPx() }
    val centerYPx = viewportHeightPx / 2f

    // Total perpindahan: dari item pertama (atas) bergulir ke bawah hingga targetTopic mendarat pas di tengah
    // Supaya arah gulungan mendatar ke bawah ("downward rolling"):
    // Posisi y bergerak positif ke bawah.
    val targetIndex = totalItems - 1
    // Offset target agar item terakhir mendarat tepat di tengah
    val finalScrollY = targetIndex * itemHeightPx
    val scrollOffsetY = remember { Animatable(0f) }

    var isSettledFlash by remember { mutableStateOf(false) }

    LaunchedEffect(isRolling) {
        if (isRolling && totalItems > 1) {
            isSettledFlash = false
            scrollOffsetY.snapTo(0f)

            // Animasi deselerasi inersia halus seperti silinder berputar mendatar ke bawah
            scrollOffsetY.animateTo(
                targetValue = finalScrollY,
                animationSpec = tween(
                    durationMillis = 1450,
                    easing = CubicBezierEasing(0.12f, 0.88f, 0.28f, 1.0f)
                )
            )

            // Sedikit pegas pantulan halus saat mengunci (gentle settle micro-bounce)
            scrollOffsetY.animateTo(
                targetValue = finalScrollY,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )

            isSettledFlash = true
            onRollFinished()
        } else if (!isRolling) {
            // Pasif: kunci tepat di targetTopic
            scrollOffsetY.snapTo(finalScrollY)
        }
    }

    val glowAlpha by animateFloatAsState(
        targetValue = if (isSettledFlash) 0.9f else 0.35f,
        animationSpec = tween(400),
        label = "settleGlow"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(viewportHeightDp)
            .border(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(SakaBorderGold, SakaGold, SakaBorderGold)
                ),
                RoundedCornerShape(22.dp)
            )
            .shadow(12.dp, RoundedCornerShape(22.dp), ambientColor = SakaGold, spotColor = SakaGold)
            .testTag("rolling_topic_wheel"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SakaDarkSoft)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(22.dp))
                .clickable(enabled = !isRolling && onTriggerRoll != null) {
                    onTriggerRoll?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            // Latar Belakang Tekstur Silinder Roda SAKA
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SakaDark,
                                SakaBrown.copy(alpha = 0.5f),
                                SakaDarkSoft,
                                SakaBrown.copy(alpha = 0.5f),
                                SakaDark
                            )
                        )
                    )
            )

            // Frame Fokus Tengah (Center Target Viewfinder Bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .height(itemHeightDp)
                    .border(
                        1.2.dp,
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                SakaGold.copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                SakaGold.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        ),
                        RoundedCornerShape(14.dp)
                    )
            )

            // Indikator Panah Sisi Penunjuk (Reel Center Pointers)
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "▶",
                    color = SakaGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.graphicsLayer { alpha = glowAlpha }
                )
                Text(
                    text = "◀",
                    color = SakaGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.graphicsLayer { alpha = glowAlpha }
                )
            }

            // Kontainer Reel Berputar ke Bawah (Downward Rolling Wheel Cylinder)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        clip = true
                        cameraDistance = 8f * density.density
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                val currentScroll = scrollOffsetY.value

                reelList.forEachIndexed { index, topic ->
                    // Posisi Y tengah dari item ini di dalam viewport:
                    // Bergerak ke bawah saat scroll bertambah
                    val itemBaseY = index * itemHeightPx
                    val itemCurrentY = itemBaseY - currentScroll + centerYPx - (itemHeightPx / 2f)

                    // Hitung jarak dari garis tengah fokus untuk transformasi 3D silinder
                    val distFromCenter = itemCurrentY + (itemHeightPx / 2f) - centerYPx

                    // Hanya render jika berada dalam batas pandang silinder (efisiensi tinggi)
                    if (abs(distFromCenter) < viewportHeightPx) {
                        val normalizedDist = (distFromCenter / (viewportHeightPx / 2f)).coerceIn(-1.2f, 1.2f)

                        // Efek Silinder 3D: rotasi sumbu mendatar (rotationX), skala dan opasitas
                        val rotX = -normalizedDist * 42f
                        val scale = (1f - abs(normalizedDist) * 0.16f).coerceIn(0.82f, 1.05f)
                        val alpha = (1f - abs(normalizedDist) * 0.72f).coerceIn(0.12f, 1f)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeightDp)
                                .offset { IntOffset(x = 0, y = itemCurrentY.roundToInt()) }
                                .graphicsLayer {
                                    rotationX = rotX
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                                }
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SakaGold,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = topic.category.uppercase(),
                                        color = SakaMatchaLight,
                                        fontSize = 9.5.sp,
                                        letterSpacing = 1.8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = topic.title,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (abs(distFromCenter) < 18f) SakaGoldLight else SakaCream,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Masking Silinder Atas & Bawah (Vignette Shadow Depth)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to SakaDark.copy(alpha = 0.96f),
                            0.22f to Color.Transparent,
                            0.78f to Color.Transparent,
                            1.0f to SakaDark.copy(alpha = 0.96f)
                        )
                    )
            )

            // Header Label Badge: Tanda Status Wheel
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SakaDark.copy(alpha = 0.85f))
                    .border(0.8.dp, SakaGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isRolling) Icons.Default.Refresh else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SakaGold,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = if (isRolling) "WHEEL MENGGULUNG KE BAWAH..." else "ROLET TOPIK SAKA • KLIK UNTUK ACAK",
                        color = SakaGold,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
