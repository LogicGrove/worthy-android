package app.worthy.android.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.FrameRateCategory
import androidx.compose.ui.Modifier
import androidx.compose.ui.preferredFrameRate
import androidx.compose.ui.platform.LocalContext
import app.worthy.android.data.settings.AppSettings
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.core.haptics.WorthyHaptics
import app.worthy.android.data.repository.SavingsRepository
import app.worthy.android.data.settings.SettingsRepository
import app.worthy.android.data.backup.BackupService
import app.worthy.android.BuildConfig
import app.worthy.android.feature.addgoal.AddGoalScreen
import app.worthy.android.feature.addgoal.AddGoalViewModel
import app.worthy.android.feature.goaldetail.GoalDetailScreen
import app.worthy.android.feature.goaldetail.GoalDetailViewModel
import app.worthy.android.feature.goaldetail.ContributionHapticEvent
import app.worthy.android.feature.home.HomeScreen
import app.worthy.android.feature.home.HomeViewModel
import app.worthy.android.feature.settings.SettingsScreen
import app.worthy.android.feature.settings.SettingsViewModel
import app.worthy.android.feature.theme.ThemeScreen
import app.worthy.android.feature.theme.ThemeViewModel
import app.worthy.android.ui.theme.isDynamicColorSupported

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WorthyNavigation(
    repository: SavingsRepository,
    settingsRepository: SettingsRepository,
    settings: AppSettings,
    backupService: BackupService,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(HomeKey)
    var transitioningGoal by remember { mutableStateOf<GoalWithSavings?>(null) }
    val hapticsEnabled = rememberUpdatedState(settings.hapticFeedbackEnabled)
    val context = LocalContext.current
    val haptics = remember(context) {
        WorthyHaptics(context.applicationContext) { hapticsEnabled.value }
    }
    val frameRateModifier = if (settings.highRefreshRateEnabled) {
        modifier.preferredFrameRate(FrameRateCategory.High)
    } else {
        modifier
    }

    SharedTransitionLayout(
        modifier = frameRateModifier,
    ) {
        val sharedTransitionScope = this
        NavDisplay(
            backStack = backStack,
            modifier = Modifier,
            sharedTransitionScope = sharedTransitionScope,
            onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
            entry<HomeKey> {
                val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(repository))
                val state = viewModel.uiState.collectAsStateWithLifecycle().value
                HomeScreen(
                    uiState = state,
                    onAddGoal = { backStack.add(AddGoalKey) },
                    onSettings = { backStack.add(SettingsKey) },
                    onTheme = { backStack.add(ThemeKey) },
                    onGoalSelected = { goalId ->
                        transitioningGoal = state.goals.firstOrNull { it.goal.id == goalId }
                        backStack.add(GoalDetailKey(goalId))
                    },
                    haptics = haptics,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                )
            }
            entry<AddGoalKey> {
                val viewModel: AddGoalViewModel = viewModel(factory = AddGoalViewModel.factory(repository, settingsRepository))
                val state = viewModel.uiState.collectAsStateWithLifecycle().value
                LaunchedEffect(state.savedGoalId) {
                    if (state.savedGoalId != null) {
                        haptics.goalCreated()
                        viewModel.consumeSavedGoal()
                        backStack.removeLastOrNull()
                    }
                }
                AddGoalScreen(
                    uiState = state,
                    onProductNameChanged = viewModel::onProductNameChanged,
                    onProductUrlChanged = viewModel::onProductUrlChanged,
                    onTargetPriceChanged = viewModel::onTargetPriceChanged,
                    onCurrencyCodeChanged = viewModel::onCurrencyCodeChanged,
                    onSave = viewModel::saveGoal,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<SettingsKey> {
                val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(settingsRepository, repository, backupService))
                val state = viewModel.uiState.collectAsStateWithLifecycle().value
                SettingsScreen(
                    uiState = state,
                    versionName = BuildConfig.VERSION_NAME,
                    onBack = { backStack.removeLastOrNull() },
                    onDefaultCurrencyChanged = viewModel::setDefaultCurrency,
                    onHapticFeedbackChanged = viewModel::setHapticFeedbackEnabled,
                    onHighRefreshRateChanged = viewModel::setHighRefreshRateEnabled,
                    haptics = haptics,
                    onAppLanguageChanged = viewModel::setAppLanguage,
                    onExport = viewModel::export,
                    onImport = viewModel::prepareImport,
                    onRequestDeleteAll = viewModel::requestDeleteAll,
                    onDismissDeleteAll = viewModel::dismissDeleteAll,
                    onConfirmDeleteAll = viewModel::confirmDeleteAll,
                    onDismissImport = viewModel::dismissImport,
                    onConfirmImport = viewModel::confirmImport,
                    onConsumeMessage = viewModel::consumeMessage,
                )
            }
            entry<ThemeKey> {
                val viewModel: ThemeViewModel = viewModel(factory = ThemeViewModel.factory(settingsRepository))
                val state = viewModel.uiState.collectAsStateWithLifecycle().value
                ThemeScreen(
                    uiState = state,
                    dynamicColorSupported = isDynamicColorSupported(),
                    onBack = { backStack.removeLastOrNull() },
                    onThemeModeChanged = viewModel::setThemeMode,
                    onDynamicColorChanged = viewModel::setDynamicColorEnabled,
                    onColorStyleChanged = viewModel::setColorStyle,
                    haptics = haptics,
                )
            }
            entry<GoalDetailKey>(
                metadata = NavDisplay.transitionSpec {
                    EnterTransition.None togetherWith ExitTransition.None
                } + NavDisplay.popTransitionSpec {
                    EnterTransition.None togetherWith ExitTransition.None
                } + NavDisplay.predictivePopTransitionSpec { _ ->
                    EnterTransition.None togetherWith ExitTransition.None
                },
            ) { key ->
                val viewModel: GoalDetailViewModel = viewModel(
                    factory = GoalDetailViewModel.factory(key.goalId, repository),
                )
                val state = viewModel.uiState.collectAsStateWithLifecycle().value
                LaunchedEffect(state.deletionCompleted) {
                    if (state.deletionCompleted) {
                        haptics.goalDeleted()
                        viewModel.consumeDeletionCompleted()
                        backStack.removeLastOrNull()
                    }
                }
                LaunchedEffect(state.contributionHapticEvent) {
                    val event = state.contributionHapticEvent
                    if (event != null) {
                        when (event) {
                            ContributionHapticEvent.Added -> haptics.contributionAdded()
                            ContributionHapticEvent.GoalCompleted -> haptics.goalCompleted()
                        }
                        viewModel.consumeContributionHapticEvent()
                    }
                }
                GoalDetailScreen(
                    goalId = key.goalId,
                    uiState = state,
                    transitionGoal = transitioningGoal?.takeIf { it.goal.id == key.goalId },
                    onContributionChanged = viewModel::onContributionChanged,
                    onAddContribution = viewModel::addContribution,
                    onDelete = viewModel::requestDeletion,
                    onConfirmDelete = viewModel::confirmDeletion,
                    onDismissDelete = viewModel::dismissDeleteConfirmation,
                    onBack = { backStack.removeLastOrNull() },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                )
            }
            },
        )
    }
}
