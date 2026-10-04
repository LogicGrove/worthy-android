package app.worthy.android.core.model

import java.math.BigDecimal
import java.math.RoundingMode

data class SavingsProgress(
    val savedAmountMinor: Long,
    val remainingAmountMinor: Long,
    val visualProgress: Float,
)

fun calculateSavingsProgress(targetAmountMinor: Long, savedAmountMinor: Long): SavingsProgress {
    require(targetAmountMinor > 0) { "Target amount must be positive" }
    require(savedAmountMinor >= 0) { "Saved amount cannot be negative" }

    val remaining = if (savedAmountMinor >= targetAmountMinor) {
        0L
    } else {
        targetAmountMinor - savedAmountMinor
    }
    val visualProgress = BigDecimal.valueOf(savedAmountMinor)
        .divide(BigDecimal.valueOf(targetAmountMinor), 6, RoundingMode.HALF_UP)
        .coerceIn(BigDecimal.ZERO, BigDecimal.ONE)
        .toFloat()

    return SavingsProgress(
        savedAmountMinor = savedAmountMinor,
        remainingAmountMinor = remaining,
        visualProgress = visualProgress,
    )
}

fun sumContributionAmounts(amounts: Iterable<Long>): Long =
    amounts.fold(0L) { total, amount ->
        require(amount > 0) { "Contribution amount must be positive" }
        Math.addExact(total, amount)
    }
