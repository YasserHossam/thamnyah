package com.thmanyah.task.domain.usecase

import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.model.SearchQuery
import com.thmanyah.task.domain.model.SearchResult
import com.thmanyah.task.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchContentUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(searchQuery: SearchQuery): Flow<Result<SearchResult>> {
        return if (searchQuery.text.isBlank()) {
            kotlinx.coroutines.flow.flowOf(
                Result.Success(
                    SearchResult(query = searchQuery.text, results = emptyList())
                )
            )
        } else {
            searchRepository.search(searchQuery)
        }
    }
}