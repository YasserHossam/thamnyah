package com.thmanyah.task.data.remote.datasource

import com.thmanyah.task.data.remote.api.HomeApiService
import com.thmanyah.task.data.remote.dto.HomeSectionsResponseDto
import com.thmanyah.task.domain.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeNetworkDataSource @Inject constructor(
    private val homeApiService: HomeApiService
) : HomeDataSource {
    
    override suspend fun getHomeSections(): Result<HomeSectionsResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = homeApiService.getHomeSections()
            Result.Success(response)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}