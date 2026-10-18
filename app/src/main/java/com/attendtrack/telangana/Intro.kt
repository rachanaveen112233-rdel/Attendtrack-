package com.attendtrack.telangana

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val IntroGold = Color(0xFFD9BF85)
private val IntroGoldMuted = Color(0xFF8F8160)

@Composable
fun IntroScreen(onDone: () -> Unit) {
    val line = remember { Animatable(0f) }
    val mark = remember { Animatable(0f) }
    val bar = remember { Animatable(0f) }
    val word = remember { Animatable(0f) }
    val by = remember { Animatable(0f) }
    val name = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        if (Motion.level == 0) {
            delay(700)
            onDone()
            return@LaunchedEffect
        }
        launch { line.animateTo(1f, tween(900)) }
        delay(500)
        launch { mark.animateTo(1f, tween(900)) }
        delay(500)
        launch { bar.animateTo(1f, tween(900)) }
        delay(700)
        launch { word.animateTo(1f, tween(1100)) }
        delay(900)
        launch { by.animateTo(1f, tween(700)) }
        delay(500)
        launch { name.animateTo(1f, tween(900)) }
        delay(1500)
        onDone()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF171B2A), Color(0xFF05070B)),
                    radius = 1400f
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDone() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(150.dp)
                    .graphicsLayer {
                        alpha = mark.value
                        val s = 0.85f + 0.15f * mark.value
                        scaleX = s
                        scaleY = s
                    }
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    val k = size.width / 108f
                    fun p(x: Float, y: Float) = Offset(x * k, y * k)

                    val a = Path().apply {
                        moveTo(32.54f * k, 72.27f * k)
                        lineTo(54f * k, 27.03f * k)
                        lineTo(75.46f * k, 72.27f * k)
                    }
                    drawPath(
                        a, Color.White,
                        style = Stroke(8f * k, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawLine(
                        Color(0xFF3DDC97), p(43f, 58.35f), p(65f, 58.35f),
                        strokeWidth = 6.4f * k, cap = StrokeCap.Round
                    )
                    drawLine(
                        Color(0x33FFFFFF), p(34.9f, 82.7f), p(73.1f, 82.7f),
                        strokeWidth = 4.64f * k, cap = StrokeCap.Round
                    )
                    drawLine(
                        Color(0xFF5B6CFF), p(34.9f, 82.7f),
                        p(34.9f + 27.2f * bar.value, 82.7f),
                        strokeWidth = 4.64f * k, cap = StrokeCap.Round
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            Box(
                Modifier
                    .width((140f * line.value).dp)
                    .height(1.dp)
                    .background(IntroGold)
            )

            Spacer(Modifier.height(26.dp))
            Text(
                "ATTENDTRACK",
                fontSize = 26.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                color = Color.White,
                letterSpacing = (14f - 8f * word.value).sp,
                modifier = Modifier.graphicsLayer { alpha = word.value }
            )

            Spacer(Modifier.height(38.dp))
            Text(
                "APP BY",
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                color = IntroGoldMuted,
                letterSpacing = 4.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = by.value
                    translationY = (1f - by.value) * 10.dp.toPx()
                }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "RACHA MADHAV",
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                color = IntroGold,
                letterSpacing = (10f - 5f * name.value).sp,
                modifier = Modifier.graphicsLayer {
                    alpha = name.value
                    translationY = (1f - name.value) * 10.dp.toPx()
                }
            )
        }
    }
}
