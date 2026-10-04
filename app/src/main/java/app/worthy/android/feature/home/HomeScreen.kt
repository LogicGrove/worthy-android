package app.worthy.android.feature.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.zIndex
import app.worthy.android.R
import app.worthy.android.core.haptics.WorthyHaptics
import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.ui.components.GoalHeroCard
import app.worthy.android.ui.components.goalHeroCardUiModel
import app.worthy.android.ui.components.goalHeroSharedElementModifier
import app.worthy.android.ui.components.worthyPressClickable
import kotlin.math.absoluteValue
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAddGoal: () -> Unit,
    onGoalSelected: (Long) -> Unit,
    onSettings: () -> Unit = {},
    onTheme: () -> Unit = {},
    onSupportWorthy: () -> Unit = {},
    haptics: WorthyHaptics? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    modifier: Modifier = Modifier,
) {
    val topBarChromeModifier = homeChromeModifier(animatedVisibilityScope)
    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopBar(
                modifier = topBarChromeModifier,
                onAddGoal = onAddGoal,
                onSettings = onSettings,
                onTheme = onTheme,
                onSupportWorthy = onSupportWorthy,
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> CenteredMessage(padding) { CircularProgressIndicator() }
            uiState.hasError -> CenteredMessage(padding) { Text(stringResource(R.string.error_loading)) }
            uiState.goals.isEmpty() -> EmptyHome(padding)
            else -> GoalStack(
                goals = uiState.goals,
                padding = padding,
                onGoalSelected = onGoalSelected,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                haptics = haptics,
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    onAddGoal: () -> Unit,
    onSettings: () -> Unit,
    onTheme: () -> Unit,
    onSupportWorthy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .heightIn(min = 64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.home_title),
            modifier = Modifier.padding(horizontal = 72.dp),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp),
        ) {
            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                Crossfade(
                    targetState = menuExpanded,
                    animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing),
                    label = "homeMenuIcon",
                ) { isOpen ->
                    Icon(
                        painter = painterResource(
                            if (isOpen) R.drawable.ic_arrow_back_24 else R.drawable.ic_menu_24,
                        ),
                        contentDescription = stringResource(
                            if (isOpen) R.string.close_menu else R.string.open_menu,
                        ),
                    )
                }
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.widthIn(min = 240.dp, max = 320.dp),
                shape = MaterialTheme.shapes.large,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp,
            ) {
                HomeMenuItem(
                    label = stringResource(R.string.menu_add),
                    iconRes = R.drawable.ic_add_24,
                    onClick = {
                        menuExpanded = false
                        onAddGoal()
                    },
                )
                HomeMenuItem(
                    label = stringResource(R.string.menu_settings),
                    iconRes = R.drawable.ic_settings_24,
                    onClick = {
                        menuExpanded = false
                        onSettings()
                    },
                )
                HomeMenuItem(
                    label = stringResource(R.string.menu_theme),
                    iconRes = R.drawable.ic_palette_24,
                    onClick = {
                        menuExpanded = false
                        onTheme()
                    },
                )
                HomeMenuItem(
                    label = stringResource(R.string.menu_support_worthy),
                    iconRes = R.drawable.ic_favorite_24,
                    onClick = {
                        menuExpanded = false
                        onSupportWorthy()
                    },
                )
            }
        }
    }
}

@Composable
private fun HomeMenuItem(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = label, style = MaterialTheme.typography.bodyLarge) },
        onClick = onClick,
        modifier = Modifier.heightIn(min = 56.dp),
        leadingIcon = {
            Icon(painterResource(iconRes), contentDescription = null)
        },
        trailingIcon = {
            Icon(painterResource(R.drawable.ic_chevron_right_24), contentDescription = null)
        },
    )
}

