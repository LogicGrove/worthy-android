package app.worthy.android.core.money

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyInputTest {
    @Test fun `parses decimal amount into minor units`() {
        assertEquals(MoneyInputResult.Valid(12_345), parseMoneyInput("123.45", "EUR", Locale.US))
    }

    @Test fun `uses locale decimal separator`() {
        assertEquals(MoneyInputResult.Valid(1_050), parseMoneyInput("10,50", "EUR", Locale.GERMANY))
    }

    @Test fun `supports zero fraction currency`() {
        assertEquals(MoneyInputResult.Valid(500), parseMoneyInput("500", "JPY", Locale.JAPAN))
    }

    @Test fun `rejects zero negative and excessive precision`() {
        assertEquals(MoneyInputResult.NotPositive, parseMoneyInput("0", "EUR", Locale.US))
        assertEquals(MoneyInputResult.NotPositive, parseMoneyInput("-1", "EUR", Locale.US))
        assertEquals(MoneyInputResult.TooManyFractionDigits, parseMoneyInput("1.001", "EUR", Locale.US))
    }

    @Test fun `rejects malformed and overflowing values`() {
        assertEquals(MoneyInputResult.Invalid, parseMoneyInput("12 money", "EUR", Locale.US))
        assertTrue(parseMoneyInput("999999999999999999999", "EUR", Locale.US) is MoneyInputResult.Overflow)
    }
}
