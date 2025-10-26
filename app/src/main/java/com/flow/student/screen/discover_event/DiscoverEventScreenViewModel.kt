package com.flow.student.screen.discover_event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.database.dao.DepartmentDao
import com.database.dao.EventCategoryDao
import com.database.dao.EventDao
import com.database.entities.Department
import com.database.entities.Event
import com.database.entities.EventCategory
import com.util.likeWrap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiscoverEventScreenViewModel(
    private val eventDao: EventDao,
    private val departmentDao: DepartmentDao,
    private val eventCategoryDao: EventCategoryDao,
) : ViewModel() {
    data class UIState(
        val search: String = "",
        val fromDate: Long? = null,
        val toDate: Long? = null,
        val departmentId: Long? = null,
        val categoryEventId: Long? = null
    )

    private data class Params(
        val search: String?,
        val fromDate: Long?,
        val toDate: Long?,
        val departmentId: Long?,
        val categoryEventId: Long?
    )

    private val _ui = MutableStateFlow(UIState())
    val ui: StateFlow<UIState> = _ui.asStateFlow()

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments.asStateFlow()

    private val _eventCategories = MutableStateFlow<List<EventCategory>>(emptyList())
    val eventCategories: StateFlow<List<EventCategory>> = _eventCategories.asStateFlow()

    fun updateSearch(s: String) = _ui.update { it.copy(search = s) }
    fun setRangeDate(fromDate: Long?, toDate: Long?) = _ui.update { it.copy(fromDate = fromDate, toDate = toDate) }
    fun setDepartmentId(id: Long?) = _ui.update { it.copy(departmentId = id) }
    fun setCategoryId(id: Long?) = _ui.update { it.copy(categoryEventId = id) }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val paramsFlow: Flow<Params> =
        _ui.flatMapLatest { state ->
            combine(
                flowOf(state.copy(search = state.search))
                    .map { it.copy() },
                flowOf(state.search)
                    .debounce(300)
                    .map { it.trim().takeIf { s -> s.isNotEmpty() }?.let(::likeWrap) }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val events: Flow<List<Event>> =
        paramsFlow.flatMapLatest { params ->
            eventDao.observeAll(params.search, params.fromDate, params.toDate, params.departmentId, params.categoryEventId)
        }

    fun retrieveDepartments() {
        viewModelScope.launch {
            departmentDao.observeAll().collect { list ->
                _departments.value = list
            }
        }
    }

    fun retrieveCategories() {
        viewModelScope.launch {
            eventCategoryDao.observeAll().collect { list ->
                _eventCategories.value = list
            }
        }
    }
}