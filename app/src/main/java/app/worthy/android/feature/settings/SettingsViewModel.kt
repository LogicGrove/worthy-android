package app.worthy.android.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.worthy.android.data.backup.BackupService
import app.worthy.android.data.repository.SavingsRepository
import app.worthy.android.data.settings.AppLanguage
import app.worthy.android.data.settings.SettingsRepository
import app.worthy.android.data.settings.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val savingsRepository: SavingsRepository,
    private val backupService: BackupService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.withSettings(settings) }
            }
        }
    }

    fun setDefaultCurrency(code: String) = updateSettingsOptimistically(
        update = { it.copy(defaultCurrencyCode = code) },
        persist = { settingsRepository.setDefaultCurrency(code) },
    )

    fun setHapticFeedbackEnabled(enabled: Boolean) = updateSettingsOptimistically(
        update = { it.copy(hapticFeedbackEnabled = enabled) },
        persist = { settingsRepository.setHapticFeedbackEnabled(enabled) },
    )

    fun setHighRefreshRateEnabled(enabled: Boolean) = updateSettingsOptimistically(
        update = { it.copy(highRefreshRateEnabled = enabled) },
        persist = { settingsRepository.setHighRefreshRateEnabled(enabled) },
    )

    fun setAppLanguage(language: AppLanguage) = updateSettingsOptimistically(
        update = { it.copy(appLanguage = language) },
        persist = { settingsRepository.setAppLanguage(language) },
    )

    private fun updateSettingsOptimistically(
        update: (SettingsUiState) -> SettingsUiState,
        persist: suspend () -> Unit,
    ) = viewModelScope.launch {
        _uiState.update(update)
        runCatching { persist() }.onFailure {
            _uiState.update { it.withSettings(settingsRepository.settings.value) }
        }
    }

    fun requestDeleteAll() = _uiState.update { if (it.isWorking) it else it.copy(showDeleteConfirmation = true) }
    fun dismissDeleteAll() = _uiState.update { if (it.isWorking) it else it.copy(showDeleteConfirmation = false) }
    fun confirmDeleteAll() = runWork(SettingsMessage.Deleted, SettingsMessage.DeleteFailed) { savingsRepository.deleteAllData() }

    fun export(uri: Uri) = runWork(SettingsMessage.Exported, SettingsMessage.ExportFailed) { backupService.exportTo(uri) }
    fun prepareImport(uri: Uri) {
        if (_uiState.value.isWorking) return
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true, message = null) }
            runCatching { backupService.readAndValidate(uri) }
                .onSuccess { snapshot -> _uiState.update { it.copy(isWorking = false, pendingImport = snapshot) } }
                .onFailure { _uiState.update { it.copy(isWorking = false, message = SettingsMessage.ImportInvalid) } }
        }
    }
    fun dismissImport() = _uiState.update { if (it.isWorking) it else it.copy(pendingImport = null) }
    fun confirmImport() = runWork(SettingsMessage.Imported, SettingsMessage.ImportFailed) {
        val snapshot = requireNotNull(_uiState.value.pendingImport)
        savingsRepository.replaceAll(snapshot)
    }
    fun consumeMessage() = _uiState.update { it.copy(message = null) }

    private fun runWork(success: SettingsMessage, failure: SettingsMessage, block: suspend () -> Unit) {
        if (_uiState.value.isWorking) return
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true, showDeleteConfirmation = false, message = null) }
            runCatching { block() }
                .onSuccess { _uiState.update { it.copy(isWorking = false, pendingImport = null, message = success) } }
                .onFailure { _uiState.update { it.copy(isWorking = false, message = failure) } }
        }
    }

    companion object {
        fun factory(settings: SettingsRepository, savings: SavingsRepository, backup: BackupService): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingsViewModel(settings, savings, backup) }
        }
    }
}

internal fun SettingsUiState.withSettings(settings: AppSettings): SettingsUiState = copy(
    defaultCurrencyCode = settings.defaultCurrencyCode,
    hapticFeedbackEnabled = settings.hapticFeedbackEnabled,
    highRefreshRateEnabled = settings.highRefreshRateEnabled,
    appLanguage = settings.appLanguage,
)
