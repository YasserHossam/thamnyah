package com.thmanyah.task.di

import com.thmanyah.task.data.remote.datasource.HomeDataSource
import com.thmanyah.task.data.remote.datasource.HomeNetworkDataSource
import com.thmanyah.task.data.remote.datasource.SearchDataSource
import com.thmanyah.task.data.remote.datasource.SearchNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    
    @Binds
    abstract fun bindHomeDataSource(
        homeNetworkDataSource: HomeNetworkDataSource
    ): HomeDataSource
    
    @Binds
    abstract fun bindSearchDataSource(
        searchNetworkDataSource: SearchNetworkDataSource
    ): SearchDataSource
}