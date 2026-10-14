package com.attendtrack.telangana

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val todaySlots = listOf(
    "10:00 – 11:00", "11:00 – 12:00", "12:00 – 1:00",
    "2:00 – 3:00", "3:00 – 4:00", "4:00 – 5:00"
)
private val todayMuted = Color(0xFFBBBBBB)

@Composable
private fun TodayRing(fraction: Float, ringSize: Dp, label: String) {
    val anim by animateFloatAsState(fraction.coerceIn(0f, 1f), tween(900), label = "ring")
    Box(Modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 12.dp.toPx()
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arc = Size(size.width - stroke, size.height - stroke)
            drawArc(Color(0x26FFFFFF), 0f, 360f, false, topLeft, arc, style = Stroke(stroke))
            drawArc(
                Color.White, -90f, 360f * anim, false, topLeft, arc,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-1).sp
            )
        )
    }
}

@Composable
private fun TodayChip(filled: Boolean, text: String) {
    val shape = RoundedCornerShape(50)
    Text(
        text,
        modifier = Modifier
            .background(if (filled) Color.White else Color.Transparent, shape)
            .border(1.dp, Color.White, shape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = if (filled) Color.Black else Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp
    )
}

@Composable
private fun TodayToggle(
    selected: Boolean,
    selectedBg: Color,
    onClick: () -> Unit,
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(50)
    val fg = if (selectedBg == Color.White) Color.Black else Color.White
    Row(
        modifier
            .clip(shape)
            .background(if (selected) selectedBg else Color(0x0DFFFFFF), shape)
            .border(1.dp, if (selected) selectedBg else Color(0x66FFFFFF), shape)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides if (selected) fg else Color.White) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(vm: AttendViewModel) {
    val list by vm.subjects.collectAsStateWithLifecycle()
    val recs by vm.records.collectAsStateWithLifecycle()
    val dateStr by vm.selectedDate.collectAsStateWithLifecycle()
    val hols by vm.holidays.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    var picking by remember { mutableStateOf(false) }

    val date = LocalDate.parse(dateStr)
    val att = list.sumOf { it.attended }
    val held = list.sumOf { it.held }
    val pct = Calculator.percent(att, held)
    val hol = hols.firstOrNull { dateStr >= it.startDate && dateStr <= it.endDate }
    val periods = if (hol != null) emptyList() else (Timetable.week[date.dayOfWeek] ?: emptyList())
    val monday = date.minusDays((date.dayOfWeek.value - 1).toLong())

    val go = { d: LocalDate -> if (d.year in 2026..2027) vm.setDate(d.toString()) }
    val isHoliday = { d: LocalDate ->
        val s = d.toString()
        hols.any { s >= it.startDate && s <= it.endDate }
    }

    val advice = when {
        held == 0 -> "Mark your first class to begin"
        pct >= Calculator.TARGET -> {
            val n = Calculator.canSkip(att, held)
            "You can skip $n period" + if (n == 1) "" else "s"
        }
        else -> {
            val n = Calculator.needed(att, held)
            "Attend the next $n period" + (if (n == 1) "" else "s") + " to reach 75%"
        }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (date == LocalDate.now()) "TODAY" else date.dayOfWeek.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = todayMuted
                    )
                    Text(
                        date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                IconButton(onClick = { picking = true }) {
                    Icon(Icons.Default.DateRange, "Pick a date", tint = Color.White)
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    TodayRing((pct / 100.0).toFloat(), 128.dp, "%.1f%%".format(pct))
                    Spacer(Modifier.width(20.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "OVERALL",
                            style = MaterialTheme.typography.labelLarge,
                            color = todayMuted
                        )
                        Text("$att / $held periods", style = MaterialTheme.typography.titleMedium)
                        when {
                            held == 0 -> TodayChip(false, "NO DATA YET")
                            pct >= Calculator.TARGET -> TodayChip(true, "ON TRACK")
                            else -> TodayChip(false, "BELOW 75%")
                        }
                        Text(advice, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { go(date.minusWeeks(1)) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.KeyboardArrowLeft, "Previous week", tint = Color.White)
                }
                Row(
                    Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in 0..5) {
                        val d = monday.plusDays(i.toLong())
                        val sel = d == date
                        val shape = RoundedCornerShape(18.dp)
                        Column(
                            Modifier
                                .weight(1f)
                                .clip(shape)
                                .background(if (sel) Color.White else Color(0x14FFFFFF), shape)
                                .border(1.dp, Color(0x33FFFFFF), shape)
                                .clickable { go(d) }
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                d.dayOfWeek.name.take(3),
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = if (sel) Color.Black else todayMuted
                            )
                            Text(
                                d.dayOfMonth.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (sel) Color.Black else Color.White
                            )
                            Spacer(Modifier.height(3.dp))
                            Box(
                                Modifier
                                    .size(4.dp)
                                    .background(
                                        if (isHoliday(d)) (if (sel) Color.Black else Color.White)
                                        else Color.Transparent,
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
                IconButton(onClick = { go(date.plusWeeks(1)) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.KeyboardArrowRight, "Next week", tint = Color.White)
                }
            }
        }

        if (hol != null) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TodayChip(false, "HOLIDAY")
                        Text(hol.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "No classes. Holidays never count as absences.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = todayMuted
                        )
                    }
                }
            }
        } else if (periods.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text("No classes on this day", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        } else {
            item {
                Text("SCHEDULE", style = MaterialTheme.typography.labelLarge, color = todayMuted)
            }
        }

        itemsIndexed(periods) { i, name ->
            val rec = recs.firstOrNull { it.period == i + 1 }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(38.dp)
                                .border(1.dp, Color(0x66FFFFFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${i + 1}", fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                name ?: "Free period",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (name == null) todayMuted else Color.White
                            )
                            Text(todaySlots[i], fontSize = 13.sp, color = todayMuted)
                        }
                    }
                    if (name != null) {
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TodayToggle(
                                selected = rec?.present == true,
                                selectedBg = Color.White,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    vm.markPeriod(name, i + 1, true)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Check, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Present", fontWeight = FontWeight.Medium)
                            }
                            TodayToggle(
                                selected = rec?.present == false,
                                selectedBg = Color(0xFF555555),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    vm.markPeriod(name, i + 1, false)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Close, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Absent", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (picking) {
        val state = rememberDatePickerState(
            yearRange = 2026..2027,
            initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
     )
        DatePickerDialog(
            onDismissRequest = { picking = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        vm.setDate(
                            Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toString()
                        )
                    }
                    picking = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { picking = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }
}
