package com.thmanyah.task.presentation.ui.screen.search

import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.presentation.arch.MviState

data class SearchState(
    val currentQuery: String = "",
    val searchResults: List<ContentItem> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val showSuggestions: Boolean = false,
    val hasSearched: Boolean = false
) : MviState

val SearchState.hasError: Boolean
    get() = errorMessage != null

val SearchState.shouldShowResults: Boolean
    get() = searchResults.isNotEmpty() && !hasError

val SearchState.shouldShowEmptyResults: Boolean
    get() = searchResults.isEmpty() && hasSearched && !isLoading && !hasError

val SearchState.shouldShowSuggestions: Boolean
    get() = showSuggestions && suggestions.isNotEmpty()

val SearchState.canLoadMore: Boolean
    get() = !isLoading && !isLoadingMore