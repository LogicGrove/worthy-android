package app.worthy.android.data.local

import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.core.model.Money
import app.worthy.android.core.model.SavingsContribution
import app.worthy.android.core.model.SavingsGoal

fun GoalWithSavedAmountRow.toModel(): GoalWithSavings {
    val goal = SavingsGoal(
        id = id,
        productName = productName,
        productUrl = productUrl,
        target = Money(targetAmountMinor, currencyCode),
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )
    return GoalWithSavings(
        goal = goal,
        saved = Money(savedAmountMinor, currencyCode),
    )
}

fun SavingsContributionEntity.toModel(currencyCode: String): SavingsContribution =
    SavingsContribution(
        id = id,
        goalId = goalId,
        amount = Money(amountMinor, currencyCode),
        createdAtEpochMillis = createdAtEpochMillis,
    )
