package com.thmanyah.task.presentation.ui.screen.home

import com.thmanyah.task.presentation.arch.MviEffect

sealed interface HomeEffect : MviEffect {
    data class NavigateToSection(val sectionId: String) : HomeEffect
    data class NavigateToContentDetail(val itemId: String) : HomeEffect
    data class ShowErrorSnackbar(val message: String) : HomeEffect
    data object ShowRefreshSuccessMessage : HomeEffect
}