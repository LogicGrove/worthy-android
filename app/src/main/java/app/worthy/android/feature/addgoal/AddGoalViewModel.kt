package app.worthy.android.feature.addgoal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.worthy.android.core.money.MoneyInputResult
import app.worthy.android.core.money.parseMoneyInput
import app.worthy.android.core.validation.isValidProductName
import app.worthy.android.core.validation.isValidProductUrl
import app.worthy.android.core.validation.normalizedCurrencyCodeOrNull
import app.worthy.android.data.repository.SavingsRepository
import app.worthy.android.data.settings.SettingsRepository
import java.util.Currency
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddGoalViewModel(
    private val repository: SavingsRepository,
    private val locale: Locale = Locale.getDefault(),
    private val settingsRepository: SettingsRepository? = null,
) : ViewModel() {
    private val initialCurrency = runCatching { Currency.getInstance(locale).currencyCode }.getOrDefault("EUR")
    private var currencyWasEdited = false
    private val _uiState = MutableStateFlow(AddGoalUiState(currencyCode = initialCurrency))
    val uiState: StateFlow<AddGoalUiState> = _uiState.asStateFlow()

    init {
        settingsRepository?.let { settings ->
            viewModelScope.launch {
                val defaultCurrency = settings.settings.first().defaultCurrencyCode
                _uiState.update { state ->
                    if (!currencyWasEdited) state.copy(currencyCode = defaultCurrency) else state
                }
            }
        }
    }

    fun onProductNameChanged(value: String) = _uiState.update {
        it.copy(productName = value, productNameError = false, saveFailed = false)
    }

    fun onProductUrlChanged(value: String) = _uiState.update {
        it.copy(productUrl = value, productUrlError = false, saveFailed = false)
    }

    fun onTargetPriceChanged(value: String) = _uiState.update {
        it.copy(targetPriceText = value, amountError = null, saveFailed = false)
    }

    fun onCurrencyCodeChanged(value: String) {
        currencyWasEdited = true
        _uiState.update { state ->
            state.copy(
                currencyCode = value.uppercase(Locale.ROOT).take(3),
                currencyError = false,
                amountError = null,
                saveFailed = false,
            )
        }
    }

    fun saveGoal() {
        val state = _uiState.value
        val normalizedCurrency = normalizedCurrencyCodeOrNull(state.currencyCode)
        val amountResult = normalizedCurrency?.let {
            parseMoneyInput(state.targetPriceText, it, locale)
        }
        val validAmount = amountResult as? MoneyInputResult.Valid
        val nameError = !isValidProductName(state.productName)
        val urlError = !isValidProductUrl(state.productUrl)
        val currencyError = normalizedCurrency == null
        if (nameError || urlError || currencyError || validAmount == null) {
            _uiState.update {
                it.copy(
                    productNameError = nameError,
                    productUrlError = urlError,
                    currencyError = currencyError,
                    amountError = if (currencyError) null else amountResult,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveFailed = false) }
            runCatching {
                repository.createGoal(
                    productName = state.productName,
                    productUrl = state.productUrl,
                    targetAmountMinor = validAmount.amountMinor,
                    currencyCode = normalizedCurrency,
                )
            }.onSuccess { id ->
                _uiState.update { it.copy(isSaving = false, savedGoalId = id) }
            }.onFailure {
                _uiState.update { it.copy(isSaving = false, saveFailed = true) }
            }
        }
    }

    fun consumeSavedGoal() = _uiState.update { it.copy(savedGoalId = null) }

    companion object {
        fun factory(
            repository: SavingsRepository,
            settingsRepository: SettingsRepository,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { AddGoalViewModel(repository, settingsRepository = settingsRepository) }
        }
    }
}
