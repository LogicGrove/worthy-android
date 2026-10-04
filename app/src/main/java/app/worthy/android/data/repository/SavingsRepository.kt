package app.worthy.android.data.repository

import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.core.model.SavingsContribution
import app.worthy.android.data.local.SavingsContributionEntity
import app.worthy.android.data.local.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

data class SavingsDataSnapshot(
    val goals: List<SavingsGoalEntity>,
    val contributions: List<SavingsContributionEntity>,
)

interface SavingsRepository {
    fun observeGoals(): Flow<List<GoalWithSavings>>
    fun observeGoal(goalId: Long): Flow<GoalWithSavings?>
    fun observeContributions(goalId: Long): Flow<List<SavingsContribution>>

    suspend fun createGoal(
        productName: String,
        productUrl: String,
        targetAmountMinor: Long,
        currencyCode: String,
    ): Long

    suspend fun addContribution(goalId: Long, amountMinor: Long): Long
    suspend fun deleteGoal(goalId: Long)
    suspend fun deleteAllData()
    suspend fun snapshot(): SavingsDataSnapshot
    suspend fun replaceAll(snapshot: SavingsDataSnapshot)
}
