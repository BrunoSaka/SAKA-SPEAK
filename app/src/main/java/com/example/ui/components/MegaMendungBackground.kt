package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.SakaGold
import com.example.ui.theme.SakaMatcha

@Composable
fun MegaMendungBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.08f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Subtle dotted batik background grid (45dp spacing equivalent)
        val spacing = 48f
        var x = 0f
        while (x < width) {
            var y = 0f
            while (y < height) {
                drawCircle(
                    color = if ((x + y).toInt() % 96 == 0) SakaGold else SakaMatcha,
                    radius = 1.5f,
                    center = Offset(x, y),
                    alpha = alpha * 0.7f
                )
                y += spacing
            }
            x += spacing
        }

        // 2. Left Mega Mendung Cloud curves
        val leftCloudPath = Path().apply {
            moveTo(-100f, 150f)
            cubicTo(0f, 100f, 120f, 140f, 160f, 220f)
            cubicTo(200f, 300f, 100f, 360f, 40f, 380f)
            cubicTo(-20f, 400f, -80f, 420f, -100f, 420f)
            close()
        }
        drawPath(
            path = leftCloudPath,
            color = SakaGold,
            alpha = alpha * 1.2f,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val leftInnerCloud = Path().apply {
            moveTo(-60f, 190f)
            cubicTo(10f, 160f, 80f, 180f, 110f, 240f)
            cubicTo(140f, 300f, 70f, 340f, -20f, 350f)
        }
        drawPath(
            path = leftInnerCloud,
            color = SakaMatcha,
            alpha = alpha * 0.9f,
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )

        // 3. Right Bottom Mega Mendung Cloud curves
        val rightCloudPath = Path().apply {
            moveTo(width + 120f, height - 200f)
            cubicTo(width - 40f, height - 260f, width - 150f, height - 190f, width - 180f, height - 110f)
            cubicTo(width - 210f, height - 30f, width - 100f, height + 40f, width + 120f, height + 40f)
            close()
        }
        drawPath(
            path = rightCloudPath,
            color = SakaGold,
            alpha = alpha * 1.2f,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val rightInnerCloud = Path().apply {
            moveTo(width + 80f, height - 160f)
            cubicTo(width - 10f, height - 200f, width - 100f, height - 150f, width - 120f, height - 80f)
            cubicTo(width - 140f, height - 10f, width - 50f, height + 20f, width + 80f, height + 20f)
        }
        drawPath(
            path = rightInnerCloud,
            color = SakaMatcha,
            alpha = alpha * 0.9f,
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
    }
}
