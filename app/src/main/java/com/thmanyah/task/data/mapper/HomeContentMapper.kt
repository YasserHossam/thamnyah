package com.thmanyah.task.data.mapper

import com.thmanyah.task.data.remote.dto.HomeItemDto
import com.thmanyah.task.data.remote.dto.ContentSectionDto
import com.thmanyah.task.data.remote.dto.HomeSectionsResponseDto
import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.domain.model.ContentSection
import com.thmanyah.task.domain.model.SectionLayout
import com.thmanyah.task.domain.model.SectionType

object HomeContentMapper {
    
    fun mapHomeSectionsResponse(dto: HomeSectionsResponseDto): List<ContentSection> =
        dto.sections.map { mapContentSection(it) }

    private fun mapContentSection(dto: ContentSectionDto): ContentSection =
        ContentSection(
            id = dto.id,
            title = dto.title,
            sectionType = mapSectionType(dto.sectionType),
            layoutType = mapSectionLayout(dto.layoutType),
            items = dto.items.map { mapContentItem(it) },
            order = dto.order,
            hasMore = dto.hasMore,
            nextPageUrl = dto.nextPageUrl
        )

    private fun mapContentItem(dto: HomeItemDto): ContentItem =
        when (dto.contentType.lowercase()) {
            "podcast" -> ContentItem.Podcast(
                id = dto.id,
                title = dto.title.orEmpty(),
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration?.toString(),
                publishDate = dto.publishDate,
                episodeCount = dto.episodeCount,
                category = null
            )
            "episode" -> ContentItem.Episode(
                id = dto.id,
                title = dto.title.orEmpty(),
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration?.toString(),
                publishDate = dto.publishDate,
                podcastId = dto.podcastId,
                episodeNumber = dto.episodeNumber,
                audioUrl = dto.audioUrl
            )
            "audio_book" -> ContentItem.AudioBook(
                id = dto.id,
                title = dto.title.orEmpty(),
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration?.toString(),
                publishDate = dto.publishDate,
                narrator = dto.author,
                chapters = null,
                language = dto.language
            )
            "audio_article" -> ContentItem.AudioArticle(
                id = dto.id,
                title = dto.title.orEmpty(),
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration?.toString(),
                publishDate = dto.publishDate,
                articleUrl = dto.articleUrl,
                readBy = dto.readBy,
                category = null
            )
            else -> ContentItem.AudioArticle(
                id = dto.id,
                title = dto.title.orEmpty(),
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration?.toString(),
                publishDate = dto.publishDate
            )
        }

    private fun mapSectionType(typeString: String): SectionType =
        when (typeString.lowercase()) {
            "podcast" -> SectionType.PODCASTS
            "episode" -> SectionType.EPISODES
            "audio_book" -> SectionType.AUDIOBOOKS
            "audio_article" -> SectionType.AUDIO_ARTICLES
            "mixed" -> SectionType.MIXED
            else -> SectionType.MIXED
        }

    private fun mapSectionLayout(layoutString: String): SectionLayout =
        when (layoutString.lowercase()) {
            "binary_grid", "2_lines_grid" -> SectionLayout.BINARY_GRID
            "square_grid", "square" -> SectionLayout.SQUARE_GRID
            "horizontal_list" -> SectionLayout.HORIZONTAL_LIST
            "queue" -> SectionLayout.QUEUE
            "vertical_list" -> SectionLayout.VERTICAL_LIST
            "large_cards", "big_square" -> SectionLayout.LARGE_CARDS
            else -> SectionLayout.BINARY_GRID
        }
}