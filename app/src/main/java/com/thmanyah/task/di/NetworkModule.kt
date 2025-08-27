package com.thmanyah.task.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.thmanyah.task.data.remote.api.HomeApiService
import com.thmanyah.task.data.remote.api.SearchApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "https://api-v2-b2sit6oh3a-uc.a.run.app/"
    private const val SEARCH_BASE_URL = "https://mock.apidog.com/m1/735111-711675-default/"
    
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MainApi
    
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SearchApi
    
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }
    
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    
    @Provides
    @Singleton
    @MainApi
    fun provideMainRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    
    @Provides
    @Singleton
    @SearchApi
    fun provideSearchRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(SEARCH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    
    @Provides
    @Singleton
    fun provideHomeApiService(@MainApi retrofit: Retrofit): HomeApiService =
        retrofit.create(HomeApiService::class.java)
    
    @Provides
    @Singleton
    fun provideSearchApiService(@SearchApi retrofit: Retrofit): SearchApiService =
        retrofit.create(SearchApiService::class.java)
}