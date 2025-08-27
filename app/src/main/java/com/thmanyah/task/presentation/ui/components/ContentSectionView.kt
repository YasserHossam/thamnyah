package com.thmanyah.task.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thmanyah.task.R
import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.domain.model.ContentSection
import com.thmanyah.task.domain.model.SectionLayout

@Composable
fun ContentSectionView(
    contentSection: ContentSection,
    modifier: Modifier = Modifier,
    onItemClick: (ContentItem) -> Unit = {},
    onSectionHeaderClick: (String) -> Unit = {},
    onLoadMore: (String) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SectionHeader(
            title = getLocalizedSectionName(contentSection.title),
            hasMore = contentSection.hasMore,
            onHeaderClick = { onSectionHeaderClick(contentSection.id) },
            onLoadMoreClick = { onLoadMore(contentSection.id) }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (contentSection.layoutType) {
            SectionLayout.HORIZONTAL_LIST -> {
                HorizontalContentList(
                    items = contentSection.items,
                    onItemClick = onItemClick
                )
            }
            SectionLayout.BINARY_GRID -> {
                BinaryGridContent(
                    items = contentSection.items,
                    onItemClick = onItemClick
                )
            }
            SectionLayout.SQUARE_GRID -> {
                SquareGridContent(
                    items = contentSection.items,
                    onItemClick = onItemClick
                )
            }
            SectionLayout.VERTICAL_LIST -> {
                VerticalContentList(
                    items = contentSection.items,
                    onItemClick = onItemClick
                )
            }
            SectionLayout.LARGE_CARDS -> {
                LargeCardsContent(
                    items = contentSection.items,
                    onItemClick = onItemClick
                )
            }
            SectionLayout.QUEUE -> {
                QueueContentList(
                    items = contentSection.items,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    hasMore: Boolean,
    onHeaderClick: () -> Unit,
    onLoadMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        
        if (hasMore) {
            TextButton(
                onClick = onLoadMoreClick
            ) {
                Text(
                    text = "View More",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun HorizontalContentList(
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            ContentItemCard(
                contentItem = item,
                style = ContentCardStyle.Compact,
                onClick = onItemClick
            )
        }
    }
}

@Composable
private fun BinaryGridContent(
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier.height(260.dp)
    ) {
        items(items) { item ->
            ContentItemCard(
                contentItem = item,
                style = ContentCardStyle.Square,
                onClick = onItemClick,
                modifier = Modifier.width(140.dp)
            )
        }
    }
}

@Composable
private fun SquareGridContent(
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(180.dp)
    ) {
        items(items) { item ->
            ContentItemCard(
                contentItem = item,
                style = ContentCardStyle.Square,
                onClick = onItemClick,
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
private fun VerticalContentList(
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.take(3).forEach { item ->
            ContentItemCard(
                contentItem = item,
                style = ContentCardStyle.Standard,
                onClick = onItemClick
            )
        }
    }
}

@Composable
private fun LargeCardsContent(
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(items) { item ->
            ContentItemCard(
                contentItem = item,
                style = ContentCardStyle.Large,
                onClick = onItemClick,
                modifier = Modifier.width(340.dp)
            )
        }
    }
}

@Composable
private fun QueueContentList(
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(280.dp)
    ) {
        items(items) { item ->
            ContentItemCard(
                contentItem = item,
                style = ContentCardStyle.Standard,
                onClick = onItemClick,
                modifier = Modifier.width(300.dp)
            )
        }
    }
}

@Composable
private fun getLocalizedSectionName(apiSectionName: String): String {
    return when (apiSectionName) {
        "Top Podcasts" -> stringResource(R.string.section_top_podcasts)
        "Trending Episodes" -> stringResource(R.string.section_trending_episodes)
        "Bestselling Audiobooks" -> stringResource(R.string.section_bestselling_audiobooks)
        "Must-Read Audio Articles" -> stringResource(R.string.section_must_read_audio_articles)
        "Editor's Pick Episodes" -> stringResource(R.string.section_editors_pick_episodes)
        "Popular in Audiobooks" -> stringResource(R.string.section_popular_audiobooks)
        "New Podcasts" -> stringResource(R.string.section_popular_audiobooks)
        else -> apiSectionName // Fallback to original name if no translation found
    }
}