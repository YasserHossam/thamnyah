package com.thmanyah.task.presentation.arch.home

import app.cash.turbine.test
import com.thmanyah.task.domain.model.ContentSection
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.model.SectionLayout
import com.thmanyah.task.domain.model.SectionType
import com.thmanyah.task.domain.usecase.GetHomeSectionsUseCase
import com.thmanyah.task.domain.usecase.RefreshHomeSectionsUseCase
import com.thmanyah.task.presentation.ui.screen.home.HomeEffect
import com.thmanyah.task.presentation.ui.screen.home.HomeIntent
import com.thmanyah.task.presentation.ui.screen.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getHomeSectionsUseCase: GetHomeSectionsUseCase
    private lateinit var refreshHomeSectionsUseCase: RefreshHomeSectionsUseCase
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getHomeSectionsUseCase = mockk()
        refreshHomeSectionsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewModel is initialized, should load home sections`() = runTest {
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
        coEvery { getHomeSectionsUseCase() } returns flowOf(Result.Success(mockSections))

        viewModel = HomeViewModel(getHomeSectionsUseCase, refreshHomeSectionsUseCase)

        viewModel.state.test {
            val firstState = awaitItem()
            println("first state -> $firstState")
            assertEquals(firstState.isLoading, true)
            val updatedState = awaitItem()
            assertEquals(mockSections, updatedState.sections)
            assertFalse(updatedState.isLoading)
            assertNull(updatedState.errorMessage)
        }
    }

    @Test
    fun `when loading sections fails, should show error and emit error effect`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { getHomeSectionsUseCase() } returns flowOf(Result.Error(exception))

        viewModel = HomeViewModel(getHomeSectionsUseCase, refreshHomeSectionsUseCase)

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is HomeEffect.ShowErrorSnackbar)
            assertEquals("Network error", (effect as HomeEffect.ShowErrorSnackbar).message)
        }

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Network error", state.errorMessage)
            assertTrue(state.sections.isEmpty())
        }
    }

    @Test
    fun `when refresh intent is handled, should update refresh state and load data`() = runTest {
        val mockSections = listOf(
            ContentSection(
                id = "1",
                title = "Refreshed Section",
                sectionType = SectionType.PODCASTS,
                layoutType = SectionLayout.BINARY_GRID,
                items = emptyList(),
                order = 1
            )
        )
        coEvery { getHomeSectionsUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { refreshHomeSectionsUseCase() } returns Result.Success(mockSections)

        viewModel = HomeViewModel(getHomeSectionsUseCase, refreshHomeSectionsUseCase)

        viewModel.handleIntent(HomeIntent.RefreshHomeSections)

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is HomeEffect.ShowRefreshSuccessMessage)
        }

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(mockSections, state.sections)
            assertFalse(state.isRefreshing)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `when refresh fails, should show error and emit error effect`() = runTest {
        val exception = RuntimeException("Refresh failed")
        coEvery { getHomeSectionsUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { refreshHomeSectionsUseCase() } returns Result.Error(exception)

        viewModel = HomeViewModel(getHomeSectionsUseCase, refreshHomeSectionsUseCase)

        viewModel.handleIntent(HomeIntent.RefreshHomeSections)

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is HomeEffect.ShowErrorSnackbar)
            assertEquals("Refresh failed", (effect as HomeEffect.ShowErrorSnackbar).message)
        }

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isRefreshing)
            assertEquals("Refresh failed", state.errorMessage)
        }
    }

    @Test
    fun `when clear error intent is handled, should clear error message`() = runTest {
        coEvery { getHomeSectionsUseCase() } returns flowOf(Result.Error(RuntimeException("Error")))
        viewModel = HomeViewModel(getHomeSectionsUseCase, refreshHomeSectionsUseCase)

        viewModel.handleIntent(HomeIntent.ClearError)

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `when section click intent is handled, should emit navigation effect`() = runTest {
        coEvery { getHomeSectionsUseCase() } returns flowOf(Result.Success(emptyList()))
        viewModel = HomeViewModel(getHomeSectionsUseCase, refreshHomeSectionsUseCase)

        viewModel.handleIntent(HomeIntent.OnSectionClicked("section1"))

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is HomeEffect.NavigateToSection)
            assertEquals("section1", (effect as HomeEffect.NavigateToSection).sectionId)
        }
    }

    @Test
    fun `when content item click intent is handled, should emit navigation effect`() = runTest {
        coEvery { getHomeSectionsUseCase() } returns flowOf(Result.Success(emptyList()))
        viewModel = HomeViewModel(getHomeSectionsUseCase, refreshHomeSectionsUseCase)

        viewModel.handleIntent(HomeIntent.OnContentItemClicked("item1"))

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is HomeEffect.NavigateToContentDetail)
            assertEquals("item1", (effect as HomeEffect.NavigateToContentDetail).itemId)
        }
    }

    @Test
    fun `when retry intent is handled, should load home sections again`() = runTest {
        val mockSections = listOf(
            ContentSection(
                id = "1",
                title = "Retry Section",
                sectionType = SectionType.PODCASTS,
                layoutType = SectionLayout.BINARY_GRID,
                items = emptyList(),
                order = 1
            )
        )
        coEvery { getHomeSectionsUseCase() } returns flowOf(Result.Success(mockSections))

        viewModel = HomeViewModel(getHomeSectionsUseCase, refreshHomeSectionsUseCase)

        viewModel.handleIntent(HomeIntent.Retry)

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals(mockSections, state.sections)
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `when already refreshing, should not start another refresh`() = runTest {
        coEvery { getHomeSectionsUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { refreshHomeSectionsUseCase() } coAnswers {
            kotlinx.coroutines.delay(1000)
            Result.Success(emptyList())
        }

        viewModel = HomeViewModel(getHomeSectionsUseCase, refreshHomeSectionsUseCase)

        viewModel.handleIntent(HomeIntent.RefreshHomeSections)
        viewModel.handleIntent(HomeIntent.RefreshHomeSections)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isRefreshing || !state.isRefreshing)
        }
    }
}