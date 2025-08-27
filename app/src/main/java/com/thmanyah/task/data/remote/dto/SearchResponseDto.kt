package com.thmanyah.task.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    @SerialName("sections")
    val sections: List<SearchSectionDto>
)

@Serializable
data class SearchRequestDto(
    @SerialName("query")
    val query: String,
    @SerialName("filters")
    val filters: SearchFiltersDto? = null,
    @SerialName("page")
    val page: Int = 1,
    @SerialName("limit")
    val limit: Int = 20
)

@Serializable
data class SearchFiltersDto(
    @SerialName("content_types")
    val contentTypes: List<String>? = null,
    @SerialName("authors")
    val authors: List<String>? = null,
    @SerialName("categories")
    val categories: List<String>? = null,
    @SerialName("min_duration")
    val minDuration: Int? = null,
    @SerialName("max_duration")
    val maxDuration: Int? = null
)

@Serializable
data class SearchSectionDto(
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("content_type")
    val contentType: String,
    @SerialName("order")
    val order: String,
    @SerialName("content")
    val content: List<SearchItemDto>
)

@Serializable
data class SearchItemDto(
    @SerialName("podcast_id")
    val podcastId: String? = null,
    @SerialName("name")
    val title: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("avatar_url")
    val imageUrl: String? = null,
    @SerialName("episode_count")
    val episodeCount: String? = null,
    @SerialName("duration")
    val duration: String? = null,
    @SerialName("language")
    val language: String? = null,
    @SerialName("priority")
    val priority: String? = null,
    @SerialName("popularityScore")
    val popularityScore: String? = null,
    @SerialName("score")
    val score: String? = null,
    @SerialName("episode_id")
    val episodeId: String? = null,
    @SerialName("season_number")
    val seasonNumber: Int? = null,
    @SerialName("episode_type")
    val episodeType: String? = null,
    @SerialName("podcast_name")
    val podcastName: String? = null,
    @SerialName("author_name")
    val authorName: String? = null,
    @SerialName("number")
    val episodeNumber: Int? = null,
    @SerialName("separated_audio_url")
    val separatedAudioUrl: String? = null,
    @SerialName("audio_url")
    val audioUrl: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("audiobook_id")
    val audiobookId: String? = null,
    @SerialName("article_id")
    val articleId: String? = null,
    @SerialName("article_url")
    val articleUrl: String? = null,
    @SerialName("read_by")
    val readBy: String? = null
) {
    val id: String get() = podcastId ?: episodeId ?: audiobookId ?: articleId ?: ""
    val author: String? get() = authorName
    val contentTypeDetected: String get() = when {
        podcastId != null -> "podcast"
        episodeId != null -> "episode"
        audiobookId != null -> "audio_book"
        articleId != null -> "audio_article"
        else -> "podcast" // Default to podcast based on the JSON structure
    }
    val publishDate: String? get() = releaseDate
}