package app.worthy.android.core.model

data class SavingsContribution(
    val id: Long,
    val goalId: Long,
    val amount: Money,
    val createdAtEpochMillis: Long,
)
