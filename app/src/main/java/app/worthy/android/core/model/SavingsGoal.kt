package app.worthy.android.core.model

data class SavingsGoal(
    val id: Long,
    val productName: String,
    val productUrl: String,
    val target: Money,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
