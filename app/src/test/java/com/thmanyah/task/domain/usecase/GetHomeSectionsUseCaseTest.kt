package com.thmanyah.task.domain.usecase

import app.cash.turbine.test
import com.thmanyah.task.domain.model.ContentSection
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.model.SectionLayout
import com.thmanyah.task.domain.model.SectionType
import com.thmanyah.task.domain.repository.HomeRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class GetHomeSectionsUseCaseTest {

    private lateinit var homeRepository: HomeRepository
    private lateinit var useCase: GetHomeSectionsUseCase

    @Before
    fun setup() {
        homeRepository = mockk()
        useCase = GetHomeSectionsUseCase(homeRepository)
    }

    @Test
    fun `when repository returns success, use case should emit success`() = runTest {
        val mockSections = listOf(
            ContentSection(
                id = "1",
                title = "Test Section",
                sectionType = SectionType.PODCASTS,
                layoutType = SectionLayout.BINARY_GRID,
                items = emptyList(),
                order = 1
            )
        )
        coEvery { homeRepository.getHomeSections() } returns flowOf(Result.Success(mockSections))

        useCase().test {
            val emission = awaitItem()
            assertTrue(emission is Result.Success)
            assertEquals(mockSections, (emission as Result.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `when repository returns error, use case should emit error`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { homeRepository.getHomeSections() } returns flowOf(Result.Error(exception))

        useCase().test {
            val emission = awaitItem()
            assertTrue(emission is Result.Error)
            assertEquals(exception, (emission as Result.Error).exception)
            awaitComplete()
        }
    }

    @Test
    fun `when repository returns loading, use case should emit loading`() = runTest {
        coEvery { homeRepository.getHomeSections() } returns flowOf(Result.Loading)

        useCase().test {
            val emission = awaitItem()
            assertTrue(emission is Result.Loading)
            awaitComplete()
        }
    }
}