package app.worthy.android.feature.settings

import app.worthy.android.data.repository.SavingsDataSnapshot
import app.worthy.android.data.settings.AppLanguage

data class SettingsUiState(
    val defaultCurrencyCode: String = "EUR",
    val hapticFeedbackEnabled: Boolean = true,
    val highRefreshRateEnabled: Boolean = true,
    val appLanguage: AppLanguage = AppLanguage.SystemDefault,
    val isWorking: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val pendingImport: SavingsDataSnapshot? = null,
    val message: SettingsMessage? = null,
)

enum class SettingsMessage { Exported, ExportFailed, ImportInvalid, Imported, ImportFailed, Deleted, DeleteFailed }
