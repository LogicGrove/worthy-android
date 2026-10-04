package app.worthy.android.core.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContributionValidationTest {
    @Test fun `only positive minor units are accepted`() {
        assertTrue(isValidContributionAmount(1))
        assertFalse(isValidContributionAmount(0))
        assertFalse(isValidContributionAmount(-1))
    }
}
