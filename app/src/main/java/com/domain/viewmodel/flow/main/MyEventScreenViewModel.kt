package com.domain.viewmodel.flow.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.database.dao.StudentDao
import com.database.entities.Event
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


class MyEventScreenViewModel(
    private val studentDao: StudentDao,
    private val studentId: Long
) : ViewModel() {

    val events: StateFlow<List<Event>> =
        studentDao.observeSubscribedEvents(studentId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

}