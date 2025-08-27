package com.thmanyah.task.data.repository

import com.thmanyah.task.data.mapper.SearchContentMapper
import com.thmanyah.task.data.remote.datasource.SearchDataSource
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.model.SearchQuery
import com.thmanyah.task.domain.model.SearchResult
import com.thmanyah.task.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val searchDataSource: SearchDataSource
) : SearchRepository {

    private val _currentSearchResult = MutableStateFlow<Result<SearchResult>>(Result.Loading)
    
    override suspend fun search(query: SearchQuery): Flow<Result<SearchResult>> {
        _currentSearchResult.value = Result.Loading
        
        when (val result = searchDataSource.search()) {
            is Result.Success -> {
                val searchResult = SearchContentMapper.mapSearchResponse(result.data)
                _currentSearchResult.value = Result.Success(searchResult)
            }
            is Result.Error -> {
                _currentSearchResult.value = result
            }
            is Result.Loading -> {
                _currentSearchResult.value = Result.Loading
            }
        }
        
        return _currentSearchResult.asStateFlow()
    }
}