package com.flow.student.screen.discover_event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.entities.Department
import com.domain.entities.Event
import com.domain.entities.EventCategory
import com.repository.department.DepartmentRepository
import com.repository.event.EventRepository
import com.repository.event_category.EventCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject


@HiltViewModel
class DiscoverEventScreenViewModel @Inject constructor(
    private val departmentRepository: DepartmentRepository,
    private val eventCategoryRepository: EventCategoryRepository,
    private val eventRepository: EventRepository
) : ViewModel() {
    data class Params(
        val search: String = "",
        val fromDate: Long? = null,
        val toDate: Long? = null,
        val departmentId: String? = null,
        val categoryEventId: String? = null
    )

    private val _params = MutableStateFlow(Params())
    val params: StateFlow<Params> = _params.asStateFlow()

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments.asStateFlow()

    private val _eventCategories = MutableStateFlow<List<EventCategory>>(emptyList())
    val eventCategories: StateFlow<List<EventCategory>> = _eventCategories.asStateFlow()

    init {
        setRangeDate(null, null)
    }

    fun updateSearch(s: String) = _params.update { it.copy(search = s) }
    fun setRangeDate(fromDate: Long?, toDate: Long?) = _params.update { it.copy(fromDate = fromDate ?: todayStartInMillis(), toDate = toDate) }
    fun setDepartmentId(id: String?) = _params.update { it.copy(departmentId = id) }
    fun setCategoryId(id: String?) = _params.update { it.copy(categoryEventId = id) }

    private fun todayStartInMillis(): Long {
        val now = LocalDate.now()
        return now.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val paramsFlow: Flow<Params> =
        _params.flatMapLatest { state ->
            combine(
                flowOf(state.copy(search = state.search))
                    .map { it.copy() },
                flowOf(state.search)
                    .debounce(300)
                    .map { it.trim() }
                    .distinctUntilChanged()
            ) { params, searchLike ->
                Params(
                    search = searchLike,
                    fromDate = params.fromDate,
                    toDate = params.toDate,
                    departmentId = params.departmentId,
                    categoryEventId = params.categoryEventId
                )
            }
        }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val events: Flow<List<Event>> =
        paramsFlow.debounce(250)
            .distinctUntilChanged()
            .mapLatest { params ->
            eventRepository.getAll(
                params.search,
                params.fromDate,
                params.toDate,
                params.departmentId,
                params.categoryEventId
            )
        }.catch { e -> println(e) }

    fun retrieveDepartments() {
        viewModelScope.launch {
            departmentRepository.observeAll().collect { list ->
                _departments.value = list
            }
        }
    }

    fun retrieveCategories() {
        viewModelScope.launch {
            eventCategoryRepository.observeAll().collect { list ->
                _eventCategories.value = list
            }
        }
    }
}