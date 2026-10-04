package app.worthy.android.core.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GoalValidationTest {
    @Test fun `product name must contain content`() {
        assertFalse(isValidProductName("  "))
        assertTrue(isValidProductName("Camera"))
    }

    @Test fun `only complete http and https URLs are valid`() {
        assertTrue(isValidProductUrl("https://example.com/item"))
        assertTrue(isValidProductUrl("http://example.com"))
        assertFalse(isValidProductUrl("ftp://example.com"))
        assertFalse(isValidProductUrl("https:///item"))
        assertFalse(isValidProductUrl("not a url"))
    }

    @Test fun `currency code is normalized and validated`() {
        assertEquals("EUR", normalizedCurrencyCodeOrNull(" eur "))
        assertNull(normalizedCurrencyCodeOrNull("ZZZ"))
    }
}
