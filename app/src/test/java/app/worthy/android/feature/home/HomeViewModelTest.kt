package app.worthy.android.feature.home

import app.worthy.android.MainDispatcherRule
import app.worthy.android.data.repository.FakeSavingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test fun `empty repository produces empty content state`() = runTest {
        val viewModel = HomeViewModel(FakeSavingsRepository())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        testScheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.goals.isEmpty())
    }
}