@Composable
private fun GoalStack(
    goals: List<GoalWithSavings>,
    padding: PaddingValues,
    onGoalSelected: (Long) -> Unit,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    haptics: WorthyHaptics?,
) {
    val contentHeadingModifier = homeChromeModifier(animatedVisibilityScope)
    if (goals.size == 1) {
        val goal = goals.single()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = padding.calculateTopPadding() + 28.dp,
                end = 24.dp,
                bottom = padding.calculateBottomPadding() + 32.dp,
            ),
        ) {
            item { HomeGoalsHeading(contentHeadingModifier) }
            item {
                PrimaryGoalCard(
                    goal = goal,
                    onClick = { onGoalSelected(goal.goal.id) },
                    enabled = true,
                    sharedHeroModifier = goalHeroSharedElementModifier(
                        goalId = goal.goal.id,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    top = padding.calculateTopPadding() + 28.dp,
                    end = 24.dp,
                    bottom = padding.calculateBottomPadding() + 20.dp,
                ),
        ) {
            HomeGoalsHeading(contentHeadingModifier)
            GoalPager(
                goals = goals,
                onGoalSelected = onGoalSelected,
                haptics = haptics,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HomeGoalsHeading(modifier: Modifier) {
    Text(
        text = stringResource(R.string.your_goals),
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier
            .padding(start = 8.dp, bottom = 40.dp)
            .then(modifier)
            .semantics { heading() },
    )
}

@Composable
private fun GoalPager(
    goals: List<GoalWithSavings>,
    onGoalSelected: (Long) -> Unit,
    haptics: WorthyHaptics?,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    modifier: Modifier = Modifier,
) {
    val goalIds = goals.map { it.goal.id }
    var activeGoalId by rememberSaveable { mutableStateOf(goals.first().goal.id) }
    var previousGoalIds by rememberSaveable { mutableStateOf(goalIds.toLongArray()) }
    val initialLogicalIndex = goalIds.indexOf(activeGoalId).coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = centeredVirtualPage(goalIds.size, initialLogicalIndex),
        pageCount = { VIRTUAL_PAGE_COUNT },
    )
    val currentGoals by rememberUpdatedState(goals)

    LaunchedEffect(goalIds) {
        val reconciledId = reconcileActiveGoalId(
            previousGoalIds = previousGoalIds.asList(),
            goalIds = goalIds,
            activeGoalId = activeGoalId,
        ) ?: return@LaunchedEffect
        activeGoalId = reconciledId
        previousGoalIds = goalIds.toLongArray()
        val logicalIndex = goalIds.indexOf(reconciledId)
        pagerState.scrollToPage(centeredVirtualPage(goalIds.size, logicalIndex))
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .drop(1)
            .collect { virtualPage ->
                val latestGoals = currentGoals
                if (latestGoals.isEmpty()) return@collect
                val settledGoalId = latestGoals[logicalGoalIndex(virtualPage, latestGoals.size)].goal.id
                if (settledGoalId != activeGoalId) {
                    activeGoalId = settledGoalId
                    haptics?.cardSettled()
                }
            }
    }

    val peekHeight = 56.dp
    val pageSpacing = 8.dp
    val pageSize = remember(peekHeight) {
        object : PageSize {
            override fun Density.calculateMainAxisPageSize(availableSpace: Int, pageSpacing: Int): Int =
                (availableSpace - peekHeight.roundToPx() - pageSpacing).coerceAtLeast(1)
        }
    }
    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth().clipToBounds(),
        pageSize = pageSize,
        pageSpacing = pageSpacing,
        beyondViewportPageCount = 1,
        flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            pagerSnapDistance = PagerSnapDistance.atMost(1),
        ),
        key = { virtualPage -> virtualPageKey(virtualPage, goals[logicalGoalIndex(virtualPage, goals.size)].goal.id) },
    ) { virtualPage ->
        val goal = goals[logicalGoalIndex(virtualPage, goals.size)]
        val pageOffset = pagerState.getOffsetDistanceInPages(virtualPage)
        val distance = pageOffset.absoluteValue.coerceIn(0f, 1f)
        val isFront = virtualPage == pagerState.settledPage && goal.goal.id == activeGoalId
        val showFullCard = isFront || (pagerState.isScrollInProgress && distance <= 1f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f - distance)
                .graphicsLayer {
                    scaleX = 1f - (distance * 0.02f)
                    scaleY = 1f - (distance * 0.02f)
                    alpha = 1f - (distance * 0.05f)
                },
            contentAlignment = Alignment.TopCenter,
        ) {
            if (showFullCard) {
                PrimaryGoalCard(
                    goal = goal,
                    onClick = { if (isFront) onGoalSelected(goal.goal.id) },
                    enabled = isFront && !pagerState.isScrollInProgress,
                    modifier = Modifier.fillMaxSize().then(if (isFront) Modifier else Modifier.clearAndSetSemantics { }),
                    sharedHeroModifier = if (isFront) {
                        goalHeroSharedElementModifier(
                            goalId = goal.goal.id,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                    } else {
                        Modifier
                    },
                )
            } else {
                GoalCardPeek(
                    title = goal.goal.productName,
                    announceAsNext = pageOffset > 0f,
                )
            }
        }
    }
}

@Composable
private fun GoalCardPeek(title: String, announceAsNext: Boolean) {
    val nextGoalDescription = stringResource(R.string.next_goal, title)
    val semanticsModifier = if (announceAsNext) {
        Modifier.clearAndSetSemantics { contentDescription = nextGoalDescription }
    } else {
        Modifier.clearAndSetSemantics { }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = MaterialTheme.shapes.large,
            )
            .then(semanticsModifier),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )
    }
}

