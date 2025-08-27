package com.thmanyah.task.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val query: String? = null,
    val results: List<ContentItem>,
    val totalCount: Int = 0,
    val hasMore: Boolean = false
)

@Serializable
data class SearchQuery(
    val text: String,
    val filters: SearchFilters? = null
)

@Serializable
data class SearchFilters(
    val contentTypes: List<SectionType>? = null,
    val authors: List<String>? = null,
    val categories: List<String>? = null,
    val minDuration: Int? = null,
    val maxDuration: Int? = null
)