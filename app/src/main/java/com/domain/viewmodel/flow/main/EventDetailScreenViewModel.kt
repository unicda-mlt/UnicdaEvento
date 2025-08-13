package com.domain.viewmodel.flow.main

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.database.dao.EventDao
import com.database.dao.EventStudentDao
import com.database.entities.EventStudentCrossRef
import com.database.entities.EventWithRefs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class EventDetailScreenViewModel(
    private val eventDao: EventDao,
    private val eventStudentDao: EventStudentDao,
    private val studentId: Long,
) : ViewModel() {
    data class UIState(
        val loading: Boolean = false,
        val joining: Boolean = false,
        val error: String? = null
    )

    private val eventId = MutableStateFlow<Long?>(null)

    private val _ui = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _ui

    @OptIn(ExperimentalCoroutinesApi::class)
    val event: StateFlow<EventWithRefs?> =
        eventId
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { id -> eventDao.observeWithRefs(id) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

    init {
        eventId
            .filterNotNull()
            .onEach { _ui.update { it.copy(loading = true, error = null) } }
            .launchIn(viewModelScope)

        event
            .onEach { _ui.update { it.copy(loading = false) } }
            .launchIn(viewModelScope)
    }

    fun loadEvent(id: Long) {
        _ui.update { it.copy(loading = true, error = null) }
        eventId.value = id
    }

    fun joinStudent() {
        val id = eventId.value ?: run {
            _ui.update { it.copy(error = "No event selected") }
            return
        }

        if (_ui.value.joining) return

        viewModelScope.launch {
            _ui.update { it.copy(joining = true, error = null) }
            try {
                // MUST BE DELETED IN PRODUCTION //
                delay(1_000)
                // MUST BE DELETED IN PRODUCTION //

                eventStudentDao.insert(
                    EventStudentCrossRef(eventId = id, studentId = studentId)
                )
            } catch (e: Exception) {
                _ui.update { it.copy(error = "Error trying to join event") }
            } finally {
                _ui.update { it.copy(joining = false) }
            }
        }
    }
}
