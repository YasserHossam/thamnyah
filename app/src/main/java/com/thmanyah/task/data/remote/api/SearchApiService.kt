package com.thmanyah.task.data.remote.api

import com.thmanyah.task.data.remote.dto.SearchResponseDto
import retrofit2.http.GET

interface SearchApiService {
    
    @GET("search")
    suspend fun search(): SearchResponseDto
}