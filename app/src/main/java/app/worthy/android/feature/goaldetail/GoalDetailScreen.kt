package app.worthy.android.feature.goaldetail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.worthy.android.R
import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.feature.addgoal.amountErrorResource
import app.worthy.android.ui.components.GoalHeroCard
import app.worthy.android.ui.components.formatMoney
import app.worthy.android.ui.components.goalHeroCardUiModel
import app.worthy.android.ui.components.goalHeroSharedElementModifier

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GoalDetailScreen(
    goalId: Long,
    uiState: GoalDetailUiState,
    transitionGoal: GoalWithSavings? = null,
    onContributionChanged: (String) -> Unit,
    onAddContribution: () -> Unit,
    onDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    modifier: Modifier = Modifier,
) {
    val displayedGoal = uiState.goal ?: transitionGoal
    val sharedHeroModifier = goalHeroSharedElementModifier(
        goalId = goalId,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
    )
    Scaffold(modifier = modifier) { padding ->
        when {
            uiState.isLoading && displayedGoal == null -> Column(
                Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) { CircularProgressIndicator() }
            uiState.loadFailed -> DetailMessage(stringResource(R.string.error_loading), padding.calculateTopPadding())
            displayedGoal == null -> DetailMessage(stringResource(R.string.goal_not_found), padding.calculateTopPadding())
            else -> GoalContent(
                uiState = uiState,
                goal = displayedGoal,
                onContributionChanged = onContributionChanged,
                onAddContribution = onAddContribution,
                onDelete = onDelete,
                onBack = onBack,
                topPadding = padding.calculateTopPadding(),
                sharedHeroModifier = sharedHeroModifier,
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
    }
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text(stringResource(R.string.delete_goal_title)) },
            text = { Text(stringResource(R.string.delete_goal_body)) },
            confirmButton = {
                TextButton(onClick = onConfirmDelete, enabled = !uiState.isDeleting) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDelete, enabled = !uiState.isDeleting) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun DetailMessage(message: String, topPadding: androidx.compose.ui.unit.Dp) {
    Column(
        Modifier.fillMaxSize().padding(top = topPadding).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { Text(message) }
}

@Composable
private fun GoalContent(
    uiState: GoalDetailUiState,
    goal: GoalWithSavings,
    onContributionChanged: (String) -> Unit,
    onAddContribution: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    topPadding: androidx.compose.ui.unit.Dp,
    sharedHeroModifier: Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope?,
) {
    val topControlsModifier = with(animatedVisibilityScope) {
        if (this == null) Modifier else Modifier.animateEnterExit(
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 220, easing = LinearEasing),
                initialOffsetY = { -it / 2 },
            ) + fadeIn(tween(durationMillis = 180, easing = LinearEasing)),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 180, easing = LinearEasing),
                targetOffsetY = { -it / 2 },
            ) + fadeOut(tween(durationMillis = 160, easing = LinearEasing)),
        )
    }
    val detailContentModifier = with(animatedVisibilityScope) {
        if (this == null) Modifier else Modifier.animateEnterExit(
            enter = fadeIn(tween(durationMillis = 220, easing = LinearEasing)),
            exit = fadeOut(tween(durationMillis = 180, easing = LinearEasing)),
        )
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 24.dp,
            top = topPadding + 12.dp,
            end = 24.dp,
            bottom = 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = topControlsModifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onBack, enabled = !uiState.isDeleting) {
                    Text(stringResource(R.string.back))
                }
                IconButton(onClick = onDelete, enabled = !uiState.isDeleting) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete_24),
                        contentDescription = stringResource(R.string.delete_goal),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            if (uiState.isDeleting) {
                Text(stringResource(R.string.deleting_goal), color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (uiState.deleteFailed) {
                Text(stringResource(R.string.error_delete_goal), color = MaterialTheme.colorScheme.error)
            }
        }
        item {
            GoalHeroCard(
                model = goalHeroCardUiModel(goal),
                modifier = Modifier.fillMaxWidth().then(sharedHeroModifier),
            )
        }
        item {
            Text(
                text = goal.goal.productUrl,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = detailContentModifier,
            )
        }
        item {
            Column(modifier = detailContentModifier) {
                Text(stringResource(R.string.add_contribution_title), style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = uiState.contributionText,
                    onValueChange = onContributionChanged,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    label = { Text(stringResource(R.string.contribution_amount)) },
                    supportingText = uiState.contributionError?.let { error ->
                        { Text(stringResource(amountErrorResource(error))) }
                    },
                    isError = uiState.contributionError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text(goal.goal.target.currencyCode) },
                )
                if (uiState.addFailed) {
                    Text(stringResource(R.string.error_add_contribution), color = MaterialTheme.colorScheme.error)
                }
                Button(
                    onClick = onAddContribution,
                    enabled = !uiState.isAddingContribution,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) {
                    Text(stringResource(if (uiState.isAddingContribution) R.string.adding_contribution else R.string.add_contribution))
                }
            }
        }
        item {
            Column(modifier = detailContentModifier) {
                Text(
                    stringResource(R.string.contribution_history),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { heading() },
                )
                if (uiState.contributions.isEmpty()) {
                    Text(stringResource(R.string.no_contributions), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        items(uiState.contributions, key = { it.id }) { contribution ->
            Surface(
                modifier = detailContentModifier,
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    stringResource(R.string.contribution_item, formatMoney(contribution.amount)),
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                )
            }
        }
    }
}
