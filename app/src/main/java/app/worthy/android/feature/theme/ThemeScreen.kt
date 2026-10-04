package app.worthy.android.feature.theme

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.worthy.android.R
import app.worthy.android.core.haptics.WorthyHaptics
import app.worthy.android.data.settings.ColorStyle
import app.worthy.android.data.settings.ThemeMode
import app.worthy.android.ui.components.GoalHeroCard
import app.worthy.android.ui.components.GoalHeroCardUiModel
import app.worthy.android.ui.components.worthyPressClickable

@Composable
fun ThemeScreen(
    uiState: ThemeUiState,
    dynamicColorSupported: Boolean,
    onBack: () -> Unit,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    onColorStyleChanged: (ColorStyle) -> Unit,
    haptics: WorthyHaptics? = null,
) {
    Scaffold(topBar = { ThemeTopBar(onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .navigationBarsPadding().padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            SectionHeading(R.string.theme_appearance)
            AppearanceSelector(uiState.themeMode) { mode ->
                if (mode != uiState.themeMode) { haptics?.cardSettled(); onThemeModeChanged(mode) }
            }
            SectionHeading(R.string.theme_color)
            DynamicColorRow(uiState.dynamicColorEnabled, dynamicColorSupported) { enabled ->
                haptics?.cardSettled(); onDynamicColorChanged(enabled)
            }
            ColorSelector(uiState.colorStyle, enabled = !uiState.dynamicColorEnabled || !dynamicColorSupported) { style ->
                if (style != uiState.colorStyle) { haptics?.cardSettled(); onColorStyleChanged(style) }
            }
            SectionHeading(R.string.theme_preview)
            ThemeGoalPreview()
        }
    }
}

@Composable private fun ThemeTopBar(onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth().statusBarsPadding().heightIn(min = 64.dp), contentAlignment = Alignment.Center) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)) {
            Icon(painterResource(R.drawable.ic_arrow_back_24), stringResource(R.string.back))
        }
        Text(stringResource(R.string.theme_title), style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
    }
}

@Composable private fun SectionHeading(@StringRes text: Int) {
    Text(stringResource(text), style = MaterialTheme.typography.displaySmall, modifier = Modifier.semantics { heading() })
}

private data class AppearanceOption(val mode: ThemeMode, @StringRes val label: Int, @DrawableRes val icon: Int)
private val appearanceOptions = listOf(
    AppearanceOption(ThemeMode.SYSTEM, R.string.theme_system, R.drawable.ic_contrast_24),
    AppearanceOption(ThemeMode.LIGHT, R.string.theme_light, R.drawable.ic_light_mode_24),
    AppearanceOption(ThemeMode.DARK, R.string.theme_dark, R.drawable.ic_dark_mode_24),
)

@Composable private fun AppearanceSelector(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        appearanceOptions.forEachIndexed { index, option ->
            val isSelected = option.mode == selected
            SegmentedButton(
                selected = isSelected, onClick = { onSelect(option.mode) },
                shape = SegmentedButtonDefaults.itemShape(index, appearanceOptions.size),
                icon = { SegmentedButtonDefaults.Icon(isSelected, activeContent = { AppearanceIcon(option.icon) }, inactiveContent = { AppearanceIcon(option.icon) }) },
                label = { Text(stringResource(option.label), maxLines = 1) },
            )
        }
    }
}

@Composable private fun AppearanceIcon(@DrawableRes icon: Int) {
    Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(SegmentedButtonDefaults.IconSize))
}

@Composable private fun DynamicColorRow(checked: Boolean, supported: Boolean, onChange: (Boolean) -> Unit) {
    val supporting = if (supported) R.string.theme_dynamic_color_supporting else R.string.theme_dynamic_color_unavailable
    Row(
        Modifier.fillMaxWidth().heightIn(min = 88.dp).clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .worthyPressClickable(enabled = supported, role = Role.Switch) { onChange(!checked) }
            .padding(horizontal = 24.dp, vertical = 16.dp).semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(stringResource(R.string.theme_dynamic_color), style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(supporting), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked && supported, enabled = supported, onCheckedChange = null)
    }
}

private data class ColorOption(val style: ColorStyle, @StringRes val label: Int)
private val colorOptions = listOf(
    ColorOption(ColorStyle.RED, R.string.theme_red), ColorOption(ColorStyle.BLUE, R.string.theme_blue),
    ColorOption(ColorStyle.GREEN, R.string.theme_green), ColorOption(ColorStyle.ORANGE, R.string.theme_orange),
    ColorOption(ColorStyle.PINK, R.string.theme_pink),
)

@Composable private fun ColorSelector(selected: ColorStyle, enabled: Boolean, onSelect: (ColorStyle) -> Unit) {
    val alpha = animateFloatAsState(
        targetValue = if (enabled) 1f else 0.62f,
        animationSpec = tween(durationMillis = 120),
        label = "customPaletteEnabledAlpha",
    )
    BoxWithConstraints(Modifier.fillMaxWidth().graphicsLayer { this.alpha = alpha.value }) {
        val compact = maxWidth < 420.dp || LocalConfiguration.current.fontScale > 1.15f
        if (compact) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                colorOptions.forEach { option ->
                    FilterChip(selected = option.style == selected, onClick = { onSelect(option.style) },
                        enabled = enabled, label = { Text(stringResource(option.label)) })
                }
            }
        } else {
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                colorOptions.forEachIndexed { index, option ->
                    SegmentedButton(selected = option.style == selected, onClick = { onSelect(option.style) }, enabled = enabled,
                        shape = SegmentedButtonDefaults.itemShape(index, colorOptions.size),
                        label = { Text(stringResource(option.label), maxLines = 1) })
                }
            }
        }
    }
}

@Composable private fun ThemeGoalPreview() {
    val description = stringResource(R.string.theme_preview_description)
    GoalHeroCard(
        model = GoalHeroCardUiModel(
            goalId = -1L, productName = stringResource(R.string.theme_preview_product),
            savedText = stringResource(R.string.theme_preview_saved), targetText = stringResource(R.string.theme_preview_target),
            progress = 0.5f, progressText = stringResource(R.string.goal_progress_compact, 50),
            remainingText = stringResource(R.string.theme_preview_remaining),
        ),
        modifier = Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = description },
    )
}
