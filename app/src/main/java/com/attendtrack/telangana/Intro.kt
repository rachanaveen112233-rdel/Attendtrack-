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
    val line1 = "A APP BY"
    val line2 = "RACHA MADHAV"
    var t1 by remember { mutableStateOf("") }
    var t2 by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // 0 to 2.5 seconds: type line 1
        for (c in line1) {
            t1 += c
            delay(250)
        }
        delay(500)
        // 2.5 to 5 seconds: type line 2
        for (c in line2) {
            t2 += c
            delay(150)
        }
        delay(700)
        onDone()
    }

    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    t1,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    t2,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
