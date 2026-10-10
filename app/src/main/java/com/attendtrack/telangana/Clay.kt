package com.attendtrack.telangana

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val ClayBg = Color(0xFFF1EEFF)
val ClayCardEnd = Color(0xFFE2DCFA)
val ClayPrimary = Color(0xFF7C6CF0)
val ClayPrimaryLight = Color(0xFFA69BFF)
val ClayInk = Color(0xFF2E2A4A)
val ClayShadow = Color(0xFF7C6CF0)

@Composable
fun ClayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = ClayPrimary,
            onPrimary = Color.White,
            background = ClayBg,
            surface = ClayBg,
            onBackground = ClayInk,
            onSurface = ClayInk
        ),
        content = content
    )
}

private val highlight = Brush.linearGradient(listOf(Color.White, Color(0x22FFFFFF)))

@Composable
fun Card(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier
            .shadow(10.dp, shape, ambientColor = ClayShadow, spotColor = ClayShadow)
            .background(Brush.linearGradient(listOf(Color.White, ClayCardEnd)), shape)
            .border(2.dp, highlight, shape),
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
            .shadow(8.dp, shape, ambientColor = ClayShadow, spotColor = ClayShadow)
            .background(Brush.verticalGradient(listOf(ClayPrimaryLight, ClayPrimary)), shape)
            .border(1.5.dp, highlight, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) { content() }
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
            .shadow(6.dp, shape, ambientColor = ClayShadow, spotColor = ClayShadow)
            .background(Brush.verticalGradient(listOf(Color.White, ClayCardEnd)), shape)
            .border(1.5.dp, highlight, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides ClayPrimary) { content() }
    }
}
