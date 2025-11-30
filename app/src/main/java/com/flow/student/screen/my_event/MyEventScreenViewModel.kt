package com.flow.student.screen.my_event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.entities.UserEventWithRefs
import com.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class MyEventScreenViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private data class Params(
        val fromDate: Long? = null
    )

    private val _params = MutableStateFlow(Params())

    private val _selectedEventId = MutableStateFlow<String?>(null)
    val selectedEventId:StateFlow<String?> = _selectedEventId.asStateFlow()

    init {
        startDailyTicker()
        setFromDate(null)
    }

    private fun setFromDate(fromDate: Long?) = _params.update { it.copy(fromDate = fromDate ?: todayStartInMillis()) }

    private fun todayStartInMillis(): Long {
        val now = LocalDate.now()
        return now.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun startDailyTicker() {
        viewModelScope.launch {
            while (isActive) {
                val now = LocalDateTime.now()
                val nextMidnight = now.toLocalDate()
                    .plusDays(1)
                    .atStartOfDay()
                val nowMillis = System.currentTimeMillis()
                val midnightMillis = nextMidnight
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val delayMillis = midnightMillis - nowMillis

                delay(delayMillis)

                setFromDate(fromDate = todayStartInMillis())
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val events: StateFlow<List<UserEventWithRefs>> =
        _params
            .map { it.fromDate }
            .distinctUntilChanged()
            .flatMapLatest { fromDate ->
                userRepository.myEventsObserveAll(
                    fromDate = fromDate
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

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