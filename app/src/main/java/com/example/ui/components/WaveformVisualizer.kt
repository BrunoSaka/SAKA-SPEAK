package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SakaGold
import com.example.ui.theme.SakaMatchaLight

@Composable
fun WaveformVisualizer(
    isRecording: Boolean,
    modifier: Modifier = Modifier,
    amplitude: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "p1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "p2"
    )

    val pulse3 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "p3"
    )

    val ampFactor = (amplitude / 32767f).coerceIn(0f, 1f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bars = listOf(pulse1, pulse2, pulse3, pulse1 * 0.8f, pulse2 * 1.1f, pulse3 * 0.9f, pulse1 * 0.6f)
        bars.forEachIndexed { index, pulse ->
            val heightFraction = if (isRecording) {
                ((pulse * 0.5f) + (ampFactor * 0.8f)).coerceIn(0.15f, 1f)
            } else {
                0.15f
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(fraction = heightFraction)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index % 2 == 0) SakaGold else SakaMatchaLight)
            )
            Box(modifier = Modifier.width(4.dp))
        }
    }
}
