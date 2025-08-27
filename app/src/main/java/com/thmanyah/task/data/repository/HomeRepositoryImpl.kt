package com.thmanyah.task.data.repository

import com.thmanyah.task.data.mapper.HomeContentMapper
import com.thmanyah.task.data.remote.datasource.HomeDataSource
import com.thmanyah.task.domain.model.ContentSection
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource
) : HomeRepository {
    
    private val _sectionsCache = MutableStateFlow<Result<List<ContentSection>>?>(null)
    
    override suspend fun getHomeSections(): Flow<Result<List<ContentSection>>> = flow {
        if (_sectionsCache.value == null) {
            emit(Result.Loading)
            val result = fetchHomeSections()
            _sectionsCache.value = result
            emit(result)
        } else {
            emit(_sectionsCache.value!!)
        }
    }
    
    override suspend fun refreshHomeSections(): Result<List<ContentSection>> {
        val result = fetchHomeSections()
        _sectionsCache.value = result
        return result
    }
    
    private suspend fun fetchHomeSections(): Result<List<ContentSection>> {
        return when (val result = homeDataSource.getHomeSections()) {
            is Result.Success -> {
                val sections = HomeContentMapper.mapHomeSectionsResponse(result.data).sortedBy { it.order }
                Result.Success(sections)
            }
            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}