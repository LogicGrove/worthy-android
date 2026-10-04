package app.worthy.android.data.local

data class GoalWithSavedAmountRow(
    val id: Long,
    val productName: String,
    val productUrl: String,
    val targetAmountMinor: Long,
    val currencyCode: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val savedAmountMinor: Long,
)
