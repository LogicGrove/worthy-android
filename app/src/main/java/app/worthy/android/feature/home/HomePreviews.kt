package app.worthy.android.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.core.model.Money
import app.worthy.android.core.model.SavingsGoal
import app.worthy.android.ui.theme.WorthyTheme

@Preview(showBackground = true)
@Composable
private fun EmptyHomePreview() {
    WorthyTheme(dynamicColor = false) {
        HomeScreen(HomeUiState(isLoading = false), {}, {})
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeWithGoalPreview() {
    WorthyTheme(dynamicColor = false) {
        HomeScreen(
            uiState = HomeUiState(
                goals = listOf(
                    GoalWithSavings(
                        goal = SavingsGoal(
                            id = 1,
                            productName = "Noise-cancelling headphones",
                            productUrl = "https://example.com/headphones",
                            target = Money(34_999, "EUR"),
                            createdAtEpochMillis = 0,
                            updatedAtEpochMillis = 0,
                        ),
                        saved = Money(12_500, "EUR"),
                    ),
                    GoalWithSavings(
                        goal = SavingsGoal(
                            id = 2,
                            productName = "Weekend camera",
                            productUrl = "https://example.com/camera",
                            target = Money(89_900, "EUR"),
                            createdAtEpochMillis = 1,
                            updatedAtEpochMillis = 1,
                        ),
                        saved = Money(18_000, "EUR"),
                    ),
                    GoalWithSavings(
                        goal = SavingsGoal(
                            id = 3,
                            productName = "City bicycle",
                            productUrl = "https://example.com/bicycle",
                            target = Money(54_900, "EUR"),
                            createdAtEpochMillis = 2,
                            updatedAtEpochMillis = 2,
                        ),
                        saved = Money(41_000, "EUR"),
                    ),
                ),
                isLoading = false,
            ),
            onAddGoal = {},
            onGoalSelected = {},
        )
    }
}
