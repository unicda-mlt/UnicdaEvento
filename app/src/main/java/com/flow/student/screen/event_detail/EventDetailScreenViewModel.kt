package com.flow.student.screen.event_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.entities.EventWithRefs
import com.repository.event.EventRepository
import com.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EventDetailScreenViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    data class UIState(
        val loading: Boolean = false,
        val joining: Boolean = false,
        val error: String? = null
    )

    private val eventId = MutableStateFlow<String?>(null)

    private val _ui = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _ui

    @OptIn(ExperimentalCoroutinesApi::class)
    val event: StateFlow<EventWithRefs?> =
        eventId
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { id -> eventRepository.observeWithRefs(id) }
            .stateIn(
                viewModelScope,
                SharingStarted.Companion.WhileSubscribed(5_000),
                null
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val isJoinedEvent: StateFlow<Boolean> = eventId
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { id -> userRepository.isJoinedEventFlow(id) }
        .catch { emit(false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
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

    fun loadEvent(id: String) {
        _ui.update { it.copy(loading = true, error = null) }
        eventId.value = id
    }

    fun joinEvent() {
        val id = eventId.value ?: run {
            _ui.update { it.copy(error = "No event selected") }
            return
        }

        if (_ui.value.joining) return

        viewModelScope.launch {
            _ui.update { it.copy(joining = true, error = null) }
            try {
                userRepository.joinEvent(id)
            } catch (e: Exception) {
                _ui.update { it.copy(error = "Error trying to join event") }
            } finally {
                _ui.update { it.copy(joining = false) }
            }
        }
    }
}