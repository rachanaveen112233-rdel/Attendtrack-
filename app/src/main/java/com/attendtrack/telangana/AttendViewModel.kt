package com.attendtrack.telangana

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AttendViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).dao()

    val selectedDate = MutableStateFlow(LocalDate.now().toString())

    val subjects = dao.all().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val records = selectedDate.flatMapLatest { dao.forDate(it) }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    init {
        viewModelScope.launch {
            if (dao.count() == 0) {
                dao.insertAll(
                    listOf(
                        "Engineering Mathematics-I",
                        "Engineering Chemistry",
                        "Communicative English",
                        "DLD",
                        "Chemistry Lab",
                        "English Lab",
                        "Engineering Graphics"
                    ).map { Subject(name = it) }
                )
            }
        }
    }

    fun setDate(date: String) {
        selectedDate.value = date
    }

    fun markOn(s: Subject, present: Boolean) {
        viewModelScope.launch {
            val date = selectedDate.value
            val old = dao.find(s.id, date)
            val cur = dao.getSubject(s.id)
            val p = if (present) 1 else 0
            when {
                old == null -> {
                    dao.insertRecord(AttRecord(subjectId = s.id, date = date, present = present))
                    dao.update(cur.copy(attended = cur.attended + p, held = cur.held + 1))
                }
                old.present == present -> {
                    dao.deleteRecord(old)
                    dao.update(cur.copy(attended = cur.attended - p, held = cur.held - 1))
                }
                else -> {
                    dao.updateRecord(old.copy(present = present))
                    dao.update(cur.copy(attended = cur.attended + if (present) 1 else -1))
                }
            }
        }
    }

    fun edit(s: Subject, name: String, attended: Int, held: Int) {
        viewModelScope.launch {
            dao.update(s.copy(name = name, attended = attended, held = held))
        }
    }
}
