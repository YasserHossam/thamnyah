package com.thmanyah.task.presentation.ui.screen.search

import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.presentation.arch.MviIntent

sealed interface SearchIntent : MviIntent {
    data class UpdateSearchQuery(val query: String) : SearchIntent
    data class OnSuggestionClicked(val suggestion: String) : SearchIntent
    data class OnSearchResultClicked(val item: ContentItem) : SearchIntent
    data object ClearSearch : SearchIntent
    data object ClearError : SearchIntent
    data object ToggleSuggestions : SearchIntent
    data object LoadMoreResults : SearchIntent
}