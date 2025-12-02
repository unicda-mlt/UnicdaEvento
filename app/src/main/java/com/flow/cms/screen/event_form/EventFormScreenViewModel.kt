package com.flow.cms.screen.event_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.RepoResult
import com.domain.entities.Department
import com.domain.entities.EditableEvent
import com.domain.entities.Event
import com.domain.entities.EventCategory
import com.domain.entities.toEditable
import com.google.android.gms.maps.model.LatLng
import com.repository.department.DepartmentRepository
import com.repository.event.EventRepository
import com.repository.event_category.EventCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EventFormScreenViewModel @Inject constructor(
    private val departmentRepository: DepartmentRepository,
    private val eventCategoryRepository: EventCategoryRepository,
    private val eventRepository: EventRepository
) : ViewModel() {
    data class UIState(
        val loading: Boolean = false,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _ui

    private val _formHasSaved = MutableStateFlow(false)
    val formHasSaved: StateFlow<Boolean> = _formHasSaved.asStateFlow()

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments.asStateFlow()

    private val _categories = MutableStateFlow<List<EventCategory>>(emptyList())
    val categories: StateFlow<List<EventCategory>> = _categories.asStateFlow()

    private val _eventId = MutableStateFlow<String?>(null)

    private val _event = MutableStateFlow(EditableEvent())

    val departmentId: StateFlow<String?> = eventProperty { it.departmentId }
    val categoryId: StateFlow<String?> = eventProperty { it.eventCategoryId }
    val title: StateFlow<String?> = eventProperty { it.title }
    val description: StateFlow<String?> = eventProperty { it.description }
    val location: StateFlow<String?> = eventProperty { it.location }
    val latLng: StateFlow<LatLng?> = eventProperty { it ->
        if (it.latitude == null || it.longitude == null) {
            null
        }
        else {
            LatLng(it.latitude, it.longitude)
        }
    }
    val startDate: StateFlow<Long?> = eventProperty { it.startDate }
    val endDate: StateFlow<Long?> = eventProperty { it.endDate }
    val principalImage: StateFlow<String?> = eventProperty { it.principalImage }

    init {
        _eventId
            .filterNotNull()
            .onEach { _ui.update { it.copy(loading = true, error = null) } }
            .launchIn(viewModelScope)

        _event
            .onEach { _ui.update { it.copy(loading = false) } }
            .launchIn(viewModelScope)

        observeEvent()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> eventProperty(
        selector: (EditableEvent) -> T
    ): StateFlow<T?> =
        _event
            .map { e -> e?.let(selector) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

    fun updateDepartmentId(id: String?) { _event.update { it.copy(departmentId = id) } }
    fun updateCategoryId(id: String?) { _event.update { it.copy(eventCategoryId = id) } }
    fun updateTitle(title: String?) { _event.update { it.copy(title = title) } }
    fun updateDescription(description: String?) { _event.update { it.copy(description = description) } }
    fun updateLocation(location: String?) { _event.update { it.copy(location = location) } }
    fun updateLatLng(latLng: LatLng?) { _event.update { it.copy(latitude = latLng?.latitude, longitude = latLng?.longitude) } }
    fun updateStartDate(date: Long?) { _event.update { it.copy(startDate = date) } }
    fun updateEndDate(date: Long?) { _event.update { it.copy(endDate = date) } }
    fun updatePrincipalImage(url: String?) { _event.update { it.copy(principalImage = url) } }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeEvent() {
        viewModelScope.launch {
            _eventId
                .filterNotNull()
                .distinctUntilChanged()
                .mapLatest { id ->
                    eventRepository.getById(id)
                }
                .collect { loaded ->
                    val event = loaded?.toEditable()?.copy()

                    if (event == null) {
                        _event.value = EditableEvent()
                    }
                    else {
                        _event.value = event
                    }
                }
        }
    }

    fun loadEvent(id: String) {
        if (id == _eventId.value) {
            return
        }

        _ui.update { it.copy(loading = true, error = null) }
        _eventId.value = id
    }

    fun retrieveDepartments() {
        viewModelScope.launch {
            _departments.value =  departmentRepository.getAll()
        }
    }

    fun retrieveCategories() {
        viewModelScope.launch {
            _categories.value = eventCategoryRepository.getAll()
        }
    }

    fun save() {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }

            val event = _event.value
            val departmentId = event.departmentId
            val eventCategoryId = event.eventCategoryId
            val title = event.title
            val description = event.description
            val location = event.location
            val latitude = event.latitude
            val longitude = event.longitude
            val startDate = event.startDate
            val endDate = event.endDate
            val principalImage = event.principalImage

            if (departmentId.isNullOrBlank()) {
                _ui.value = UIState(error = "Must select a department")
                return@launch
            }

            if (eventCategoryId.isNullOrBlank()) {
                _ui.value = UIState(error = "Must select a category")
                return@launch
            }

            if (title.isNullOrBlank()) {
                _ui.value = UIState(error = "Title can not be empty")
                return@launch
            }

            if (description.isNullOrBlank()) {
                _ui.value = UIState(error = "Description can not be empty")
                return@launch
            }

            if (location.isNullOrBlank()) {
                _ui.value = UIState(error = "Location can not be empty")
                return@launch
            }

            if (latitude == null || longitude == null) {
                _ui.value = UIState(error = "Must select a location")
                return@launch
            }

            if (startDate == null) {
                _ui.value = UIState(error = "Start date can not be empty")
                return@launch
            }

            if (endDate == null) {
                _ui.value = UIState(error = "End date can not be empty")
                return@launch
            }

            if (startDate >= endDate) {
                _ui.value = UIState(error = "End date must be greater than start date")
                return@launch
            }

            val eventToSave = Event(
                id = _eventId.value ?: "",
                departmentId = departmentId,
                eventCategoryId = eventCategoryId,
                title = title,
                description = description,
                location = location,
                latitude = latitude,
                longitude = longitude,
                startDate = startDate,
                endDate = endDate,
                principalImage = principalImage,
            )

            val result =
                if (_eventId.value == null) {
                    eventRepository.insert(eventToSave)
                }
                else {
                    eventRepository.update(eventToSave)
                }

            when (result) {
                is RepoResult.Success -> {
                    _formHasSaved.value = true
                }
                is RepoResult.Error -> {
                    _ui.value = UIState(error = "Error when try to save form")
                }
            }
        }
    }
}