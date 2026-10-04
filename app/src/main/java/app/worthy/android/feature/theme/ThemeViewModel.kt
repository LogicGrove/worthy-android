package app.worthy.android.feature.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.worthy.android.data.settings.ColorStyle
import app.worthy.android.data.settings.SettingsRepository
import app.worthy.android.data.settings.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThemeViewModel(private val repository: SettingsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(repository.settings.value.toThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.settings.collect { settings -> _uiState.update { settings.toThemeUiState() } }
        }
    }

    fun setThemeMode(mode: ThemeMode) = updateOptimistically(
        update = { it.copy(themeMode = mode) },
        persist = { repository.setThemeMode(mode) },
    )

    fun setDynamicColorEnabled(enabled: Boolean) = updateOptimistically(
        update = { it.copy(dynamicColorEnabled = enabled) },
        persist = { repository.setDynamicColorEnabled(enabled) },
    )

    fun setColorStyle(style: ColorStyle) = updateOptimistically(
        update = { it.copy(colorStyle = style) },
        persist = { repository.setColorStyle(style) },
    )

    private fun updateOptimistically(
        update: (ThemeUiState) -> ThemeUiState,
        persist: suspend () -> Unit,
    ) = viewModelScope.launch {
        _uiState.update(update)
        runCatching { persist() }.onFailure {
            _uiState.value = repository.settings.value.toThemeUiState()
        }
    }

    companion object {
        fun factory(repository: SettingsRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { ThemeViewModel(repository) }
        }
    }
}

internal fun app.worthy.android.data.settings.AppSettings.toThemeUiState() = ThemeUiState(
    themeMode = themeMode,
    dynamicColorEnabled = dynamicColorEnabled,
    colorStyle = colorStyle,
)
