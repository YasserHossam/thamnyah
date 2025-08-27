package com.thmanyah.task.domain.repository

import com.thmanyah.task.domain.model.ContentSection
import com.thmanyah.task.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getHomeSections(): Flow<Result<List<ContentSection>>>
    suspend fun refreshHomeSections(): Result<List<ContentSection>>
}