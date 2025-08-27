package com.thmanyah.task.presentation.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thmanyah.task.R
import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.domain.model.Language
import com.thmanyah.task.domain.model.SectionType
import com.thmanyah.task.presentation.ui.components.ContentSectionView
import com.thmanyah.task.presentation.ui.components.ErrorState
import com.thmanyah.task.presentation.ui.components.LoadingIndicator
import com.thmanyah.task.presentation.ui.components.SectionDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onContentItemClick: (ContentItem) -> Unit = {},
    onNavigateToSection: (String) -> Unit = {},
    onLanguageToggle: () -> Unit = {},
    currentLanguage: Language = Language.ENGLISH,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    var selectedFilter by remember { mutableStateOf<SectionType?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToSection -> onNavigateToSection(effect.sectionId)
                is HomeEffect.NavigateToContentDetail -> onContentItemClick(
                    state.sections.flatMap { it.items }
                        .first { it.id == effect.itemId }
                )
                is HomeEffect.ShowErrorSnackbar -> {
                }
                is HomeEffect.ShowRefreshSuccessMessage -> {
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    EnhancedTopBarTitle()
                },
                actions = {
                    TextButton(onClick = onLanguageToggle) {
                        Text(
                            text = if (currentLanguage == Language.ENGLISH) "EN" else "AR",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* TODO: Handle profile */ }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.sections.isEmpty() -> {
                    LoadingIndicator(
                        message = stringResource(R.string.loading_content),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                state.shouldShowErrorState -> {
                    val errorMsg = state.errorMessage ?: stringResource(R.string.unknown_error)
                    ErrorState(
                        message = errorMsg,
                        onRetry = { 
                            viewModel.handleIntent(HomeIntent.Retry)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                state.shouldShowEmptyState -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_content_available),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                state.shouldShowContent -> {
                    val filteredSections = selectedFilter?.let { filter ->
                        state.sections.filter { it.sectionType == filter }
                    } ?: state.sections
                    
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (state.sections.isNotEmpty()) {
                            ContentFilterChips(
                                sections = state.sections,
                                selectedFilter = selectedFilter,
                                onFilterSelected = { filter ->
                                    selectedFilter = if (selectedFilter == filter) null else filter
                                }
                            )
                        }
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            items(filteredSections) { section ->
                                Column {
                                    ContentSectionView(
                                    contentSection = section,
                                    onItemClick = { item ->
                                        viewModel.handleIntent(
                                            HomeIntent.OnContentItemClicked(item.id)
                                        )
                                    },
                                    onSectionHeaderClick = { sectionId ->
                                        viewModel.handleIntent(
                                            HomeIntent.OnSectionClicked(sectionId)
                                        )
                                    },
                                    onLoadMore = { sectionId ->
                                    }
                                )
                                SectionDivider()
                                }
                            }
                            
                            state.errorMessage?.let { error ->
                                if (state.sections.isNotEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = error,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedTopBarTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun ContentFilterChips(
    sections: List<com.thmanyah.task.domain.model.ContentSection>,
    selectedFilter: SectionType?,
    onFilterSelected: (SectionType) -> Unit
) {
    val availableTypes = sections.map { it.sectionType }.distinct()
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(availableTypes) { sectionType ->
            FilterChip(
                onClick = { onFilterSelected(sectionType) },
                label = {
                    Text(
                        text = when (sectionType) {
                            SectionType.PODCASTS -> stringResource(R.string.filter_podcasts)
                            SectionType.EPISODES -> stringResource(R.string.filter_episodes)
                            SectionType.AUDIOBOOKS -> stringResource(R.string.filter_audiobooks)
                            SectionType.AUDIO_ARTICLES -> stringResource(R.string.filter_audio_articles)
                            SectionType.MIXED -> stringResource(R.string.filter_mixed)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                },
                selected = selectedFilter == sectionType,
                leadingIcon = if (selectedFilter == sectionType) {
                    {
                        Text(
                            text = when (sectionType) {
                                SectionType.PODCASTS -> "🎧"
                                SectionType.EPISODES -> "▶️"
                                SectionType.AUDIOBOOKS -> "📚"
                                SectionType.AUDIO_ARTICLES -> "📰"
                                SectionType.MIXED -> "🔀"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else null
            )
        }
    }
}

