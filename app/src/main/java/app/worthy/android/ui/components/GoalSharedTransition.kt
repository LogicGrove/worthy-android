package app.worthy.android.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.PlaceholderSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

val GoalBoundsTransform = BoundsTransform { _, _ ->
    spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh,
    )
}

fun goalHeroSharedKey(goalId: Long) = "goal-hero-$goalId"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun goalHeroSharedElementModifier(
    goalId: Long,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
): Modifier {
    if (sharedTransitionScope == null || animatedVisibilityScope == null) return Modifier
    return with(sharedTransitionScope) {
        Modifier.sharedElement(
            sharedContentState = rememberSharedContentState(goalHeroSharedKey(goalId)),
            animatedVisibilityScope = animatedVisibilityScope,
            boundsTransform = GoalBoundsTransform,
            placeholderSize = PlaceholderSize.ContentSize,
            renderInOverlayDuringTransition = true,
            zIndexInOverlay = 101f,
        )
    }
}
