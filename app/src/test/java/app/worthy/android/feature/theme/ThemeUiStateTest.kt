package app.worthy.android.feature.theme

import app.worthy.android.data.settings.AppLanguage
import app.worthy.android.data.settings.AppSettings
import app.worthy.android.data.settings.ColorStyle
import app.worthy.android.data.settings.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeUiStateTest {
    @Test fun `custom color remains selected while dynamic color is enabled`() {
        val state = AppSettings("EUR", true, true, AppLanguage.SystemDefault, ThemeMode.SYSTEM, true, ColorStyle.GREEN)
            .toThemeUiState()
        assertTrue(state.dynamicColorEnabled)
        assertEquals(ColorStyle.GREEN, state.colorStyle)
    }

    @Test fun `disabling dynamic color does not alter selected style`() {
        val enabled = ThemeUiState(dynamicColorEnabled = true, colorStyle = ColorStyle.PINK)
        val disabled = enabled.copy(dynamicColorEnabled = false)
        assertEquals(ColorStyle.PINK, disabled.colorStyle)
    }
}
