package com.thmanyah.task.presentation.ui.screen.home

import com.thmanyah.task.domain.model.ContentSection
import com.thmanyah.task.presentation.arch.MviState

data class HomeState(
    val sections: List<ContentSection> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
) : MviState

val HomeState.hasError: Boolean
    get() = errorMessage != null

val HomeState.shouldShowContent: Boolean
    get() = sections.isNotEmpty() && !hasError

val HomeState.shouldShowEmptyState: Boolean
    get() = isEmpty && !isLoading && !hasError

val HomeState.shouldShowErrorState: Boolean
    get() = hasError && !isLoading