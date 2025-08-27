package com.thmanyah.task.data.remote.datasource

import com.thmanyah.task.data.remote.api.SearchApiService
import com.thmanyah.task.data.remote.dto.SearchResponseDto
import com.thmanyah.task.domain.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchNetworkDataSource @Inject constructor(
    private val searchApiService: SearchApiService
) : SearchDataSource {
    
    override suspend fun search(): Result<SearchResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = searchApiService.search()
            Result.Success(response)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}