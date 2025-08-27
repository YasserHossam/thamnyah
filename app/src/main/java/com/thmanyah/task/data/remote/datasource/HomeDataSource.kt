package com.thmanyah.task.data.remote.datasource

import com.thmanyah.task.data.remote.dto.HomeSectionsResponseDto
import com.thmanyah.task.domain.model.Result

interface HomeDataSource {
    suspend fun getHomeSections(): Result<HomeSectionsResponseDto>
}