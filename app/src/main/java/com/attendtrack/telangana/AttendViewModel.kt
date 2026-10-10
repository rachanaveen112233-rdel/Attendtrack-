package com.attendtrack.telangana

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttendViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).dao()

    val subjects = dao.all().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    init {
        viewModelScope.launch {
            if (dao.count() == 0) {
                dao.insertAll((1..6).map { Subject(name = "Subject $it") })
            }
        }
    }

    fun mark(s: Subject, present: Boolean) {
        viewModelScope.launch {
            dao.update(
                s.copy(
                    attended = s.attended + if (present) 1 else 0,
                    held = s.held + 1
                )
            )
        }
    }

    fun edit(s: Subject, name: String, attended: Int, held: Int) {
        viewModelScope.launch {
            dao.update(s.copy(name = name, attended = attended, held = held))
        }
    }
}
