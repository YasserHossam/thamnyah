package com.thmanyah.task.data.mapper

import com.thmanyah.task.data.remote.dto.ContentSectionDto
import com.thmanyah.task.data.remote.dto.HomeItemDto
import com.thmanyah.task.data.remote.dto.HomeSectionsResponseDto
import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.domain.model.SectionLayout
import com.thmanyah.task.domain.model.SectionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeContentMapperTest {

    @Test
    fun `should map HomeSectionsResponseDto to domain correctly`() {
        val dto = HomeSectionsResponseDto(
            sections = listOf(
                ContentSectionDto(
                    title = "Test Section",
                    sectionType = "podcast",
                    layoutType = "binary_grid",
                    items = listOf(
                        HomeItemDto(
                            podcastId = "item1",
                            title = "Test Podcast",
                            authorName = "Test Author"
                        )
                    ),
                    order = 1,
                    hasMore = true
                )
            )
        )

        val result = HomeContentMapper.mapHomeSectionsResponse(dto)

        assertEquals(1, result.size)
        val section = result.first()
        assertEquals("Test Section_1", section.id)
        assertEquals("Test Section", section.title)
        assertEquals(SectionType.PODCASTS, section.sectionType)
        assertEquals(SectionLayout.BINARY_GRID, section.layoutType)
        assertEquals(1, section.order)
        assertTrue(section.hasMore)
        assertEquals(1, section.items.size)

        val item = section.items.first()
        assertTrue(item is ContentItem.Podcast)
        assertEquals("item1", item.id)
        assertEquals("Test Podcast", item.title)
        assertEquals("Test Author", item.author)
    }

    @Test
    fun `should map ContentItemDto to Podcast correctly`() {
        val dto = HomeItemDto(
            podcastId = "1",
            title = "Test Podcast",
            description = "Test Description",
            imageUrl = "https://example.com/image.jpg",
            authorName = "Test Author",
            duration = 30,
            releaseDate = "2024-01-01",
            episodeCount = 50
        )

        val result = HomeContentMapper.mapHomeSectionsResponse(
            HomeSectionsResponseDto(
                sections = listOf(
                    ContentSectionDto(
                        title = "Test",
                        sectionType = "podcast",
                        layoutType = "binary_grid",
                        items = listOf(dto),
                        order = 1
                    )
                )
            )
        )

        val item = result.first().items.first()
        assertTrue(item is ContentItem.Podcast)
        assertEquals("1", item.id)
        assertEquals("Test Podcast", item.title)
        assertEquals("Test Description", item.description)
        assertEquals("https://example.com/image.jpg", item.imageUrl)
        assertEquals("Test Author", item.author)
        assertEquals("30", item.duration)
        assertEquals("2024-01-01", item.publishDate)
    }

    @Test
    fun `should map ContentItemDto to Episode correctly`() {
        val dto = HomeItemDto(
            episodeId = "2",
            title = "Test Episode",
            episodeNumber = 5,
            audioUrl = "https://example.com/audio.mp3"
        )

        val result = HomeContentMapper.mapHomeSectionsResponse(
            HomeSectionsResponseDto(
                sections = listOf(
                    ContentSectionDto(
                        title = "Test",
                        sectionType = "episode",
                        layoutType = "binary_grid",
                        items = listOf(dto),
                        order = 1
                    )
                )
            )
        )

        val item = result.first().items.first()
        assertTrue(item is ContentItem.Episode)
        val episode = item as ContentItem.Episode
        assertEquals("2", episode.id)
        assertEquals("Test Episode", episode.title)
        assertEquals(5, episode.episodeNumber)
        assertEquals("https://example.com/audio.mp3", episode.audioUrl)
    }

    @Test
    fun `should map ContentItemDto to AudioBook correctly`() {
        val dto = HomeItemDto(
            audiobookId = "3",
            title = "Test AudioBook",
            language = "English"
        )

        val result = HomeContentMapper.mapHomeSectionsResponse(
            HomeSectionsResponseDto(
                sections = listOf(
                    ContentSectionDto(
                        title = "Test",
                        sectionType = "audio_book",
                        layoutType = "binary_grid",
                        items = listOf(dto),
                        order = 1
                    )
                )
            )
        )

        val item = result.first().items.first()
        assertTrue(item is ContentItem.AudioBook)
        val audioBook = item as ContentItem.AudioBook
        assertEquals("3", audioBook.id)
        assertEquals("Test AudioBook", audioBook.title)
        assertEquals("English", audioBook.language)
    }

    @Test
    fun `should map ContentItemDto to AudioArticle correctly`() {
        val dto = HomeItemDto(
            articleId = "4",
            title = "Test Article",
            articleUrl = "https://example.com/article",
            readBy = "Test Reader"
        )

        val result = HomeContentMapper.mapHomeSectionsResponse(
            HomeSectionsResponseDto(
                sections = listOf(
                    ContentSectionDto(
                        title = "Test",
                        sectionType = "audio_article",
                        layoutType = "binary_grid",
                        items = listOf(dto),
                        order = 1
                    )
                )
            )
        )

        val item = result.first().items.first()
        assertTrue(item is ContentItem.AudioArticle)
        val article = item as ContentItem.AudioArticle
        assertEquals("4", article.id)
        assertEquals("Test Article", article.title)
        assertEquals("https://example.com/article", article.articleUrl)
        assertEquals("Test Reader", article.readBy)
    }

    @Test
    fun `should map unknown content type to AudioArticle as fallback`() {
        val dto = HomeItemDto(
            title = "Unknown Content"
        )

        val result = HomeContentMapper.mapHomeSectionsResponse(
            HomeSectionsResponseDto(
                sections = listOf(
                    ContentSectionDto(
                        title = "Test",
                        sectionType = "mixed",
                        layoutType = "binary_grid",
                        items = listOf(dto),
                        order = 1
                    )
                )
            )
        )

        val item = result.first().items.first()
        assertTrue(item is ContentItem.AudioArticle)
        assertEquals("", item.id)
        assertEquals("Unknown Content", item.title)
    }

    @Test
    fun `should map section types correctly`() {
        val testCases = mapOf(
            "podcast" to SectionType.PODCASTS,
            "episode" to SectionType.EPISODES,
            "audio_book" to SectionType.AUDIOBOOKS,
            "audio_article" to SectionType.AUDIO_ARTICLES,
            "mixed" to SectionType.MIXED,
            "unknown" to SectionType.MIXED
        )

        testCases.forEach { (input, expected) ->
            val dto = HomeSectionsResponseDto(
                sections = listOf(
                    ContentSectionDto(
                        title = "Test",
                        sectionType = input,
                        layoutType = "binary_grid",
                        items = emptyList(),
                        order = 1
                    )
                )
            )

            val result = HomeContentMapper.mapHomeSectionsResponse(dto)
            assertEquals("Failed for input: $input", expected, result.first().sectionType)
        }
    }

    @Test
    fun `should map layout types correctly`() {
        val testCases = mapOf(
            "binary_grid" to SectionLayout.BINARY_GRID,
            "2_lines_grid" to SectionLayout.BINARY_GRID,
            "square_grid" to SectionLayout.SQUARE_GRID,
            "square" to SectionLayout.SQUARE_GRID,
            "horizontal_list" to SectionLayout.HORIZONTAL_LIST,
            "queue" to SectionLayout.QUEUE,
            "vertical_list" to SectionLayout.VERTICAL_LIST,
            "large_cards" to SectionLayout.LARGE_CARDS,
            "big_square" to SectionLayout.LARGE_CARDS,
            "unknown" to SectionLayout.BINARY_GRID
        )

        testCases.forEach { (input, expected) ->
            val dto = HomeSectionsResponseDto(
                sections = listOf(
                    ContentSectionDto(
                        title = "Test",
                        sectionType = "podcast",
                        layoutType = input,
                        items = emptyList(),
                        order = 1
                    )
                )
            )

            val result = HomeContentMapper.mapHomeSectionsResponse(dto)
            assertEquals("Failed for input: $input", expected, result.first().layoutType)
        }
    }
}