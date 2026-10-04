package app.worthy.android.core.validation

import java.net.URI
import java.util.Currency
import java.util.Locale

fun isValidProductName(value: String): Boolean = value.isNotBlank()

fun isValidProductUrl(value: String): Boolean = runCatching {
    val uri = URI(value.trim())
    (uri.scheme.equals("http", ignoreCase = true) || uri.scheme.equals("https", ignoreCase = true)) &&
        !uri.host.isNullOrBlank()
}.getOrDefault(false)

fun normalizedCurrencyCodeOrNull(value: String): String? {
    val normalized = value.trim().uppercase(Locale.ROOT)
    if (normalized.length != 3) return null
    return runCatching { Currency.getInstance(normalized).currencyCode }.getOrNull()
}
