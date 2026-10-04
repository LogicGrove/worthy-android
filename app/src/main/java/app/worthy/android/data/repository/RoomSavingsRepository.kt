package app.worthy.android.data.repository

import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.core.model.SavingsContribution
import app.worthy.android.core.validation.isValidContributionAmount
import app.worthy.android.core.validation.isValidProductName
import app.worthy.android.core.validation.isValidProductUrl
import app.worthy.android.core.validation.normalizedCurrencyCodeOrNull
import app.worthy.android.data.local.SavingsContributionEntity
import app.worthy.android.data.local.SavingsGoalEntity
import app.worthy.android.data.local.WorthyDao
import app.worthy.android.data.local.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class RoomSavingsRepository(
    private val dao: WorthyDao,
    private val timeProvider: () -> Long = System::currentTimeMillis,
) : SavingsRepository {
    override fun observeGoals(): Flow<List<GoalWithSavings>> =
        dao.observeGoalsWithSavedAmount().map { rows -> rows.map { it.toModel() } }

    override fun observeGoal(goalId: Long): Flow<GoalWithSavings?> =
        dao.observeGoalWithSavedAmount(goalId).map { it?.toModel() }

    override fun observeContributions(goalId: Long): Flow<List<SavingsContribution>> =
        combine(
            dao.observeGoalWithSavedAmount(goalId),
            dao.observeContributions(goalId),
        ) { goal, contributions ->
            val currencyCode = goal?.currencyCode ?: return@combine emptyList()
            contributions.map { it.toModel(currencyCode) }
        }

    override suspend fun createGoal(
        productName: String,
        productUrl: String,
        targetAmountMinor: Long,
        currencyCode: String,
    ): Long {
        require(isValidProductName(productName))
        require(isValidProductUrl(productUrl))
        require(targetAmountMinor > 0)
        val normalizedCurrency = requireNotNull(normalizedCurrencyCodeOrNull(currencyCode))
        val now = timeProvider()
        return dao.insertGoal(
            SavingsGoalEntity(
                productName = productName.trim(),
                productUrl = productUrl.trim(),
                targetAmountMinor = targetAmountMinor,
                currencyCode = normalizedCurrency,
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now,
            ),
        )
    }

    override suspend fun addContribution(goalId: Long, amountMinor: Long): Long {
        require(isValidContributionAmount(amountMinor))
        return dao.insertContributionForGoal(
            SavingsContributionEntity(
                goalId = goalId,
                amountMinor = amountMinor,
                createdAtEpochMillis = timeProvider(),
            ),
        )
    }

    override suspend fun deleteGoal(goalId: Long) {
        dao.deleteGoalById(goalId)
    }

    override suspend fun deleteAllData() = dao.deleteAllSavingsData()

    override suspend fun snapshot() = SavingsDataSnapshot(
        goals = dao.getAllGoals(),
        contributions = dao.getAllContributions(),
    )

    override suspend fun replaceAll(snapshot: SavingsDataSnapshot) {
        dao.replaceAllSavingsData(snapshot.goals, snapshot.contributions)
    }
}
