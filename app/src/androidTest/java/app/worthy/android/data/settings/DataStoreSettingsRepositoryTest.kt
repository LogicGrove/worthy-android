package app.worthy.android.data.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.Locale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataStoreSettingsRepositoryTest {
    @Test fun settingsPersistAcrossRepositoryInstances() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val first = DataStoreSettingsRepository(context, localeProvider = { Locale.US })
        first.setDefaultCurrency("JPY")
        first.setHapticFeedbackEnabled(false)
        first.setHighRefreshRateEnabled(false)
        first.setAppLanguage(AppLanguage.English)
        first.setThemeMode(ThemeMode.DARK)
        first.setColorStyle(ColorStyle.BLUE)
        first.setDynamicColorEnabled(true)

        val restored = DataStoreSettingsRepository(context, localeProvider = { Locale.US }).settings.first {
            it.defaultCurrencyCode == "JPY" && it.themeMode == ThemeMode.DARK && it.colorStyle == ColorStyle.BLUE
        }

        assertEquals("JPY", restored.defaultCurrencyCode)
        assertEquals(false, restored.hapticFeedbackEnabled)
        assertEquals(false, restored.highRefreshRateEnabled)
        assertEquals(AppLanguage.English, restored.appLanguage)
        assertEquals(ThemeMode.DARK, restored.themeMode)
        assertEquals(ColorStyle.BLUE, restored.colorStyle)
        assertEquals(true, restored.dynamicColorEnabled)

        first.setDynamicColorEnabled(false)
        val dynamicDisabled = first.settings.first { !it.dynamicColorEnabled }
        assertEquals(ColorStyle.BLUE, dynamicDisabled.colorStyle)
    }
}
