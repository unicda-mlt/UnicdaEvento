package com.flow.cms.screen.event_department

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.RepoResult
import com.domain.entities.Department
import com.repository.department.DepartmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


typealias ParamFlow = StateFlow<EventDepartmentListScreenViewModel.Params>
typealias DepartmentsFlow = StateFlow<List<Department>>

@HiltViewModel
class EventDepartmentListScreenViewModel @Inject constructor(
    private val departmentRepository: DepartmentRepository
) : ViewModel() {
    sealed interface UiState {
        data object Idle : UiState
        data object Adding : UiState
        data class Updating(val department: Department) : UiState
        data class Success(val message: String) : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    data class Params(
        val search: String = ""
    )

    private val _params = MutableStateFlow(Params())
    val params: ParamFlow = _params.asStateFlow()

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: DepartmentsFlow = _departments.asStateFlow()

    fun updateParamSearch(s: String) = _params.update { it.copy(search = s) }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun observeDepartments() {
        viewModelScope.launch {
            _params
                .map { it.search }
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { search ->
                    departmentRepository.observeAll(search)
                }
                .collect { list ->
                    _departments.value = list
                }
        }
    }

    fun setStateAdding() {
        _uiState.value = UiState.Adding
    }

    fun setStateUpdating(department: Department) {
        _uiState.value = UiState.Updating(department)
    }

    fun addDepartment(name: String?) {
        viewModelScope.launch {
            if (name.isNullOrBlank()) {
                return@launch
            }

            when (val result = departmentRepository.insert(Department.create(name = name))) {
                is RepoResult.Success -> {
                    _uiState.value = UiState.Success("New department has been added")
                }
                is RepoResult.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun updateDepartment(department: Department) {
        viewModelScope.launch {
            if (department.name.isBlank()) {
                return@launch
            }

            val newDepartment = Department.create(
                id = department.id,
                name = department.name
            )

            when (val result = departmentRepository.update(newDepartment)) {
                is RepoResult.Success -> {
                    _uiState.value = UiState.Success("Department has been updated")
                }
                is RepoResult.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }
}

