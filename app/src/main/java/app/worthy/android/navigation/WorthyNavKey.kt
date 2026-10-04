package app.worthy.android.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface WorthyNavKey : NavKey

@Serializable
data object HomeKey : WorthyNavKey

@Serializable
data object AddGoalKey : WorthyNavKey

@Serializable
data object SettingsKey : WorthyNavKey

@Serializable
data object ThemeKey : WorthyNavKey

@Serializable
data class GoalDetailKey(val goalId: Long) : WorthyNavKey
