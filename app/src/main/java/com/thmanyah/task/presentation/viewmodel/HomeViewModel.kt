package com.thmanyah.task.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thmanyah.task.domain.model.ContentSection
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.usecase.GetHomeSectionsUseCase
import com.thmanyah.task.domain.usecase.RefreshHomeSectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeSectionsUseCase: GetHomeSectionsUseCase,
    private val refreshHomeSectionsUseCase: RefreshHomeSectionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeSections()
    }

    fun loadHomeSections() {
        viewModelScope.launch {
            getHomeSectionsUseCase().collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is Result.Success -> _uiState.value.copy(
                        sections = result.data,
                        isLoading = false,
                        errorMessage = null
                    )
                    is Result.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message ?: "An error occurred"
                    )
                }
            }
        }
    }

    fun refreshSections() {
        if (_uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)
            
            when (val result = refreshHomeSectionsUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        sections = result.data,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        errorMessage = result.exception.message ?: "Failed to refresh"
                    )
                }
                is Result.Loading -> {
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun onSectionClick(sectionId: String) {
    }

    fun onContentItemClick(itemId: String) {
    }
}

data class HomeUiState(
    val sections: List<ContentSection> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)