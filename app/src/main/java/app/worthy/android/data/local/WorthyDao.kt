package app.worthy.android.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorthyDao {
    @Query(
        """
        SELECT goals.*, COALESCE(SUM(contributions.amountMinor), 0) AS savedAmountMinor
        FROM savings_goals AS goals
        LEFT JOIN savings_contributions AS contributions ON contributions.goalId = goals.id
        GROUP BY goals.id
        ORDER BY goals.createdAtEpochMillis DESC, goals.id DESC
        """,
    )
    fun observeGoalsWithSavedAmount(): Flow<List<GoalWithSavedAmountRow>>

    @Query(
        """
        SELECT goals.*, COALESCE(SUM(contributions.amountMinor), 0) AS savedAmountMinor
        FROM savings_goals AS goals
        LEFT JOIN savings_contributions AS contributions ON contributions.goalId = goals.id
        WHERE goals.id = :goalId
        GROUP BY goals.id
        """,
    )
    fun observeGoalWithSavedAmount(goalId: Long): Flow<GoalWithSavedAmountRow?>

    @Query(
        """
        SELECT * FROM savings_contributions
        WHERE goalId = :goalId
        ORDER BY createdAtEpochMillis ASC, id ASC
        """,
    )
    fun observeContributions(goalId: Long): Flow<List<SavingsContributionEntity>>

    @Insert
    suspend fun insertGoal(goal: SavingsGoalEntity): Long

    @Insert
    suspend fun insertContribution(contribution: SavingsContributionEntity): Long

    @Query("SELECT * FROM savings_goals ORDER BY id ASC")
    suspend fun getAllGoals(): List<SavingsGoalEntity>

    @Query("SELECT * FROM savings_contributions ORDER BY id ASC")
    suspend fun getAllContributions(): List<SavingsContributionEntity>

    @Query("DELETE FROM savings_contributions")
    suspend fun deleteAllContributions()

    @Query("DELETE FROM savings_goals")
    suspend fun deleteAllGoals()

    @Query("SELECT EXISTS(SELECT 1 FROM savings_goals WHERE id = :goalId)")
    suspend fun goalExists(goalId: Long): Boolean

    @Query("DELETE FROM savings_goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)

    @Transaction
    suspend fun insertContributionForGoal(contribution: SavingsContributionEntity): Long {
        require(goalExists(contribution.goalId)) { "Savings goal does not exist" }
        return insertContribution(contribution)
    }

    @Transaction
    suspend fun deleteAllSavingsData() {
        deleteAllContributions()
        deleteAllGoals()
    }

    @Transaction
    suspend fun replaceAllSavingsData(
        goals: List<SavingsGoalEntity>,
        contributions: List<SavingsContributionEntity>,
    ) {
        deleteAllSavingsData()
        goals.forEach { insertGoal(it) }
        contributions.forEach { insertContribution(it) }
    }
}
