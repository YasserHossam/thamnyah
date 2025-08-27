package com.thmanyah.task.data.remote.api

import com.thmanyah.task.data.remote.dto.HomeSectionsResponseDto
import retrofit2.http.GET

interface HomeApiService {
    
    @GET("home_sections")
    suspend fun getHomeSections(): HomeSectionsResponseDto
}