internal const val VIRTUAL_PAGE_COUNT = Int.MAX_VALUE

internal fun logicalGoalIndex(virtualPage: Int, goalCount: Int): Int {
    require(goalCount > 0)
    return Math.floorMod(virtualPage, goalCount)
}

internal fun centeredVirtualPage(goalCount: Int, logicalIndex: Int): Int {
    require(goalCount > 0)
    require(logicalIndex in 0 until goalCount)
    val midpoint = VIRTUAL_PAGE_COUNT / 2
    return midpoint - logicalGoalIndex(midpoint, goalCount) + logicalIndex
}

internal fun virtualPageKey(virtualPage: Int, goalId: Long): String =
    "virtual-$virtualPage-goal-$goalId"

internal fun reconcileActiveGoalId(
    previousGoalIds: List<Long>,
    goalIds: List<Long>,
    activeGoalId: Long?,
): Long? {
    if (goalIds.isEmpty()) return null
    if (activeGoalId != null && activeGoalId in goalIds) return activeGoalId
    val previousIndex = previousGoalIds.indexOf(activeGoalId)
    return goalIds[previousIndex.coerceAtLeast(0).coerceIn(goalIds.indices)]
}

@Composable
private fun homeChromeModifier(animatedVisibilityScope: AnimatedVisibilityScope?): Modifier =
    with(animatedVisibilityScope) {
        if (this == null) Modifier else Modifier.animateEnterExit(
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 220, easing = LinearEasing),
                initialOffsetY = { -it / 3 },
            ) + fadeIn(tween(durationMillis = 220, easing = LinearEasing)),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 180, easing = LinearEasing),
                targetOffsetY = { -it / 3 },
            ) + fadeOut(tween(durationMillis = 180, easing = LinearEasing)),
        )
    }

@Composable
private fun PrimaryGoalCard(
    goal: GoalWithSavings,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    sharedHeroModifier: Modifier,
) {
    GoalHeroCard(
        model = goalHeroCardUiModel(goal),
        modifier = modifier
            .fillMaxWidth()
            .then(sharedHeroModifier)
            .worthyPressClickable(enabled = enabled, role = Role.Button, onClick = onClick),
    )
}

@Composable
private fun EmptyHome(padding: PaddingValues) {
    CenteredMessage(padding) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                stringResource(R.string.empty_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                stringResource(R.string.empty_body),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CenteredMessage(padding: PaddingValues, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}
