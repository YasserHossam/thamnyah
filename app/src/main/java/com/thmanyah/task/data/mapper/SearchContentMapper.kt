package com.thmanyah.task.data.mapper

import com.thmanyah.task.data.remote.dto.SearchItemDto
import com.thmanyah.task.data.remote.dto.SearchFiltersDto
import com.thmanyah.task.data.remote.dto.SearchRequestDto
import com.thmanyah.task.data.remote.dto.SearchResponseDto
import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.domain.model.SearchFilters
import com.thmanyah.task.domain.model.SearchQuery
import com.thmanyah.task.domain.model.SearchResult
import com.thmanyah.task.domain.model.SectionType

object SearchContentMapper {
    
    fun mapSearchResponse(dto: SearchResponseDto): SearchResult {
        val allItems = dto.sections.flatMap { section ->
            section.content.map { mapSearchContentItem(it) }
        }
        return SearchResult(
            results = allItems,
            totalCount = allItems.size,
            hasMore = false
        )
    }

    fun mapSearchQuery(query: SearchQuery): SearchRequestDto =
        SearchRequestDto(
            query = query.text,
            filters = query.filters?.let { mapSearchFilters(it) }
        )

    private fun mapSearchFilters(filters: SearchFilters): SearchFiltersDto =
        SearchFiltersDto(
            contentTypes = filters.contentTypes?.map { mapSectionTypeToApiString(it) },
            authors = filters.authors,
            categories = filters.categories,
            minDuration = filters.minDuration,
            maxDuration = filters.maxDuration
        )

    private fun mapSearchContentItem(dto: SearchItemDto): ContentItem =
        when (dto.contentTypeDetected.lowercase()) {
            "podcast" -> ContentItem.Podcast(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration,
                publishDate = dto.publishDate,
                episodeCount = dto.episodeCount?.toIntOrNull(),
                category = null
            )
            "episode" -> ContentItem.Episode(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration,
                publishDate = dto.publishDate,
                podcastId = dto.podcastId,
                episodeNumber = dto.episodeNumber,
                audioUrl = dto.audioUrl
            )
            "audio_book" -> ContentItem.AudioBook(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration,
                publishDate = dto.publishDate,
                narrator = dto.author,
                chapters = null,
                language = dto.language
            )
            "audio_article" -> ContentItem.AudioArticle(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration,
                publishDate = dto.publishDate,
                articleUrl = dto.articleUrl,
                readBy = dto.readBy,
                category = null
            )
            else -> ContentItem.AudioArticle(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                imageUrl = dto.imageUrl,
                author = dto.author,
                duration = dto.duration,
                publishDate = dto.publishDate
            )
        }

    private fun mapSectionTypeToApiString(type: SectionType): String =
        when (type) {
            SectionType.PODCASTS -> "podcasts"
            SectionType.EPISODES -> "episodes"
            SectionType.AUDIOBOOKS -> "audiobooks"
            SectionType.AUDIO_ARTICLES -> "audio_articles"
            SectionType.MIXED -> "mixed"
        }
}