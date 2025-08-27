package com.thmanyah.task.presentation.ui.screen.home

import com.thmanyah.task.presentation.arch.MviIntent

sealed interface HomeIntent : MviIntent {
    data object LoadHomeSections : HomeIntent
    data object RefreshHomeSections : HomeIntent
    data class OnSectionClicked(val sectionId: String) : HomeIntent
    data class OnContentItemClicked(val itemId: String) : HomeIntent
    data object ClearError : HomeIntent
    data object Retry : HomeIntent
}