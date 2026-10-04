package app.worthy.android.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.util.Currency
import java.util.Locale
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

enum class AppLanguage(val storedValue: String) {
    SystemDefault("system"),
    English("en"),
}

enum class ThemeMode(val storedValue: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark"),
}

enum class ColorStyle(val storedValue: String) {
    RED("red"),
    BLUE("blue"),
    GREEN("green"),
    ORANGE("orange"),
    PINK("pink"),
}

data class AppSettings(
    val defaultCurrencyCode: String,
    val hapticFeedbackEnabled: Boolean,
    val highRefreshRateEnabled: Boolean,
    val appLanguage: AppLanguage,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    val colorStyle: ColorStyle = ColorStyle.RED,
)

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    suspend fun setDefaultCurrency(code: String)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)
    suspend fun setHighRefreshRateEnabled(enabled: Boolean)
    suspend fun setAppLanguage(language: AppLanguage)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColorEnabled(enabled: Boolean)
    suspend fun setColorStyle(style: ColorStyle)
}

class DataStoreSettingsRepository(
    private val context: Context,
    private val localeProvider: () -> Locale = Locale::getDefault,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) : SettingsRepository {
    override val settings: StateFlow<AppSettings> = context.settingsDataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw error
        }
        .map { preferences -> appSettingsFrom(preferences, localeProvider()) }
        .stateIn(scope, SharingStarted.Eagerly, defaultAppSettings(localeProvider()))

    override suspend fun setDefaultCurrency(code: String) {
        val normalized = code.uppercase(Locale.ROOT)
        require(isIsoCurrency(normalized))
        context.settingsDataStore.edit { it[DEFAULT_CURRENCY] = normalized }
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[HAPTICS_ENABLED] = enabled }
    }

    override suspend fun setHighRefreshRateEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[HIGH_REFRESH_RATE_ENABLED] = enabled }
    }

    override suspend fun setAppLanguage(language: AppLanguage) {
        context.settingsDataStore.edit { it[APP_LANGUAGE] = language.storedValue }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[THEME_MODE] = mode.storedValue }
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[DYNAMIC_COLOR_ENABLED] = enabled }
    }

    override suspend fun setColorStyle(style: ColorStyle) {
        context.settingsDataStore.edit { it[COLOR_STYLE] = style.storedValue }
    }

    companion object {
        val DEFAULT_CURRENCY = stringPreferencesKey("default_currency_code")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptic_feedback_enabled")
        val HIGH_REFRESH_RATE_ENABLED = booleanPreferencesKey("high_refresh_rate_enabled")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
        val COLOR_STYLE = stringPreferencesKey("color_style")
    }
}

internal fun appSettingsFrom(preferences: Preferences, locale: Locale): AppSettings = AppSettings(
    defaultCurrencyCode = preferences[DataStoreSettingsRepository.DEFAULT_CURRENCY]
        ?.takeIf(::isIsoCurrency)
        ?: localCurrencyOrFallback(locale),
    hapticFeedbackEnabled = preferences[DataStoreSettingsRepository.HAPTICS_ENABLED] ?: true,
    highRefreshRateEnabled = preferences[DataStoreSettingsRepository.HIGH_REFRESH_RATE_ENABLED] ?: true,
    appLanguage = AppLanguage.entries.firstOrNull {
        it.storedValue == preferences[DataStoreSettingsRepository.APP_LANGUAGE]
    } ?: AppLanguage.SystemDefault,
    themeMode = ThemeMode.entries.firstOrNull {
        it.storedValue == preferences[DataStoreSettingsRepository.THEME_MODE]
    } ?: ThemeMode.SYSTEM,
    dynamicColorEnabled = preferences[DataStoreSettingsRepository.DYNAMIC_COLOR_ENABLED] ?: true,
    colorStyle = ColorStyle.entries.firstOrNull {
        it.storedValue == preferences[DataStoreSettingsRepository.COLOR_STYLE]
    } ?: ColorStyle.RED,
)

fun defaultAppSettings(locale: Locale = Locale.getDefault()) = AppSettings(
    defaultCurrencyCode = localCurrencyOrFallback(locale),
    hapticFeedbackEnabled = true,
    highRefreshRateEnabled = true,
    appLanguage = AppLanguage.SystemDefault,
)

fun localCurrencyOrFallback(locale: Locale): String =
    runCatching { Currency.getInstance(locale).currencyCode }.getOrDefault("EUR")

private fun isIsoCurrency(code: String): Boolean =
    runCatching { Currency.getInstance(code).currencyCode == code }.getOrDefault(false)
