package com.thmanyah.task.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class ContentItem {
    abstract val id: String
    abstract val title: String
    abstract val description: String?
    abstract val imageUrl: String?
    abstract val author: String?
    abstract val duration: String?
    abstract val publishDate: String?

    @Serializable
    data class Podcast(
        override val id: String,
        override val title: String,
        override val description: String? = null,
        override val imageUrl: String? = null,
        override val author: String? = null,
        override val duration: String? = null,
        override val publishDate: String? = null,
        val episodeCount: Int? = null,
        val category: String? = null
    ) : ContentItem()

    @Serializable
    data class Episode(
        override val id: String,
        override val title: String,
        override val description: String? = null,
        override val imageUrl: String? = null,
        override val author: String? = null,
        override val duration: String? = null,
        override val publishDate: String? = null,
        val podcastId: String? = null,
        val episodeNumber: Int? = null,
        val audioUrl: String? = null
    ) : ContentItem()

    @Serializable
    data class AudioBook(
        override val id: String,
        override val title: String,
        override val description: String? = null,
        override val imageUrl: String? = null,
        override val author: String? = null,
        override val duration: String? = null,
        override val publishDate: String? = null,
        val narrator: String? = null,
        val chapters: Int? = null,
        val language: String? = null
    ) : ContentItem()

    @Serializable
    data class AudioArticle(
        override val id: String,
        override val title: String,
        override val description: String? = null,
        override val imageUrl: String? = null,
        override val author: String? = null,
        override val duration: String? = null,
        override val publishDate: String? = null,
        val articleUrl: String? = null,
        val readBy: String? = null,
        val category: String? = null
    ) : ContentItem()
}