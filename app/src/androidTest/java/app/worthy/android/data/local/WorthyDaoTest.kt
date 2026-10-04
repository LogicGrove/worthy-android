package app.worthy.android.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorthyDaoTest {
    private lateinit var database: WorthyDatabase
    private lateinit var dao: WorthyDao

    @Before fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder<WorthyDatabase>(ApplicationProvider.getApplicationContext<Context>())
            .setDriver(AndroidSQLiteDriver())
            .build()
        dao = database.worthyDao()
    }

    @After fun closeDatabase() = runBlocking { database.close() }

    @Test fun goalAndContributionsAreStoredAndAggregated() = runBlocking {
        val goalId = dao.insertGoal(
            SavingsGoalEntity(
                productName = "Camera",
                productUrl = "https://example.com/camera",
                targetAmountMinor = 10_000,
                currencyCode = "EUR",
                createdAtEpochMillis = 1,
                updatedAtEpochMillis = 1,
            ),
        )
        assertEquals(0L, dao.observeGoalWithSavedAmount(goalId).first()?.savedAmountMinor)
        dao.insertContributionForGoal(SavingsContributionEntity(goalId = goalId, amountMinor = 1_500, createdAtEpochMillis = 2))
        dao.insertContributionForGoal(SavingsContributionEntity(goalId = goalId, amountMinor = 2_000, createdAtEpochMillis = 3))
        assertEquals(3_500L, dao.observeGoalWithSavedAmount(goalId).first()?.savedAmountMinor)
        assertEquals(2, dao.observeContributions(goalId).first().size)
        assertFalse(dao.observeGoalsWithSavedAmount().first().isEmpty())
        dao.deleteGoalById(goalId)
        assertTrue(dao.observeContributions(goalId).first().isEmpty())
    }

    @Test fun deleteAllSavingsDataRemovesGoalsAndContributions() = runBlocking {
        val goalId = dao.insertGoal(SavingsGoalEntity(productName = "Camera", productUrl = "https://example.com/camera", targetAmountMinor = 10_000, currencyCode = "EUR", createdAtEpochMillis = 1, updatedAtEpochMillis = 1))
        dao.insertContributionForGoal(SavingsContributionEntity(goalId = goalId, amountMinor = 500, createdAtEpochMillis = 2))

        dao.deleteAllSavingsData()

        assertTrue(dao.getAllGoals().isEmpty())
        assertTrue(dao.getAllContributions().isEmpty())
    }

    @Test fun replaceAllSavingsDataPreservesRelationships() = runBlocking {
        dao.replaceAllSavingsData(
            goals = listOf(SavingsGoalEntity(id = 42, productName = "Camera", productUrl = "https://example.com/camera", targetAmountMinor = 10_000, currencyCode = "EUR", createdAtEpochMillis = 1, updatedAtEpochMillis = 1)),
            contributions = listOf(SavingsContributionEntity(id = 99, goalId = 42, amountMinor = 500, createdAtEpochMillis = 2)),
        )

        assertEquals(42L, dao.getAllGoals().single().id)
        assertEquals(42L, dao.getAllContributions().single().goalId)
    }
}
