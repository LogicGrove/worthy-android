package app.worthy.android.feature.goaldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.worthy.android.core.money.MoneyInputResult
import app.worthy.android.core.money.parseMoneyInput
import app.worthy.android.data.repository.SavingsRepository
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoalDetailViewModel(
    private val goalId: Long,
    private val repository: SavingsRepository,
    private val locale: Locale = Locale.getDefault(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoalDetailUiState())
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repository.observeGoal(goalId), repository.observeContributions(goalId)) { goal, contributions ->
                goal to contributions
            }.catch {
                _uiState.update { state -> state.copy(isLoading = false, loadFailed = true) }
            }.collect { (goal, contributions) ->
                _uiState.update { state ->
                    state.copy(goal = goal, contributions = contributions, isLoading = false, loadFailed = false)
                }
            }
        }
    }

    fun onContributionChanged(value: String) = _uiState.update {
        it.copy(contributionText = value, contributionError = null, addFailed = false)
    }

    fun addContribution() {
        val state = _uiState.value
        val goal = state.goal ?: return
        val result = parseMoneyInput(state.contributionText, goal.goal.target.currencyCode, locale)
        val valid = result as? MoneyInputResult.Valid
        if (valid == null) {
            _uiState.update { it.copy(contributionError = result) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingContribution = true, addFailed = false) }
            val completesGoal = goal.saved.amountMinor < goal.goal.target.amountMinor &&
                valid.amountMinor >= goal.goal.target.amountMinor - goal.saved.amountMinor
            runCatching { repository.addContribution(goalId, valid.amountMinor) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isAddingContribution = false,
                            contributionText = "",
                            contributionError = null,
                            contributionHapticEvent = if (completesGoal) {
                                ContributionHapticEvent.GoalCompleted
                            } else {
                                ContributionHapticEvent.Added
                            },
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isAddingContribution = false, addFailed = true) }
                }
        }
    }

    fun requestDeletion() {
        val state = _uiState.value
        val goal = state.goal ?: return
        if (state.isDeleting || state.deletionCompleted) return
        if (goal.saved.amountMinor < goal.goal.target.amountMinor) {
            _uiState.update { it.copy(showDeleteConfirmation = true, deleteFailed = false) }
        } else {
            deleteGoal()
        }
    }

    fun dismissDeleteConfirmation() = _uiState.update {
        if (it.isDeleting) it else it.copy(showDeleteConfirmation = false)
    }

    fun confirmDeletion() {
        if (!_uiState.value.showDeleteConfirmation) return
        deleteGoal()
    }

    fun consumeDeletionCompleted() = _uiState.update { it.copy(deletionCompleted = false) }
    fun consumeContributionHapticEvent() = _uiState.update { it.copy(contributionHapticEvent = null) }

    private fun deleteGoal() {
        val state = _uiState.value
        if (state.goal == null || state.isDeleting || state.deletionCompleted) return
        _uiState.update {
            it.copy(isDeleting = true, showDeleteConfirmation = false, deleteFailed = false)
        }
        viewModelScope.launch {
            runCatching { repository.deleteGoal(goalId) }
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, deletionCompleted = true) }
                }
                .onFailure {
                    _uiState.update { it.copy(isDeleting = false, deleteFailed = true) }
                }
        }
    }

    companion object {
        fun factory(goalId: Long, repository: SavingsRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { GoalDetailViewModel(goalId, repository) }
        }
    }
}
