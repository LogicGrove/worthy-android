package app.worthy.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.worthy.android.R
import app.worthy.android.core.model.GoalWithSavings
import app.worthy.android.core.model.Money
import androidx.compose.ui.res.stringResource
import kotlin.math.roundToInt

data class GoalHeroCardUiModel(
    val goalId: Long,
    val productName: String,
    val savedText: String,
    val targetText: String,
    val progress: Float,
    val progressText: String,
    val remainingText: String,
)

@Composable
fun goalHeroCardUiModel(goal: GoalWithSavings): GoalHeroCardUiModel {
    val remaining = Money(
        amountMinor = goal.progress.remainingAmountMinor,
        currencyCode = goal.goal.target.currencyCode,
    )
    return GoalHeroCardUiModel(
        goalId = goal.goal.id,
        productName = goal.goal.productName,
        savedText = stringResource(R.string.amount_saved, formatMoney(goal.saved)),
        targetText = stringResource(R.string.target_amount, formatMoney(goal.goal.target)),
        progress = goal.progress.visualProgress,
        progressText = stringResource(
            R.string.goal_progress_compact,
            (goal.progress.visualProgress * 100).roundToInt(),
        ),
        remainingText = stringResource(R.string.amount_to_go, formatMoney(remaining)),
    )
}

@Composable
fun GoalHeroCard(model: GoalHeroCardUiModel, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = model.productName,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .semantics { heading() },
            )
            GoalArtwork(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 190.dp, max = 280.dp)
                    .aspectRatio(1.75f),
            )
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = model.savedText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = model.targetText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    GoalProgressIndicator(
                        progress = model.progress,
                        modifier = Modifier.weight(1f),
                    )
                    Text(text = model.progressText, style = MaterialTheme.typography.titleLarge)
                }
                Text(
                    text = model.remainingText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
