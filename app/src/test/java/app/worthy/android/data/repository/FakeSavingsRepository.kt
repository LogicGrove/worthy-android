package app.worthy.android.data.repository

import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.core.model.Money
import app.worthy.android.core.model.SavingsContribution
import app.worthy.android.core.model.SavingsGoal
import app.worthy.android.data.local.SavingsContributionEntity
import app.worthy.android.data.local.SavingsGoalEntity
import app.worthy.android.data.local.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSavingsRepository : SavingsRepository {
    val goals = MutableStateFlow<List<GoalWithSavings>>(emptyList())
    val contributions = MutableStateFlow<List<SavingsContribution>>(emptyList())
    var createFailure: Throwable? = null
    var addFailure: Throwable? = null
    var deleteFailure: Throwable? = null
    var deleteCallCount = 0
    var lastCreatedName: String? = null
    private var nextGoalId = 1L
    private var nextContributionId = 1L

    override fun observeGoals(): Flow<List<GoalWithSavings>> = goals
    override fun observeGoal(goalId: Long): Flow<GoalWithSavings?> =
        goals.map { list -> list.firstOrNull { it.goal.id == goalId } }
    override fun observeContributions(goalId: Long): Flow<List<SavingsContribution>> = contributions

    override suspend fun createGoal(
        productName: String,
        productUrl: String,
        targetAmountMinor: Long,
        currencyCode: String,
    ): Long {
        createFailure?.let { throw it }
        lastCreatedName = productName
        val id = nextGoalId++
        val goal = SavingsGoal(id, productName, productUrl, Money(targetAmountMinor, currencyCode), 0, 0)
        goals.value = goals.value + GoalWithSavings(goal, Money(0, currencyCode))
        return id
    }

    override suspend fun addContribution(goalId: Long, amountMinor: Long): Long {
        addFailure?.let { throw it }
        val goal = goals.value.first { it.goal.id == goalId }
        val contribution = SavingsContribution(
            id = nextContributionId++,
            goalId = goalId,
            amount = Money(amountMinor, goal.goal.target.currencyCode),
            createdAtEpochMillis = 0,
        )
        contributions.value = contributions.value + contribution
        val saved = contributions.value.filter { it.goalId == goalId }.sumOf { it.amount.amountMinor }
        goals.value = goals.value.map {
            if (it.goal.id == goalId) GoalWithSavings(it.goal, Money(saved, it.goal.target.currencyCode)) else it
        }
        return contribution.id
    }

    override suspend fun deleteGoal(goalId: Long) {
        deleteCallCount++
        deleteFailure?.let { throw it }
        goals.value = goals.value.filterNot { it.goal.id == goalId }
        contributions.value = contributions.value.filterNot { it.goalId == goalId }
    }

    override suspend fun deleteAllData() {
        goals.value = emptyList()
        contributions.value = emptyList()
    }

    override suspend fun snapshot(): SavingsDataSnapshot = SavingsDataSnapshot(
        goals = goals.value.map { item ->
            SavingsGoalEntity(
                id = item.goal.id,
                productName = item.goal.productName,
                productUrl = item.goal.productUrl,
                targetAmountMinor = item.goal.target.amountMinor,
                currencyCode = item.goal.target.currencyCode,
                createdAtEpochMillis = item.goal.createdAtEpochMillis,
                updatedAtEpochMillis = item.goal.updatedAtEpochMillis,
            )
        },
        contributions = contributions.value.map {
            SavingsContributionEntity(it.id, it.goalId, it.amount.amountMinor, it.createdAtEpochMillis)
        },
    )

    override suspend fun replaceAll(snapshot: SavingsDataSnapshot) {
        goals.value = snapshot.goals.map {
            GoalWithSavings(
                SavingsGoal(it.id, it.productName, it.productUrl, Money(it.targetAmountMinor, it.currencyCode), it.createdAtEpochMillis, it.updatedAtEpochMillis),
                Money(0, it.currencyCode),
            )
        }
        contributions.value = snapshot.contributions.map { entity ->
            val currency = snapshot.goals.first { it.id == entity.goalId }.currencyCode
            entity.toModel(currency)
        }
    }
}
