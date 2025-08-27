package com.thmanyah.task.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ContentSection(
    val id: String,
    val title: String,
    val sectionType: SectionType,
    val layoutType: SectionLayout,
    val items: List<ContentItem>,
    val order: Int,
    val hasMore: Boolean = false,
    val nextPageUrl: String? = null
)

enum class SectionType {
    PODCASTS,
    EPISODES,
    AUDIOBOOKS,
    AUDIO_ARTICLES,
    MIXED
}

enum class SectionLayout {
    BINARY_GRID,
    SQUARE_GRID,
    HORIZONTAL_LIST,
    VERTICAL_LIST,
    LARGE_CARDS,
    QUEUE
}