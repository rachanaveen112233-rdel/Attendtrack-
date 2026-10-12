package com.attendtrack.telangana

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    listOf(Color(0xCCFFFFFF), Color(0x10FFFFFF), Color(0x66FFFFFF))
)

@Composable
fun GlassTheme(content: @Composable () -> Unit) {
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
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x40FFFFFF), Color.Transparent),
                        center = Offset(150f, 250f),
                        radius = 900f
                    )
                )
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x30FFFFFF), Color.Transparent),
                        center = Offset(1000f, 1700f),
                        radius = 1100f
                    )
                )
        ) { content() }
    }
}

@Composable
fun Card(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier
            .background(
                Brush.linearGradient(listOf(Color(0x2EFFFFFF), Color(0x0AFFFFFF))),
                shape
            )
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
    Row(
        modifier
            .background(Color.White, shape)
            .clickable(onClick = onClick)
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
    Row(
        modifier
            .background(Color(0x0DFFFFFF), shape)
            .border(1.dp, Color(0xB3FFFFFF), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) { content() }
    }
}
