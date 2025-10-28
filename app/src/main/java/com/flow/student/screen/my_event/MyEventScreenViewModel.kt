package com.flow.student.screen.my_event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.entities.UserEventWithRefs
import com.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyEventScreenViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val events: StateFlow<List<UserEventWithRefs>> =
        userRepository.myEventsObserveAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _selectedEventId = MutableStateFlow<String?>(null)
    val selectedEventId:StateFlow<String?> = _selectedEventId.asStateFlow()

    fun setSelectedEventId(eventId: String?) {
        _selectedEventId.value = eventId
    }

    fun unjoinEvent(eventId: String) {
        viewModelScope.launch {
            try {
                userRepository.unjoinEvent(eventId)
            } catch (e: Throwable) {
                println(e)
            }
        }
    }
}