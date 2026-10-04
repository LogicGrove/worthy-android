package app.worthy.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import app.worthy.android.data.settings.AppLanguage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            (application as WorthyApplication).container.settingsRepository.settings
                .map { it.appLanguage }
                .distinctUntilChanged()
                .collect { language ->
                    val locales = if (language == AppLanguage.English) LocaleListCompat.forLanguageTags("en") else LocaleListCompat.getEmptyLocaleList()
                    if (AppCompatDelegate.getApplicationLocales() != locales) AppCompatDelegate.setApplicationLocales(locales)
                }
        }
        enableEdgeToEdge()
        setContent {
            val container = (application as WorthyApplication).container
            WorthyApp(
                repository = container.savingsRepository,
                settingsRepository = container.settingsRepository,
                backupService = container.backupService,
            )
        }
    }
}
