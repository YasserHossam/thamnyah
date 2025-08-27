package com.thmanyah.task.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thmanyah.task.domain.model.Language
import com.thmanyah.task.presentation.ui.screen.home.HomeScreen
import com.thmanyah.task.presentation.ui.screen.search.SearchScreen

@Composable
fun ThamanyaNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ThamanyaDestinations.HOME,
    currentLanguage: Language = Language.ENGLISH,
    onLanguageToggle: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ThamanyaDestinations.HOME) {
            HomeScreen(
                onNavigateToSearch = {
                    navController.navigate(ThamanyaDestinations.SEARCH)
                },
                onContentItemClick = { contentItem ->
                },
                onLanguageToggle = onLanguageToggle,
                currentLanguage = currentLanguage
            )
        }
        
        composable(ThamanyaDestinations.SEARCH) {
            SearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onContentItemClick = { contentItem ->
                }
            )
        }
    }
}

object ThamanyaDestinations {
    const val HOME = "home"
    const val SEARCH = "search"
}