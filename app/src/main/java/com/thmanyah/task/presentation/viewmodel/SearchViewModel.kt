package com.thmanyah.task.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.model.SearchQuery
import com.thmanyah.task.domain.usecase.SearchContentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchContentUseCase: SearchContentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
        setupSearchDebouncing()
    }

    private fun setupSearchDebouncing() {
        _searchQuery
            .debounce(200L)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { query ->
                performSearch(query)
            }
            .launchIn(viewModelScope)
        _searchQuery
            .filter { it.isBlank() }
            .onEach {
                clearSearchResults()
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(currentQuery = query)
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                showSuggestions = false
            )

            searchContentUseCase(SearchQuery(query)).collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )

                    is Result.Success -> _uiState.value.copy(
                        searchResults = result.data.results,
                        totalCount = result.data.totalCount,
                        hasMore = result.data.hasMore,
                        isLoading = false,
                        errorMessage = null,
                        showSuggestions = false
                    )

                    is Result.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message ?: "Search failed",
                        showSuggestions = false
                    )
                }
            }
        }
    }

    fun onSuggestionClick(suggestion: String) {
        _searchQuery.value = suggestion
        _uiState.value = _uiState.value.copy(
            currentQuery = suggestion,
            showSuggestions = false
        )
    }

    fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            totalCount = 0,
            hasMore = false,
            isLoading = false,
            errorMessage = null
        )
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = SearchUiState()
        searchJob?.cancel()
    }

    fun onSearchResultClick(item: ContentItem) {
    }

    fun toggleSuggestions() {
        _uiState.value = _uiState.value.copy(
            showSuggestions = !_uiState.value.showSuggestions
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class SearchUiState(
    val currentQuery: String = "",
    val searchResults: List<ContentItem> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val totalCount: Int = 0,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showSuggestions: Boolean = false
)