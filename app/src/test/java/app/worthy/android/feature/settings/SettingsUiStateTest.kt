package app.worthy.android.feature.settings

import app.worthy.android.data.settings.AppLanguage
import app.worthy.android.data.settings.AppSettings
import org.junit.Assert.assertFalse
import org.junit.Test

class SettingsUiStateTest {
    @Test fun `app settings expose high refresh rate in UI state`() {
        val state = SettingsUiState().withSettings(
            AppSettings(
                defaultCurrencyCode = "EUR",
                hapticFeedbackEnabled = true,
                highRefreshRateEnabled = false,
                appLanguage = AppLanguage.SystemDefault,
            ),
        )
        assertFalse(state.highRefreshRateEnabled)
    }
}
