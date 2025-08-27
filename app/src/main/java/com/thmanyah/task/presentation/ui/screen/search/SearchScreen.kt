package com.thmanyah.task.presentation.ui.screen.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thmanyah.task.R
import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.presentation.ui.components.ContentItemCard
import com.thmanyah.task.presentation.ui.components.ContentCardStyle
import com.thmanyah.task.presentation.ui.components.EmptyState
import com.thmanyah.task.presentation.ui.components.ErrorState
import com.thmanyah.task.presentation.ui.components.LoadingIndicator
import com.thmanyah.task.presentation.ui.components.SearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onContentItemClick: (ContentItem) -> Unit = {},
    viewModel: SearchMviViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.NavigateToContentDetail -> onContentItemClick(effect.item)
                is SearchEffect.ShowErrorSnackbar -> {
                }
                is SearchEffect.HideKeyboard -> {
                }
                is SearchEffect.ScrollToTop -> {
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchTextField(
                query = state.currentQuery,
                onQueryChange = { query ->
                    viewModel.handleIntent(SearchIntent.UpdateSearchQuery(query))
                },
                onClearClick = {
                    viewModel.handleIntent(SearchIntent.ClearSearch)
                },
                modifier = Modifier.padding(16.dp),
                placeholder = stringResource(R.string.search_placeholder)
            )

            when {
                state.shouldShowSuggestions -> {
                    SearchSuggestions(
                        suggestions = state.suggestions,
                        onSuggestionClick = { suggestion ->
                            viewModel.handleIntent(SearchIntent.OnSuggestionClicked(suggestion))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                state.isLoading -> {
                    LoadingIndicator(
                        message = stringResource(R.string.searching),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                state.errorMessage != null && state.searchResults.isEmpty() -> {
                    val errorMsg = state.errorMessage ?: stringResource(R.string.search_error)
                    ErrorState(
                        message = errorMsg,
                        onRetry = { 
                            viewModel.handleIntent(SearchIntent.ClearError)
                            if (state.currentQuery.isNotBlank()) {
                                viewModel.handleIntent(
                                    SearchIntent.UpdateSearchQuery(state.currentQuery)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                state.shouldShowResults -> {
                    SearchResults(
                        query = state.currentQuery,
                        results = state.searchResults,
                        onItemClick = { item ->
                            viewModel.handleIntent(SearchIntent.OnSearchResultClicked(item))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                state.shouldShowEmptyResults -> {
                    EmptyState(
                        message = stringResource(R.string.no_results_for, state.currentQuery),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                else -> {
                    EmptyState(
                        message = stringResource(R.string.search_your_favorite_content),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            state.errorMessage?.let { error ->
                if (state.searchResults.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchSuggestions(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.search_suggestions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(suggestions) { suggestion ->
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchResults(
    query: String,
    results: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.search_results_for, query),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.results_found, results.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(results) { item ->
            ContentItemCard(
                contentItem = item,
                style = ContentCardStyle.Standard,
                onClick = onItemClick
            )
        }
    }
}