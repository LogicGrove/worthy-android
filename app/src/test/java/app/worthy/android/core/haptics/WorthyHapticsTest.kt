package app.worthy.android.core.haptics

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorthyHapticsTest {
    @Test fun `haptic requires enabled setting and physical vibrator`() {
        assertTrue(shouldPerformWorthyHaptic(enabled = true, hasVibrator = true))
        assertFalse(shouldPerformWorthyHaptic(enabled = false, hasVibrator = true))
        assertFalse(shouldPerformWorthyHaptic(enabled = true, hasVibrator = false))
    }
}
