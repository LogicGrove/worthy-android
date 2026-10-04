package app.worthy.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import app.worthy.android.data.settings.ColorStyle
import app.worthy.android.data.settings.ThemeMode

fun isDynamicColorSupported(sdkInt: Int = Build.VERSION.SDK_INT) = sdkInt >= Build.VERSION_CODES.S

fun resolveDarkTheme(themeMode: ThemeMode, systemInDarkTheme: Boolean) = when (themeMode) {
    ThemeMode.SYSTEM -> systemInDarkTheme
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

internal fun customColorScheme(style: ColorStyle, dark: Boolean): ColorScheme = when (style) {
    ColorStyle.RED -> if (dark) RedDarkScheme else RedLightScheme
    ColorStyle.BLUE -> if (dark) BlueDarkScheme else BlueLightScheme
    ColorStyle.GREEN -> if (dark) GreenDarkScheme else GreenLightScheme
    ColorStyle.ORANGE -> if (dark) OrangeDarkScheme else OrangeLightScheme
    ColorStyle.PINK -> if (dark) PinkDarkScheme else PinkLightScheme
}

@Composable
fun WorthyTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    colorStyle: ColorStyle = ColorStyle.RED,
    darkTheme: Boolean = resolveDarkTheme(themeMode, isSystemInDarkTheme()),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val dynamicColorEnabled = dynamicColor && isDynamicColorSupported()
    val scheme = remember(dynamicColorEnabled, darkTheme, colorStyle, context, configuration) {
        if (dynamicColorEnabled) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            customColorScheme(colorStyle, darkTheme)
        }
    }
    MaterialTheme(colorScheme = scheme, typography = Typography, shapes = WorthyShapes, content = content)
}
