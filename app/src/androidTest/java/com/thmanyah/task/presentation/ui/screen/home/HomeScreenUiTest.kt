package com.thmanyah.task.presentation.ui.screen.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.thmanyah.task.domain.model.*
import com.thmanyah.task.presentation.ui.components.ContentSectionView
import org.junit.Rule
import org.junit.Test

class HomeScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val podcastSection = ContentSection(
        id = "Top Podcasts_1",
        title = "Top Podcasts",
        sectionType = SectionType.PODCASTS,
        layoutType = SectionLayout.SQUARE_GRID,
        items = listOf(
            ContentItem.Podcast(
                id = "1503226625",
                title = "Consider This from NPR",
                description = "The hosts of NPR's All Things Considered help you make sense of a major news story...",
                imageUrl = "https://media.npr.org/assets/img/2022/09/22/consider-this_tile_npr-network-01_sq-96ca581b062bc4641008f69cf1e071394ce4c611.jpg?s=1400&c=66&f=jpg",
                author = null,
                duration = "1138158",
                episodeCount = 1423
            ),
            ContentItem.Podcast(
                id = "1057255460",
                title = "The NPR Politics Podcast",
                description = "Every weekday, NPR's best political reporters are there to explain the big news...",
                imageUrl = "https://media.npr.org/assets/img/2024/01/11/podcast-politics_2023_update1_sq-be7ef464dd058fe663d9e4cfe836fb9309ad0a4d.jpg?s=1400&c=66&f=jpg",
                author = null,
                duration = "2037728",
                episodeCount = 1750
            )
        ),
        order = 1,
        hasMore = false
    )

    private val episodeSection = ContentSection(
        id = "Trending Episodes_2",
        title = "Trending Episodes",
        sectionType = SectionType.EPISODES,
        layoutType = SectionLayout.BINARY_GRID,
        items = listOf(
            ContentItem.Episode(
                id = "4bd398b5-9619-5806-81b6-613154f79044",
                title = "NPR News: 07-23-2024 4PM EDT",
                description = "NPR News: 07-23-2024 4PM EDT...",
                imageUrl = "https://media.npr.org/assets/img/2023/03/01/npr-news-now_square.png?s=1400&c=66",
                author = "",
                duration = "300",
                publishDate = "2024-07-23T20:00:00.000Z",
                podcastId = "121493675",
                episodeNumber = null,
                audioUrl = "https://chrt.fm/track/138C95/prfx.byspotify.com/e/play.podtrac.com/npr-500005/traffic.megaphone.fm/NPR9836101076.mp3?p=500005&e=nsv2-1721764800000-s1-long&d=300&t=podcast&size=4214859"
            )
        ),
        order = 2,
        hasMore = false
    )

    private val audioBookSection = ContentSection(
        id = "Bestselling Audiobooks_3",
        title = "Bestselling Audiobooks",
        sectionType = SectionType.AUDIOBOOKS,
        layoutType = SectionLayout.LARGE_CARDS,
        items = listOf(
            ContentItem.AudioBook(
                id = "audiobook_001",
                title = "The Art of War",
                description = "An ancient military text on strategy and tactics.",
                imageUrl = "https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228",
                author = "Sun Tzu",
                duration = "36000",
                publishDate = "2023-01-10T08:00:00Z",
                narrator = "Sun Tzu",
                chapters = null,
                language = "en"
            )
        ),
        order = 3,
        hasMore = false
    )

    @Test
    fun showsSectionTitles_andFirstItemTitles() {
        composeTestRule.setContent {
            ContentSectionView(contentSection = podcastSection)
            ContentSectionView(contentSection = episodeSection)
            ContentSectionView(contentSection = audioBookSection)
        }

        // Section titles
        composeTestRule.onNodeWithText("Top Podcasts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Trending Episodes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bestselling Audiobooks").assertIsDisplayed()

        // First item titles
        composeTestRule.onNodeWithText("Consider This from NPR").assertIsDisplayed()
        composeTestRule.onNodeWithText("NPR News: 07-23-2024 4PM EDT").assertIsDisplayed()
        composeTestRule.onNodeWithText("The Art of War").assertIsDisplayed()
    }

    @Test
    fun showsFilterChips_forSectionTypes() {
        composeTestRule.setContent {
            ContentFilterChips(
                sections = listOf(podcastSection, episodeSection, audioBookSection),
                selectedFilter = null,
                onFilterSelected = {}
            )
        }

        // Use the actual string values as shown in your UI
        composeTestRule.onNodeWithText("Podcasts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Episodes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Books").assertIsDisplayed()
    }
}

