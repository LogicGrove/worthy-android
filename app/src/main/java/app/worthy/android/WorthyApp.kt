package app.worthy.android

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.worthy.android.data.repository.SavingsRepository
import app.worthy.android.data.settings.SettingsRepository
import app.worthy.android.data.backup.BackupService
import app.worthy.android.navigation.WorthyNavigation
import app.worthy.android.ui.theme.WorthyTheme
import app.worthy.android.ui.theme.resolveDarkTheme

@Composable
fun WorthyApp(
    repository: SavingsRepository,
    settingsRepository: SettingsRepository,
    backupService: BackupService,
    modifier: Modifier = Modifier,
) {
    val settings = settingsRepository.settings.collectAsStateWithLifecycle().value
    val darkTheme = resolveDarkTheme(settings.themeMode, isSystemInDarkTheme())
    val view = LocalView.current
    SideEffect {
        view.context.findActivity()?.window?.let { window ->
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    WorthyTheme(
        themeMode = settings.themeMode,
        dynamicColor = settings.dynamicColorEnabled,
        colorStyle = settings.colorStyle,
        darkTheme = darkTheme,
    ) {
        WorthyNavigation(
            repository = repository,
            settingsRepository = settingsRepository,
            settings = settings,
            backupService = backupService,
            modifier = modifier.fillMaxSize(),
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
