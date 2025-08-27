package com.thmanyah.task.presentation.ui.screen.search

import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.presentation.arch.MviEffect

sealed interface SearchEffect : MviEffect {
    data class NavigateToContentDetail(val item: ContentItem) : SearchEffect
    data class ShowErrorSnackbar(val message: String) : SearchEffect
    data object HideKeyboard : SearchEffect
    data object ScrollToTop : SearchEffect
}