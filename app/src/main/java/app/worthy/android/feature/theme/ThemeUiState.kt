package app.worthy.android.feature.theme

import app.worthy.android.data.settings.ColorStyle
import app.worthy.android.data.settings.ThemeMode

data class ThemeUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    val colorStyle: ColorStyle = ColorStyle.RED,
)
