package app.worthy.android.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SavingsProgressTest {
    @Test fun `partial savings derives remaining and progress`() {
        val result = calculateSavingsProgress(10_000, 2_500)
        assertEquals(2_500, result.savedAmountMinor)
        assertEquals(7_500, result.remainingAmountMinor)
        assertEquals(0.25f, result.visualProgress, 0.0001f)
    }

    @Test fun `over saving preserves total and clamps only visual values`() {
        val result = calculateSavingsProgress(10_000, 12_500)
        assertEquals(12_500, result.savedAmountMinor)
        assertEquals(0, result.remainingAmountMinor)
        assertEquals(1f, result.visualProgress, 0f)
    }

    @Test fun `contribution sum is exact and detects overflow`() {
        assertEquals(600, sumContributionAmounts(listOf(100, 200, 300)))
        org.junit.Assert.assertThrows(ArithmeticException::class.java) {
            sumContributionAmounts(listOf(Long.MAX_VALUE, 1))
        }
    }
}
