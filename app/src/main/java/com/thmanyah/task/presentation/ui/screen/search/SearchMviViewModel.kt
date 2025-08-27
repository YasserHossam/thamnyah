package com.thmanyah.task.presentation.ui.screen.search

import androidx.lifecycle.viewModelScope
import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.model.SearchQuery
import com.thmanyah.task.domain.usecase.SearchContentUseCase
import com.thmanyah.task.presentation.arch.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchMviViewModel @Inject constructor(
    private val searchContentUseCase: SearchContentUseCase
) : BaseMviViewModel<SearchIntent, SearchState, SearchEffect>() {

    override fun createInitialState(): SearchState = SearchState()

    private val searchQueryFlow = MutableStateFlow("")
    private var searchJob: Job? = null

    init {
        setupSearchDebouncing()
    }

    override fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            is SearchIntent.OnSuggestionClicked -> handleSuggestionClick(intent.suggestion)
            is SearchIntent.OnSearchResultClicked -> handleSearchResultClick(intent.item)
            is SearchIntent.ClearSearch -> clearSearch()
            is SearchIntent.ClearError -> clearError()
            is SearchIntent.ToggleSuggestions -> toggleSuggestions()
            is SearchIntent.LoadMoreResults -> loadMoreResults()
        }
    }

    private fun setupSearchDebouncing() {
        searchQueryFlow
            .debounce(200L)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { query ->
                performSearch(query)
            }
            .launchIn(viewModelScope)

        searchQueryFlow
            .filter { it.isBlank() }
            .onEach {
                clearSearchResults()
            }
            .launchIn(viewModelScope)
    }

    private fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
        setState { 
            copy(
                currentQuery = query,
                showSuggestions = query.isNotBlank() && suggestions.isNotEmpty(),
                hasSearched = false
            ) 
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            setState {
                copy(
                    isLoading = true,
                    errorMessage = null,
                    showSuggestions = false,
                    hasSearched = true
                )
            }

            searchContentUseCase(SearchQuery(query)).collect { result ->
                when (result) {
                    is Result.Loading -> setState {
                        copy(isLoading = true, errorMessage = null)
                    }

                    is Result.Success -> setState {
                        copy(
                            searchResults = result.data.results,
                            isLoading = false,
                            errorMessage = null,
                            showSuggestions = false
                        )
                    }

                    is Result.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = result.exception.message ?: "Search failed",
                                showSuggestions = false
                            )
                        }
                        setEffect {
                            SearchEffect.ShowErrorSnackbar(
                                result.exception.message ?: "Search failed"
                            ) 
                        }
                    }
                }
            }
        }
    }

    private fun handleSuggestionClick(suggestion: String) {
        searchQueryFlow.value = suggestion
        setState {
            copy(
                currentQuery = suggestion,
                showSuggestions = false
            )
        }
        setEffect { SearchEffect.HideKeyboard }
    }

    private fun handleSearchResultClick(item: ContentItem) {
        setEffect { SearchEffect.NavigateToContentDetail(item) }
    }

    private fun clearSearchResults() {
        setState {
            copy(
                searchResults = emptyList(),
                isLoading = false,
                errorMessage = null,
                hasSearched = false
            )
        }
    }

    private fun clearSearch() {
        searchQueryFlow.value = ""
        searchJob?.cancel()
        setState { SearchState() }
    }

    private fun clearError() {
        setState { copy(errorMessage = null) }
    }

    private fun toggleSuggestions() {
        setState {
            copy(showSuggestions = !showSuggestions)
        }
    }

    private fun loadMoreResults() {
        if (!currentState.canLoadMore) return
        
        setState { copy(isLoadingMore = true) }
        setEffect { SearchEffect.ScrollToTop }
    }
}