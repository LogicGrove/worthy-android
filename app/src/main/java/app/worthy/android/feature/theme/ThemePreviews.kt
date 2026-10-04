package app.worthy.android.feature.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.worthy.android.data.settings.ColorStyle
import app.worthy.android.data.settings.ThemeMode
import app.worthy.android.ui.theme.WorthyTheme

@Preview(showBackground = true)
@Composable
private fun ThemeScreenLightPreview() {
    WorthyTheme(themeMode = ThemeMode.LIGHT, dynamicColor = false, colorStyle = ColorStyle.RED) {
        ThemeScreen(ThemeUiState(ThemeMode.LIGHT, false, ColorStyle.RED), true, {}, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemeScreenDarkPreview() {
    WorthyTheme(themeMode = ThemeMode.DARK, dynamicColor = false, colorStyle = ColorStyle.BLUE) {
        ThemeScreen(ThemeUiState(ThemeMode.DARK, false, ColorStyle.BLUE), true, {}, {}, {}, {})
    }
}
