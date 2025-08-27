package com.thmanyah.task.data.repository

import app.cash.turbine.test
import com.thmanyah.task.data.remote.datasource.HomeDataSource
import com.thmanyah.task.data.remote.dto.ContentSectionDto
import com.thmanyah.task.data.remote.dto.HomeItemDto
import com.thmanyah.task.data.remote.dto.HomeSectionsResponseDto
import com.thmanyah.task.domain.model.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeRepositoryImplTest {

    private lateinit var homeDataSource: HomeDataSource
    private lateinit var repository: HomeRepositoryImpl

    @Before
    fun setup() {
        homeDataSource = mockk()
        repository = HomeRepositoryImpl(homeDataSource)
    }

    @Test
    fun `when getHomeSections is called first time, should fetch from network`() = runTest {
        val mockResponse = HomeSectionsResponseDto(
            sections = listOf(
                ContentSectionDto(
                    title = "Test Section",
                    sectionType = "podcast",
                    layoutType = "binary_grid",
                    items = listOf(
                        HomeItemDto(
                            podcastId = "item1",
                            title = "Test Item"
                        )
                    ),
                    order = 1
                )
            )
        )
        coEvery { homeDataSource.getHomeSections() } returns Result.Success(mockResponse)

        repository.getHomeSections().test {
            val loadingEmission = awaitItem()
            assertTrue(loadingEmission is Result.Loading)

            val successEmission = awaitItem()
            assertTrue(successEmission is Result.Success)
            val sections = (successEmission as Result.Success).data
            assertEquals(1, sections.size)
            assertEquals("Test Section", sections.first().title)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when refreshHomeSections is called, should fetch from network and update cache`() = runTest {
        val mockResponse = HomeSectionsResponseDto(
            sections = listOf(
                ContentSectionDto(
                    title = "Refreshed Section",
                    sectionType = "podcast",
                    layoutType = "binary_grid",
                    items = emptyList(),
                    order = 1
                )
            )
        )
        coEvery { homeDataSource.getHomeSections() } returns Result.Success(mockResponse)

        val result = repository.refreshHomeSections()

        assertTrue(result is Result.Success)
        val sections = (result as Result.Success).data
        assertEquals(1, sections.size)
        assertEquals("Refreshed Section", sections.first().title)
    }

    @Test
    fun `when network fails, should return error`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { homeDataSource.getHomeSections() } returns Result.Error(exception)

        val result = repository.refreshHomeSections()

        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    @Test
    fun `sections should be sorted by order`() = runTest {
        val mockResponse = HomeSectionsResponseDto(
            sections = listOf(
                ContentSectionDto(
                    title = "Second Section",
                    sectionType = "podcast",
                    layoutType = "binary_grid",
                    items = emptyList(),
                    order = 2
                ),
                ContentSectionDto(
                    title = "First Section",
                    sectionType = "podcast",
                    layoutType = "binary_grid",
                    items = emptyList(),
                    order = 1
                )
            )
        )
        coEvery { homeDataSource.getHomeSections() } returns Result.Success(mockResponse)

        val result = repository.refreshHomeSections()

        assertTrue(result is Result.Success)
        val sections = (result as Result.Success).data
        assertEquals("First Section", sections.first().title)
        assertEquals("Second Section", sections.last().title)
    }
}