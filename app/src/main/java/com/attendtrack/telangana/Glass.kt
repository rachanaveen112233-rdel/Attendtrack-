package com.attendtrack.telangana

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
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val glassBorder = Brush.linearGradient(
    listOf(Color(0x99FFFFFF), Color(0x1AFFFFFF))
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
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                                                listOf(Color(0xFF000000), Color(0xFF2B2B2B), Color(0xFF0A0A0A))
                    )
                )
        ) { content() }
    }
}

@Composable
fun Card(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier
            .background(
                                Brush.verticalGradient(listOf(Color(0x99FFFFFF), Color(0x55FFFFFF))),
                shape
            )
            .border(1.dp, glassBorder, shape),
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
            .background(
                Brush.verticalGradient(listOf(Color(0x99A5D8FF), Color(0x55A5D8FF))),
                shape
            )
            .border(1.dp, Color(0xCCFFFFFF), shape)
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
            .background(Color(0x14FFFFFF), shape)
            .border(1.dp, Color(0x66FFFFFF), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) { content() }
    }
}
