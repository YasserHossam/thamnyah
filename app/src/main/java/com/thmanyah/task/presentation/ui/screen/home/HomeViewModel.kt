package com.thmanyah.task.presentation.ui.screen.home

import androidx.lifecycle.viewModelScope
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.usecase.GetHomeSectionsUseCase
import com.thmanyah.task.domain.usecase.RefreshHomeSectionsUseCase
import com.thmanyah.task.presentation.arch.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeSectionsUseCase: GetHomeSectionsUseCase,
    private val refreshHomeSectionsUseCase: RefreshHomeSectionsUseCase
) : BaseMviViewModel<HomeIntent, HomeState, HomeEffect>() {

    override fun createInitialState(): HomeState = HomeState(isLoading = true)

    init {
        handleIntent(HomeIntent.LoadHomeSections)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadHomeSections -> loadHomeSections()
            is HomeIntent.RefreshHomeSections -> refreshHomeSections()
            is HomeIntent.OnSectionClicked -> handleSectionClick(intent.sectionId)
            is HomeIntent.OnContentItemClicked -> handleContentItemClick(intent.itemId)
            is HomeIntent.ClearError -> clearError()
            is HomeIntent.Retry -> retryLoadingSections()
        }
    }

    private fun loadHomeSections() {
        viewModelScope.launch {
            getHomeSectionsUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> setState {
                        copy(isLoading = true, errorMessage = null)
                    }
                    
                    is Result.Success -> setState {
                        copy(
                            sections = result.data,
                            isLoading = false,
                            errorMessage = null,
                            isEmpty = result.data.isEmpty()
                        )
                    }
                    
                    is Result.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = result.exception.message ?: "Failed to load content",
                                isEmpty = sections.isEmpty()
                            )
                        }
                        setEffect {
                            HomeEffect.ShowErrorSnackbar(
                                result.exception.message ?: "Failed to load content"
                            ) 
                        }
                    }
                }
            }
        }
    }

    private fun refreshHomeSections() {
        if (currentState.isRefreshing) return

        viewModelScope.launch {
            setState { copy(isRefreshing = true, errorMessage = null) }
            
            when (val result = refreshHomeSectionsUseCase()) {
                is Result.Success -> {
                    setState {
                        copy(
                            sections = result.data,
                            isRefreshing = false,
                            errorMessage = null,
                            isEmpty = result.data.isEmpty()
                        )
                    }
                    setEffect { HomeEffect.ShowRefreshSuccessMessage }
                }
                
                is Result.Error -> {
                    setState {
                        copy(
                            isRefreshing = false,
                            errorMessage = result.exception.message ?: "Failed to refresh"
                        )
                    }
                    setEffect {
                        HomeEffect.ShowErrorSnackbar(
                            result.exception.message ?: "Failed to refresh"
                        ) 
                    }
                }
                
                is Result.Loading -> {
                    setState { copy(isRefreshing = true) }
                }
            }
        }
    }

    private fun handleSectionClick(sectionId: String) {
        setEffect { HomeEffect.NavigateToSection(sectionId) }
    }

    private fun handleContentItemClick(itemId: String) {
        setEffect { HomeEffect.NavigateToContentDetail(itemId) }
    }

    private fun clearError() {
        setState { copy(errorMessage = null) }
    }

    private fun retryLoadingSections() {
        handleIntent(HomeIntent.LoadHomeSections)
    }
}