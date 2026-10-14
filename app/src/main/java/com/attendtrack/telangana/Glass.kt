package com.attendtrack.telangana

import android.graphics.Typeface
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

private val thin = FontFamily(Typeface.create("sans-serif-thin", Typeface.NORMAL))
private val light = FontFamily(Typeface.create("sans-serif-light", Typeface.NORMAL))
private val medium = FontFamily(Typeface.create("sans-serif-medium", Typeface.NORMAL))
private val condensed = FontFamily(Typeface.create("sans-serif-condensed", Typeface.NORMAL))

private val GlassTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = thin, fontSize = 72.sp, lineHeight = 80.sp, letterSpacing = (-2).sp
    ),
    titleLarge = TextStyle(
        fontFamily = condensed, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, letterSpacing = 3.sp
    ),
    titleMedium = TextStyle(fontFamily = medium, fontSize = 18.sp, letterSpacing = 0.3.sp),
    labelLarge = TextStyle(
        fontFamily = condensed, fontSize = 14.sp, letterSpacing = 1.5.sp
    ),
    bodyLarge = TextStyle(fontFamily = light, fontSize = 16.sp, letterSpacing = 0.4.sp)
)

private val glassEdge = Brush.linearGradient(
    listOf(Color(0xB3FFFFFF), Color(0x14FFFFFF), Color(0x08FFFFFF), Color(0x55FFFFFF))
)

@Composable
fun GlassTheme(content: @Composable () -> Unit) {
    val t by rememberInfiniteTransition(label = "bg").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(26000, easing = LinearEasing), RepeatMode.Restart
        ),
        label = "t"
    )
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            background = Color.Transparent,
            surface = Color.Transparent,
            onBackground = Color.White,
            onSurface = Color.White,
            onSurfaceVariant = Color(0xFFCCCCCC),
            surfaceContainerHigh = Color(0xFF1E1E1E)
        ),
        typography = GlassTypography
    ) {
        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.fillMaxSize().background(Color.Black)) {
                val w = size.width
                val h = size.height
                val a = (t * 2.0 * Math.PI).toFloat()
                fun orb(cx: Float, cy: Float, r: Float, alpha: Float) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White.copy(alpha = alpha), Color.Transparent),
                            center = Offset(cx, cy),
                            radius = r
                        ),
                        radius = r,
                        center = Offset(cx, cy)
                    )
                }
                orb(w * 0.18f + 70f * sin(a), h * 0.16f + 45f * cos(a), w * 0.80f, 0.24f)
                orb(w * 0.88f + 55f * cos(a), h * 0.48f + 65f * sin(a), w * 0.70f, 0.17f)
                orb(w * 0.30f + 75f * cos(a), h * 0.88f + 55f * sin(a), w * 0.85f, 0.22f)
            }
            content()
        }
    }
}

@Composable
fun Card(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    val enter = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        enter.animateTo(1f, tween(550, easing = FastOutSlowInEasing))
    }
    val sweep by rememberInfiniteTransition(label = "sweep").animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            tween(5200, easing = LinearEasing), RepeatMode.Restart
        ),
        label = "sweepValue"
    )
    Column(
        modifier
            .graphicsLayer {
                alpha = enter.value
                translationY = (1f - enter.value) * 48.dp.toPx()
                val sc = 0.96f + 0.04f * enter.value
                scaleX = sc
                scaleY = sc
            }
            .background(
                Brush.linearGradient(listOf(Color(0x36FFFFFF), Color(0x0DFFFFFF))),
                shape
            )
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0x24FFFFFF), Color.Transparent),
                        endY = size.height * 0.45f
                    ),
                    cornerRadius = CornerRadius(28.dp.toPx())
                )
                val x = size.width * sweep
                drawRoundRect(
                    brush = Brush.linearGradient(
                        listOf(Color.Transparent, Color(0x22FFFFFF), Color.Transparent),
                        start = Offset(x - 160f, 0f),
                        end = Offset(x + 160f, size.height)
                    ),
                    cornerRadius = CornerRadius(28.dp.toPx())
                )
            }
            .border(1.dp, glassEdge, shape),
        content = content
    )
}

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(50)
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.92f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press"
    )
    val bg by animateColorAsState(
        if (pressed) Color(0xFFCFCFCF) else Color.White,
        label = "bg"
    )
    Row(
        modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(bg, shape)
            .clickable(interactionSource = src, indication = null, onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.Black) { content() }
    }
}

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(50)
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.92f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press"
    )
    val bg by animateColorAsState(
        if (pressed) Color(0x40FFFFFF) else Color(0x14FFFFFF),
        label = "bg"
    )
    Row(
        modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(bg, shape)
            .border(1.dp, Color(0xB3FFFFFF), shape)
            .clickable(interactionSource = src, indication = null, onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) { content() }
    }
}
