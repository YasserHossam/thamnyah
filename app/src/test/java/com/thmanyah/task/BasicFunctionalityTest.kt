package com.thmanyah.task

import com.thmanyah.task.data.mapper.HomeContentMapper
import com.thmanyah.task.data.mapper.SearchContentMapper
import com.thmanyah.task.data.remote.dto.*
import com.thmanyah.task.domain.model.*
import org.junit.Test
import org.junit.Assert.*

class BasicFunctionalityTest {

    @Test
    fun `home content mapper should work with basic data`() {
        val dto = HomeSectionsResponseDto(
            sections = listOf(
                ContentSectionDto(
                    title = "Test Section",
                    sectionType = "podcast",
                    layoutType = "binary_grid",
                    items = listOf(
                        HomeItemDto(podcastId = "1", title = "Test Podcast")
                    ),
                    order = 1
                )
            )
        )

        val result = HomeContentMapper.mapHomeSectionsResponse(dto)

        assertEquals(1, result.size)
        assertEquals("Test Section", result.first().title)
        assertEquals(SectionType.PODCASTS, result.first().sectionType)
    }

    @Test
    fun `search content mapper should work with basic data`() {
        val dto = SearchResponseDto(
            sections = listOf(
                SearchSectionDto(
                    name = "Test Section",
                    type = "binary_grid",
                    contentType = "podcast",
                    order = "1",
                    content = listOf(
                        SearchItemDto(podcastId = "1", title = "Test Result")
                    )
                )
            )
        )

        val result = SearchContentMapper.mapSearchResponse(dto)

        assertEquals(1, result.totalCount)
        assertEquals(1, result.results.size)
        assertEquals("Test Result", result.results.first().title)
    }

    @Test
    fun `search query mapper should work with filters`() {
        val query = SearchQuery(
            text = "test search",
            filters = SearchFilters(
                contentTypes = listOf(SectionType.PODCASTS),
                authors = listOf("Author1")
            )
        )

        val result = SearchContentMapper.mapSearchQuery(query)

        assertEquals("test search", result.query)
        assertNotNull(result.filters)
        assertEquals(listOf("podcasts"), result.filters?.contentTypes)
        assertEquals(listOf("Author1"), result.filters?.authors)
    }

    @Test
    fun `domain models should be created correctly`() {
        val section = ContentSection(
            id = "1",
            title = "Test",
            sectionType = SectionType.PODCASTS,
            layoutType = SectionLayout.BINARY_GRID,
            items = emptyList(),
            order = 1
        )

        assertEquals("1", section.id)
        assertEquals("Test", section.title)
        assertEquals(SectionType.PODCASTS, section.sectionType)
    }

    @Test
    fun `content item types should be created correctly`() {
        val podcast = ContentItem.Podcast(
            id = "1",
            title = "Test Podcast",
            author = "Test Author",
            episodeCount = 10
        )

        val episode = ContentItem.Episode(
            id = "2",
            title = "Test Episode",
            podcastId = "1",
            episodeNumber = 5
        )

        assertEquals("1", podcast.id)
        assertEquals("Test Podcast", podcast.title)
        assertEquals(10, podcast.episodeCount)

        assertEquals("2", episode.id)
        assertEquals("Test Episode", episode.title)
        assertEquals("1", episode.podcastId)
        assertEquals(5, episode.episodeNumber)
    }
}