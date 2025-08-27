package com.thmanyah.task.domain.model

val ContentSection.displayTitle: String
    get() = title.trim().takeIf { it.isNotBlank() } ?: "Untitled Section"

val ContentSection.itemCountText: String
    get() {
        val count = items.size
        return when {
            count == 0 -> "No items"
            count == 1 -> "1 item"
            else -> "$count items"
        }
    }

val ContentSection.hasMoreText: String?
    get() = if (hasMore) "View all" else null

val SectionType.displayName: String
    get() = when (this) {
        SectionType.PODCASTS -> "Podcasts"
        SectionType.EPISODES -> "Episodes" 
        SectionType.AUDIOBOOKS -> "Audio Books"
        SectionType.AUDIO_ARTICLES -> "Audio Articles"
        SectionType.MIXED -> "Mixed Content"
    }

val SectionLayout.displayName: String
    get() = when (this) {
        SectionLayout.BINARY_GRID -> "Grid View"
        SectionLayout.SQUARE_GRID -> "Square Grid" 
        SectionLayout.HORIZONTAL_LIST -> "Horizontal List"
        SectionLayout.VERTICAL_LIST -> "Vertical List"
        SectionLayout.LARGE_CARDS -> "Large Cards"
        SectionLayout.QUEUE -> "Queue List"
    }

val SectionLayout.columnCount: Int
    get() = when (this) {
        SectionLayout.BINARY_GRID -> 2
        SectionLayout.SQUARE_GRID -> 3
        SectionLayout.HORIZONTAL_LIST -> 1
        SectionLayout.VERTICAL_LIST -> 1
        SectionLayout.LARGE_CARDS -> 1
        SectionLayout.QUEUE -> 1
    }

val ContentSection.isEmpty: Boolean
    get() = items.isEmpty()

val ContentSection.isNotEmpty: Boolean
    get() = items.isNotEmpty()

fun ContentSection.getContentTypeBreakdown(): Map<SectionType, Int> {
    return items.groupBy { item ->
        when (item) {
            is ContentItem.Podcast -> SectionType.PODCASTS
            is ContentItem.Episode -> SectionType.EPISODES
            is ContentItem.AudioBook -> SectionType.AUDIOBOOKS
            is ContentItem.AudioArticle -> SectionType.AUDIO_ARTICLES
        }
    }.mapValues { it.value.size }
}

val ContentSection.contentBreakdownText: String?
    get() {
        if (sectionType != SectionType.MIXED) return null
        
        val breakdown = getContentTypeBreakdown()
        if (breakdown.size <= 1) return null
        
        return breakdown.entries
            .sortedByDescending { it.value }
            .joinToString(", ") { (type, count) ->
                "$count ${type.displayName.lowercase()}"
            }
    }