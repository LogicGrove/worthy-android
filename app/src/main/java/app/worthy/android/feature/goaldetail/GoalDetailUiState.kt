package app.worthy.android.feature.goaldetail

import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.core.model.SavingsContribution
import app.worthy.android.core.money.MoneyInputResult

data class GoalDetailUiState(
    val goal: GoalWithSavings? = null,
    val contributions: List<SavingsContribution> = emptyList(),
    val contributionText: String = "",
    val contributionError: MoneyInputResult? = null,
    val isLoading: Boolean = true,
    val isAddingContribution: Boolean = false,
    val loadFailed: Boolean = false,
    val addFailed: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteFailed: Boolean = false,
    val deletionCompleted: Boolean = false,
    val contributionHapticEvent: ContributionHapticEvent? = null,
)

enum class ContributionHapticEvent { Added, GoalCompleted }
