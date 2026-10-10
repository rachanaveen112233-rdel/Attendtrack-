package com.attendtrack.telangana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class MainActivity : ComponentActivity() {
    private val vm: AttendViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Dashboard(vm) } }
    }
}

val periodTimes = listOf("10-11", "11-12", "12-1", "2-3", "3-4", "4-5")

fun toDate(ms: Long): String =
    Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate().toString()

fun status(a: Int, h: Int): String = when {
    h == 0 -> "No classes yet"
    Calculator.percent(a, h) >= Calculator.TARGET ->
        "Safe: you can skip ${Calculator.canSkip(a, h)} class(es)"
    else -> "Attend next ${Calculator.needed(a, h)} class(es) to reach 75%"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(vm: AttendViewModel) {
    val list by vm.subjects.collectAsStateWithLifecycle()
    val recs by vm.records.collectAsStateWithLifecycle()
    val date by vm.selectedDate.collectAsStateWithLifecycle()
    val hols by vm.holidays.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<Subject?>(null) }
    var picking by remember { mutableStateOf(false) }
    var addStep by remember { mutableStateOf(0) }
    var newName by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exporter = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val csv = vm.buildCsv()
                context.contentResolver.openOutputStream(uri)?.use {
                    it.write(csv.toByteArray())
                }
            }
        }
    }

    val att = list.sumOf { it.attended }
    val held = list.sumOf { it.held }
    val day = LocalDate.parse(date).dayOfWeek
    val hol = hols.firstOrNull { date >= it.startDate && date <= it.endDate }
    val periods = if (hol != null) emptyList() else (Timetable.week[day] ?: emptyList())

    Scaffold(topBar = { TopAppBar(title = { Text("AttendTrack") }) }) { pad ->
        LazyColumn(
            Modifier.padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Overall", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "%.2f%%".format(Calculator.percent(att, held)),
                            style = MaterialTheme.typography.displaySmall
                        )
                        Text("$att / $held periods")
                        Text(status(att, held))
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$date  ($day)", modifier = Modifier.weight(1f))
                        Button(onClick = { picking = true }) { Text("Change", maxLines = 1) }
                    }
                }
            }
            if (hol != null) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Holiday: ${hol.name}", style = MaterialTheme.typography.titleMedium)
                            Text("Holidays are not counted as absences")
                        }
                    }
                }
            } else if (periods.isEmpty()) {
                item { Text("No classes on this day") }
            }
            itemsIndexed(periods) { i, name ->
                val rec = recs.firstOrNull { it.period == i + 1 }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Period ${i + 1}  (${periodTimes[i]})",
                            style = MaterialTheme.typography.labelLarge
                        )
                        if (name == null) {
                            Text("No class")
                        } else {
                            Text(name, style = MaterialTheme.typography.titleMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (rec?.present == true) {
                                    Button(onClick = { vm.markPeriod(name, i + 1, true) }) { Text("Present") }
                                } else {
                                    OutlinedButton(onClick = { vm.markPeriod(name, i + 1, true) }) { Text("Present") }
                                }
                                if (rec?.present == false) {
                                    Button(onClick = { vm.markPeriod(name, i + 1, false) }) { Text("Absent") }
                                } else {
                                    OutlinedButton(onClick = { vm.markPeriod(name, i + 1, false) }) { Text("Absent") }
                                }
                            }
                        }
                    }
                }
            }
            item { Text("Subjects", style = MaterialTheme.typography.titleLarge) }
            items(list, key = { "s" + it.id }) { s ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(s.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "%.1f%%  (%d/%d)".format(
                                Calculator.percent(s.attended, s.held), s.attended, s.held
                            )
                        )
                        Text(status(s.attended, s.held))
                        TextButton(onClick = { editing = s }) { Text("Edit") }
                    }
                }
            }
            item { Text("Holidays", style = MaterialTheme.typography.titleLarge) }
            items(hols, key = { "h" + it.id }) { h ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(h.name, style = MaterialTheme.typography.titleMedium)
                        Text("${h.startDate} to ${h.endDate}")
                        Text(if (h.provisional) "Provisional" else "Official (college almanac)")
                        TextButton(onClick = { vm.deleteHoliday(h) }) { Text("Delete") }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { addStep = 1 }) { Text("Add holiday") }
                    OutlinedButton(onClick = { exporter.launch("attendtrack.csv") }) {
                        Text("Export CSV")
                    }
                }
            }
        }
    }

    if (picking) {
        val state = rememberDatePickerState(
            yearRange = 2026..2027,
            initialSelectedDateMillis = LocalDate.parse(date)
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { picking = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { vm.setDate(toDate(it)) }
                    picking = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { picking = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }

    if (addStep == 1) {
        AlertDialog(
            onDismissRequest = { addStep = 0 },
            title = { Text("New holiday") },
            text = {
                OutlinedTextField(newName, { newName = it }, label = { Text("Name") })
            },
            confirmButton = {
                TextButton(onClick = { if (newName.isNotBlank()) addStep = 2 }) { Text("Next") }
            },
            dismissButton = { TextButton(onClick = { addStep = 0 }) { Text("Cancel") } }
        )
    }

    if (addStep == 2) {
        val rs = rememberDateRangePickerState(yearRange = 2026..2027)
        DatePickerDialog(
            onDismissRequest = { addStep = 0 },
            confirmButton = {
                TextButton(onClick = {
                    val s = rs.selectedStartDateMillis
                    val e = rs.selectedEndDateMillis ?: s
                    if (s != null && e != null) {
                        vm.addHoliday(newName.trim(), toDate(s), toDate(e))
                        newName = ""
                        addStep = 0
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { addStep = 0 }) { Text("Cancel") } }
        ) { DateRangePicker(state = rs, modifier = Modifier.height(500.dp)) }
    }

    editing?.let { s ->
        var name by remember(s.id) { mutableStateOf(s.name) }
        var a by remember(s.id) { mutableStateOf(s.attended.toString()) }
        var h by remember(s.id) { mutableStateOf(s.held.toString()) }
        val num = KeyboardOptions(keyboardType = KeyboardType.Number)
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text("Edit subject") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Name") })
                    OutlinedTextField(a, { a = it }, label = { Text("Attended") }, keyboardOptions = num)
                    OutlinedTextField(h, { h = it }, label = { Text("Total held") }, keyboardOptions = num)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.edit(s, name, a.toIntOrNull() ?: s.attended, h.toIntOrNull() ?: s.held)
                    editing = null
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { editing = null }) { Text("Cancel") } }
        )
    }
}
