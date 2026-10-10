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

    val holidays = dao.holidays().stateIn(
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
            if (dao.holidayCount() == 0) {
                dao.insertHolidays(
                    listOf(
                        Holiday(
                            name = "Dussehra Vacation",
                            startDate = "2026-10-12",
                            endDate = "2026-10-22",
                            provisional = false
                        ),
                        Holiday(
                            name = "Preparation Holidays & Practical Exams",
                            startDate = "2026-12-06",
                            endDate = "2026-12-13",
                            provisional = false
                        )
                    )
                )
            }
        }
    }

    fun holidayOn(date: String): Holiday? =
        holidays.value.firstOrNull { date >= it.startDate && date <= it.endDate }

    fun setDate(date: String) {
        selectedDate.value = date
    }

    fun markPeriod(subjectName: String, period: Int, present: Boolean) {
        viewModelScope.launch {
            val date = selectedDate.value
            if (holidayOn(date) != null) return@launch
            val s = subjects.value.firstOrNull { it.name == subjectName } ?: return@launch
            val old = dao.find(date, period)
            val cur = dao.getSubject(s.id)
            val p = if (present) 1 else 0
            when {
                old == null -> {
                    dao.insertRecord(
                        AttRecord(subjectId = s.id, date = date, period = period, present = present)
                    )
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

        suspend fun buildCsv(): String {
        val names = subjects.value.associate { it.id to it.name }
        val sb = StringBuilder("date,day,period,subject,status\n")
        for (r in dao.allRecords()) {
            val day = LocalDate.parse(r.date).dayOfWeek
            val status = if (r.present) "Present" else "Absent"
            sb.append("${r.date},$day,${r.period},${names[r.subjectId] ?: ""},$status\n")
        }
        return sb.toString()
    }
            fun addHoliday(name: String, start: String, end: String) {
            dao.insertHoliday(
                Holiday(name = name, startDate = start, endDate = end, provisional = true)
            )
        }
    }

    fun deleteHoliday(h: Holiday) {
        viewModelScope.launch { dao.deleteHoliday(h) }
    }
}
