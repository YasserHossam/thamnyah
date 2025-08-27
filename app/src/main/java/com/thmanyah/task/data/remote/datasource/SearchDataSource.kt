package com.thmanyah.task.data.remote.datasource

import com.thmanyah.task.data.remote.dto.SearchResponseDto
import com.thmanyah.task.domain.model.Result

interface SearchDataSource {
    suspend fun search(): Result<SearchResponseDto>
}