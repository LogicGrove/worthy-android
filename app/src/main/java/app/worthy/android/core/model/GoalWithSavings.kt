package app.worthy.android.core.model

data class GoalWithSavings(
    val goal: SavingsGoal,
    val saved: Money,
) {
    val progress: SavingsProgress = calculateSavingsProgress(
        targetAmountMinor = goal.target.amountMinor,
        savedAmountMinor = saved.amountMinor,
    )
}
