package com.flow.cms.screen.event_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.RepoResult
import com.domain.entities.EventCategory
import com.repository.event_category.EventCategoryRepository
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


typealias ParamFlow = StateFlow<EventCategoryListScreenViewModel.Params>
typealias CategoriesFlow = StateFlow<List<EventCategory>>

@HiltViewModel
class EventCategoryListScreenViewModel @Inject constructor(
    private val categoryRepository: EventCategoryRepository
) : ViewModel() {
    sealed interface UiState {
        data object Idle : UiState
        data object Adding : UiState
        data class Updating(val category: EventCategory) : UiState
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

    private val _categories = MutableStateFlow<List<EventCategory>>(emptyList())
    val categories: CategoriesFlow = _categories.asStateFlow()

    fun updateParamSearch(s: String) = _params.update { it.copy(search = s) }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun observeCategories() {
        viewModelScope.launch {
            _params
                .map { it.search }
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { search ->
                    categoryRepository.observeAll(search)
                }
                .collect { list ->
                    _categories.value = list
                }
        }
    }

    fun setStateAdding() {
        _uiState.value = UiState.Adding
    }

    fun setStateUpdating(category: EventCategory) {
        _uiState.value = UiState.Updating(category)
    }

    fun addCategory(name: String?) {
        viewModelScope.launch {
            if (name.isNullOrBlank()) {
                return@launch
            }

            when (val result = categoryRepository.insert(EventCategory.create(name = name))) {
                is RepoResult.Success -> {
                    _uiState.value = UiState.Success("New category has been added")
                }
                is RepoResult.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun updateCategory(category: EventCategory) {
        viewModelScope.launch {
            if (category.name.isBlank()) {
                return@launch
            }

            val newCategory = EventCategory.create(
                id = category.id,
                name = category.name
            )

            when (val result = categoryRepository.update(newCategory)) {
                is RepoResult.Success -> {
                    _uiState.value = UiState.Success("Category has been updated")
                }
                is RepoResult.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }
}

