package app.worthy.android.feature.addgoal

import app.worthy.android.core.money.MoneyInputResult

data class AddGoalUiState(
    val productName: String = "",
    val productUrl: String = "",
    val targetPriceText: String = "",
    val currencyCode: String = "",
    val productNameError: Boolean = false,
    val productUrlError: Boolean = false,
    val currencyError: Boolean = false,
    val amountError: MoneyInputResult? = null,
    val isSaving: Boolean = false,
    val savedGoalId: Long? = null,
    val saveFailed: Boolean = false,
)
