package app.worthy.android.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import app.worthy.android.core.model.Money
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatMoney(money: Money, locale: Locale = Locale.getDefault()): String {
    val currency = Currency.getInstance(money.currencyCode)
    val amount = BigDecimal.valueOf(money.amountMinor, currency.defaultFractionDigits.coerceAtLeast(0))
    return NumberFormat.getCurrencyInstance(locale).apply { this.currency = currency }.format(amount)
}

@Composable
fun MoneyText(
    money: Money,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    Text(text = formatMoney(money), modifier = modifier, style = style)
}
