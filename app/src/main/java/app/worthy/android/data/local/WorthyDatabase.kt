package app.worthy.android.data.local

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [SavingsGoalEntity::class, SavingsContributionEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class WorthyDatabase : RoomDatabase() {
    abstract fun worthyDao(): WorthyDao
}
