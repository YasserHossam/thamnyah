package com.thmanyah.task.domain.repository

import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.model.SearchQuery
import com.thmanyah.task.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun search(query: SearchQuery): Flow<Result<SearchResult>>
}