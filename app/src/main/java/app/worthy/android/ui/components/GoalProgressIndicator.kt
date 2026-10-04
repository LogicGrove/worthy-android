package app.worthy.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun GoalProgressIndicator(progress: Float, modifier: Modifier = Modifier) {
    val clamped = progress.coerceIn(0f, 1f)
    LinearProgressIndicator(
        progress = { clamped },
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(clamped, 0f..1f) },
    )
}
