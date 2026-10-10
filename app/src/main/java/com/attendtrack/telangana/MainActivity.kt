package com.attendtrack.telangana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    private val vm: AttendViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Dashboard(vm) } }
    }
}

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
    var editing by remember { mutableStateOf<Subject?>(null) }
    val att = list.sumOf { it.attended }
    val held = list.sumOf { it.held }

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
                        Text("$att / $held classes")
                        Text(status(att, held))
                    }
                }
            }
            items(list, key = { it.id }) { s ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(s.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "%.1f%%  (%d/%d)".format(
                                Calculator.percent(s.attended, s.held), s.attended, s.held
                            )
                        )
                        Text(status(s.attended, s.held))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { vm.mark(s, true) }) { Text("Present") }
                            OutlinedButton(onClick = { vm.mark(s, false) }) { Text("Absent") }
                            TextButton(onClick = { editing = s }) { Text("Edit") }
                        }
                    }
                }
            }
        }
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
