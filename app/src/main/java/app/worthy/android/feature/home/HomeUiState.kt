package app.worthy.android.feature.home

import app.worthy.android.core.model.GoalWithSavings

data class HomeUiState(
    val goals: List<GoalWithSavings> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)
