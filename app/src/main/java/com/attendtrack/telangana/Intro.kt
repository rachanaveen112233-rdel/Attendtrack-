package com.attendtrack.telangana

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(onDone: () -> Unit) {
    val line1 = "APP BY"
    val line2 = "RACHA MADHAV"
    var t1 by remember { mutableStateOf("") }
    var t2 by remember { mutableStateOf("") }
    var cursorOn by remember { mutableStateOf(true) }
    var onSecond by remember { mutableStateOf(false) }

    // Blinking cursor
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            cursorOn = !cursorOn
        }
    }

    // Typing: 0 to 2.5 s = line 1, 2.5 to 5 s = line 2
    LaunchedEffect(Unit) {
        delay(600)
        for (c in line1) {
            t1 += c
            delay(150)
        }
        delay(1000)
        onSecond = true
        for (c in line2) {
            t2 += c
            delay(120)
        }
        delay(1060)
        onDone()
    }

    val cursor = if (cursorOn) "|" else " "

    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    t1 + if (!onSecond) cursor else "",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    t2 + if (onSecond) cursor else "",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
