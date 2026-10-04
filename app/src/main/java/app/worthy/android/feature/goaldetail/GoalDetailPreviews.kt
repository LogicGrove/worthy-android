package app.worthy.android.feature.goaldetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.core.model.Money
import app.worthy.android.core.model.SavingsContribution
import app.worthy.android.core.model.SavingsGoal
import app.worthy.android.ui.theme.WorthyTheme

@Preview(showBackground = true)
@Composable
private fun GoalDetailPreview() {
    val goal = SavingsGoal(
        id = 1,
        productName = "Weekend bicycle",
        productUrl = "https://example.com/bicycle",
        target = Money(90_000, "EUR"),
        createdAtEpochMillis = 0,
        updatedAtEpochMillis = 0,
    )
    WorthyTheme(dynamicColor = false) {
        GoalDetailScreen(
            goalId = goal.id,
            uiState = GoalDetailUiState(
                goal = GoalWithSavings(goal, Money(25_000, "EUR")),
                contributions = listOf(
                    SavingsContribution(1, 1, Money(10_000, "EUR"), 0),
                    SavingsContribution(2, 1, Money(15_000, "EUR"), 1),
                ),
                isLoading = false,
            ),
            onContributionChanged = {},
            onAddContribution = {},
            onDelete = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onBack = {},
        )
    }
}
