package app.worthy.android.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role

private const val PressedScale = 0.985f

@Composable
fun Modifier.worthyPressClickable(
    enabled: Boolean = true,
    role: Role? = Role.Button,
    onClick: () -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (enabled && isPressed) PressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "worthyPressScale",
    )
    return clickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        enabled = enabled,
        role = role,
        onClick = onClick,
    ).graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}
