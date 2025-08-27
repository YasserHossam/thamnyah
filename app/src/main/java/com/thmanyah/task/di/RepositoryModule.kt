package com.thmanyah.task.di

import com.thmanyah.task.data.repository.HomeRepositoryImpl
import com.thmanyah.task.data.repository.SearchRepositoryImpl
import com.thmanyah.task.domain.repository.HomeRepository
import com.thmanyah.task.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository
    
    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository
}