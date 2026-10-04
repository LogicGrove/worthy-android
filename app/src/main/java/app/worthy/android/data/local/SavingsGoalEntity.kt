package app.worthy.android.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productName: String,
    val productUrl: String,
    val targetAmountMinor: Long,
    val currencyCode: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
