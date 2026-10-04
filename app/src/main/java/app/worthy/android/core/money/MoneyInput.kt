package app.worthy.android.core.money

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParsePosition
import java.util.Currency
import java.util.Locale

sealed interface MoneyInputResult {
    data class Valid(val amountMinor: Long) : MoneyInputResult
    data object Empty : MoneyInputResult
    data object Invalid : MoneyInputResult
    data object NotPositive : MoneyInputResult
    data object TooManyFractionDigits : MoneyInputResult
    data object Overflow : MoneyInputResult
}

fun parseMoneyInput(
    text: String,
    currencyCode: String,
    locale: Locale = Locale.getDefault(),
): MoneyInputResult {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return MoneyInputResult.Empty

    val currency = runCatching { Currency.getInstance(currencyCode.uppercase(Locale.ROOT)) }
        .getOrNull() ?: return MoneyInputResult.Invalid
    val format = (DecimalFormat.getNumberInstance(locale) as DecimalFormat).apply {
        isParseBigDecimal = true
    }
    val position = ParsePosition(0)
    val amount = format.parse(trimmed, position) as? BigDecimal ?: return MoneyInputResult.Invalid
    if (position.index != trimmed.length) return MoneyInputResult.Invalid
    if (amount.signum() <= 0) return MoneyInputResult.NotPositive

    val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
    val minor = try {
        amount.movePointRight(fractionDigits).setScale(0, RoundingMode.UNNECESSARY)
    } catch (_: ArithmeticException) {
        return MoneyInputResult.TooManyFractionDigits
    }
    return try {
        MoneyInputResult.Valid(minor.longValueExact())
    } catch (_: ArithmeticException) {
        MoneyInputResult.Overflow
    }
}
