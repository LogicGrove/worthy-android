package app.worthy.android.data.settings

import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import java.util.Locale
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsRepositoryTest {
    @Test fun `high refresh rate defaults on to preserve existing behavior`() {
        assertTrue(appSettingsFrom(emptyPreferences(), Locale.US).highRefreshRateEnabled)
    }

    @Test fun `explicit high refresh preference is restored`() {
        val preferences = mutablePreferencesOf(
            DataStoreSettingsRepository.HIGH_REFRESH_RATE_ENABLED to false,
        )
        assertFalse(appSettingsFrom(preferences, Locale.US).highRefreshRateEnabled)
    }

    @Test fun `theme defaults preserve prior system and dynamic behavior`() {
        val settings = appSettingsFrom(emptyPreferences(), Locale.US)
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertTrue(settings.dynamicColorEnabled)
        assertEquals(ColorStyle.RED, settings.colorStyle)
    }

    @Test fun `semantic theme values deserialize and invalid values use defaults`() {
        val stored = mutablePreferencesOf(
            DataStoreSettingsRepository.THEME_MODE to "dark",
            DataStoreSettingsRepository.DYNAMIC_COLOR_ENABLED to false,
            DataStoreSettingsRepository.COLOR_STYLE to "blue",
        )
        val restored = appSettingsFrom(stored, Locale.US)
        assertEquals(ThemeMode.DARK, restored.themeMode)
        assertFalse(restored.dynamicColorEnabled)
        assertEquals(ColorStyle.BLUE, restored.colorStyle)

        val invalid = appSettingsFrom(mutablePreferencesOf(
            DataStoreSettingsRepository.THEME_MODE to "2",
            DataStoreSettingsRepository.COLOR_STYLE to "purple",
        ), Locale.US)
        assertEquals(ThemeMode.SYSTEM, invalid.themeMode)
        assertEquals(ColorStyle.RED, invalid.colorStyle)
    }

    @Test fun `enum serialization values remain stable and semantic`() {
        assertEquals(listOf("system", "light", "dark"), ThemeMode.entries.map { it.storedValue })
        assertEquals(listOf("red", "blue", "green", "orange", "pink"), ColorStyle.entries.map { it.storedValue })
    }
}
