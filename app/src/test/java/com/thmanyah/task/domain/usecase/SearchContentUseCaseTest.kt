package com.thmanyah.task.domain.usecase

import app.cash.turbine.test
import com.thmanyah.task.domain.model.ContentItem
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.model.SearchQuery
import com.thmanyah.task.domain.model.SearchResult
import com.thmanyah.task.domain.repository.SearchRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class SearchContentUseCaseTest {

    private lateinit var searchRepository: SearchRepository
    private lateinit var useCase: SearchContentUseCase

    @Before
    fun setup() {
        searchRepository = mockk()
        useCase = SearchContentUseCase(searchRepository)
    }

    @Test
    fun `when query is blank, should return empty result`() = runTest {
        val blankQuery = SearchQuery("")

        useCase(blankQuery).test {
            val emission = awaitItem()
            assertTrue(emission is Result.Success)
            val successResult = emission as Result.Success
            assertEquals("", successResult.data.query)
            assertEquals(0, successResult.data.totalCount)
            assertTrue(successResult.data.results.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `when query is not blank and repository returns success, should emit success`() = runTest {
        val query = SearchQuery("test")
        val mockResults = listOf(
            ContentItem.Podcast(
                id = "1",
                title = "Test Podcast",
                author = "Test Author"
            )
        )
        val mockSearchResult = SearchResult(
            query = "test",
            results = mockResults,
            totalCount = 1
        )
        coEvery { searchRepository.search(query) } returns flowOf(Result.Success(mockSearchResult))

        useCase(query).test {
            val emission = awaitItem()
            assertTrue(emission is Result.Success)
            assertEquals(mockSearchResult, (emission as Result.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `when query is not blank and repository returns error, should emit error`() = runTest {
        val query = SearchQuery("test")
        val exception = RuntimeException("Search failed")
        coEvery { searchRepository.search(query) } returns flowOf(Result.Error(exception))

        useCase(query).test {
            val emission = awaitItem()
            assertTrue(emission is Result.Error)
            assertEquals(exception, (emission as Result.Error).exception)
            awaitComplete()
        }
    }

    @Test
    fun `when query has whitespace only, should return empty result`() = runTest {
        val whitespaceQuery = SearchQuery("   ")

        useCase(whitespaceQuery).test {
            val emission = awaitItem()
            assertTrue(emission is Result.Success)
            val successResult = emission as Result.Success
            assertEquals("   ", successResult.data.query)
            assertEquals(0, successResult.data.totalCount)
            assertTrue(successResult.data.results.isEmpty())
            awaitComplete()
        }
    }
}