package app.worthy.android.feature.goaldetail

import app.worthy.android.MainDispatcherRule
import app.worthy.android.data.repository.FakeSavingsRepository
import java.util.Locale
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class GoalDetailViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test fun `adding contribution updates derived savings and clears input`() = runTest {
        val repository = FakeSavingsRepository()
        repository.createGoal("Camera", "https://example.com/camera", 10_000, "EUR")
        val viewModel = GoalDetailViewModel(1, repository, Locale.US)
        testScheduler.advanceUntilIdle()
        viewModel.onContributionChanged("25.00")
        viewModel.addContribution()
        testScheduler.advanceUntilIdle()
        assertEquals("", viewModel.uiState.value.contributionText)
        assertEquals(2_500L, viewModel.uiState.value.goal?.saved?.amountMinor)
        assertEquals(7_500L, viewModel.uiState.value.goal?.progress?.remainingAmountMinor)
        assertEquals(ContributionHapticEvent.Added, viewModel.uiState.value.contributionHapticEvent)
    }

    @Test fun `crossing target emits completion only for the first crossing`() = runTest {
        val repository = FakeSavingsRepository()
        repository.createGoal("Camera", "https://example.com/camera", 10_000, "EUR")
        val viewModel = GoalDetailViewModel(1, repository, Locale.US)
        testScheduler.advanceUntilIdle()

        viewModel.onContributionChanged("100.00")
        viewModel.addContribution()
        testScheduler.advanceUntilIdle()
        assertEquals(ContributionHapticEvent.GoalCompleted, viewModel.uiState.value.contributionHapticEvent)

        viewModel.consumeContributionHapticEvent()
        viewModel.onContributionChanged("1.00")
        viewModel.addContribution()
        testScheduler.advanceUntilIdle()
        assertEquals(ContributionHapticEvent.Added, viewModel.uiState.value.contributionHapticEvent)
    }

    @Test fun `zero contribution is rejected`() = runTest {
        val repository = FakeSavingsRepository()
        repository.createGoal("Camera", "https://example.com/camera", 10_000, "EUR")
        val viewModel = GoalDetailViewModel(1, repository, Locale.US)
        testScheduler.advanceUntilIdle()
        viewModel.onContributionChanged("0")
        viewModel.addContribution()
        assertTrue(viewModel.uiState.value.contributionError != null)
    }

    @Test fun `unfinished goal asks for confirmation before deletion`() = runTest {
        val repository = FakeSavingsRepository()
        repository.createGoal("Camera", "https://example.com/camera", 10_000, "EUR")
        val viewModel = GoalDetailViewModel(1, repository, Locale.US)
        testScheduler.advanceUntilIdle()

        viewModel.requestDeletion()

        assertTrue(viewModel.uiState.value.showDeleteConfirmation)
        assertEquals(0, repository.deleteCallCount)

        viewModel.confirmDeletion()
        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.deletionCompleted)
        assertTrue(repository.goals.value.isEmpty())
        assertEquals(1, repository.deleteCallCount)
    }

    @Test fun `completed goal deletes immediately and only once`() = runTest {
        val repository = FakeSavingsRepository()
        repository.createGoal("Camera", "https://example.com/camera", 10_000, "EUR")
        repository.addContribution(1, 10_000)
        val viewModel = GoalDetailViewModel(1, repository, Locale.US)
        testScheduler.advanceUntilIdle()

        viewModel.requestDeletion()
        viewModel.requestDeletion()
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showDeleteConfirmation)
        assertTrue(viewModel.uiState.value.deletionCompleted)
        assertEquals(1, repository.deleteCallCount)
    }

    @Test fun `failed deletion remains on detail and can be retried`() = runTest {
        val repository = FakeSavingsRepository()
        repository.createGoal("Camera", "https://example.com/camera", 10_000, "EUR")
        repository.deleteFailure = IllegalStateException("failure")
        val viewModel = GoalDetailViewModel(1, repository, Locale.US)
        testScheduler.advanceUntilIdle()

        viewModel.requestDeletion()
        viewModel.confirmDeletion()
        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.deleteFailed)
        assertFalse(viewModel.uiState.value.deletionCompleted)
        assertFalse(viewModel.uiState.value.isDeleting)
    }
}
