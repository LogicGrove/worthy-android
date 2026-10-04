package app.worthy.android.ui.theme

import app.worthy.android.data.settings.ColorStyle
import app.worthy.android.data.settings.ThemeMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeResolutionTest {
    @Test fun `system follows current system appearance`() {
        assertTrue(resolveDarkTheme(ThemeMode.SYSTEM, true))
        assertFalse(resolveDarkTheme(ThemeMode.SYSTEM, false))
    }

    @Test fun `light and dark override system appearance`() {
        assertFalse(resolveDarkTheme(ThemeMode.LIGHT, true))
        assertTrue(resolveDarkTheme(ThemeMode.DARK, false))
    }

    @Test fun `dynamic color support begins at Android 12`() {
        assertFalse(isDynamicColorSupported(30))
        assertTrue(isDynamicColorSupported(31))
    }

    @Test fun `each color style resolves to its own light scheme family`() {
        val expected = mapOf(
            ColorStyle.RED to RedLightScheme,
            ColorStyle.BLUE to BlueLightScheme,
            ColorStyle.GREEN to GreenLightScheme,
            ColorStyle.ORANGE to OrangeLightScheme,
            ColorStyle.PINK to PinkLightScheme,
        )
        expected.forEach { (style, scheme) ->
            assertSame(scheme, customColorScheme(style, dark = false))
        }
        expected.filterKeys { it != ColorStyle.RED }.values.forEach { scheme ->
            assertNotEquals(RedLightScheme.primary, scheme.primary)
            assertNotEquals(RedLightScheme.background, scheme.background)
        }
    }

    @Test fun `light and dark select corresponding scheme for every style`() {
        val darkSchemes = mapOf(
            ColorStyle.RED to RedDarkScheme,
            ColorStyle.BLUE to BlueDarkScheme,
            ColorStyle.GREEN to GreenDarkScheme,
            ColorStyle.ORANGE to OrangeDarkScheme,
            ColorStyle.PINK to PinkDarkScheme,
        )
        darkSchemes.forEach { (style, scheme) ->
            assertSame(scheme, customColorScheme(style, dark = true))
            assertNotEquals(customColorScheme(style, dark = false).background, scheme.background)
        }
    }
}
