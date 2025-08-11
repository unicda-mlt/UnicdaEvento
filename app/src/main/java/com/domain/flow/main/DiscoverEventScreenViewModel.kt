package com.domain.flow.main

import androidx.lifecycle.ViewModel
import com.database.dao.EventDao
import com.database.entities.Event
import com.util.SYSTEM_ZONE
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
import java.time.LocalDate


class DiscoverEventScreenViewModel(
    private val eventDao: EventDao
) : ViewModel() {
    data class UIState(
        val search: String = "",
        val fromDate: LocalDate? = null,
        val toDate: LocalDate? = null,
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

    fun updateSearch(s: String) = _ui.update { it.copy(search = s) }
    fun setFromDate(d: LocalDate?) = _ui.update { it.copy(fromDate = d) }
    fun setToDate(d: LocalDate?) = _ui.update { it.copy(toDate = d) }
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
                val fromDate = params.fromDate
                    ?.atStartOfDay(SYSTEM_ZONE)?.toInstant()?.toEpochMilli()

                val toDate = params.toDate
                    ?.plusDays(1)?.atStartOfDay(SYSTEM_ZONE)?.toInstant()?.toEpochMilli()?.minus(1)

                Params(
                    search = searchLike,
                    fromDate = fromDate,
                    toDate = toDate,
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
}