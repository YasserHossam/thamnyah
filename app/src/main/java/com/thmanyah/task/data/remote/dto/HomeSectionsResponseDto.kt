package com.thmanyah.task.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeSectionsResponseDto(
    @SerialName("sections")
    val sections: List<ContentSectionDto>,
    @SerialName("pagination")
    val pagination: PaginationDto? = null
)

@Serializable
data class PaginationDto(
    @SerialName("next_page")
    val nextPage: String? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null
)

@Serializable
data class ContentSectionDto(
    @SerialName("name")
    val title: String,
    @SerialName("type")
    val layoutType: String,
    @SerialName("content_type")
    val sectionType: String,
    @SerialName("content")
    val items: List<HomeItemDto>,
    @SerialName("order")
    val order: Int,
    @SerialName("has_more")
    val hasMore: Boolean = false,
    @SerialName("next_page_url")
    val nextPageUrl: String? = null
) {
    val id: String get() = "${title}_${order}"
}

@Serializable
data class HomeItemDto(
    @SerialName("podcast_id")
    val podcastId: String? = null,
    @SerialName("name")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("avatar_url")
    val imageUrl: String? = null,
    @SerialName("episode_count")
    val episodeCount: Int? = null,
    @SerialName("duration")
    val duration: Int? = null,
    @SerialName("language")
    val language: String? = null,
    @SerialName("priority")
    val priority: Int? = null,
    @SerialName("popularityScore")
    val popularityScore: Int? = null,
    @SerialName("score")
    val score: Double? = null,
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
    val contentType: String get() = when {
        podcastId != null -> "podcast"
        episodeId != null -> "episode"
        audiobookId != null -> "audio_book"
        articleId != null -> "audio_article"
        else -> "unknown"
    }
    val publishDate: String? get() = releaseDate
}