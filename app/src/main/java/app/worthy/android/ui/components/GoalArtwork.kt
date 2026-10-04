package app.worthy.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

@Composable
fun GoalArtwork(modifier: Modifier = Modifier) {
    val shapeColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.18f)
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(132.dp)) {
            drawCircle(
                color = shapeColor,
                radius = size.minDimension * .19f,
                center = Offset(size.width * .28f, size.height * .67f),
            )
            rotate(4f) {
                drawRoundRect(
                    color = shapeColor,
                    topLeft = Offset(size.width * .53f, size.height * .5f),
                    size = Size(size.width * .38f, size.height * .38f),
                    cornerRadius = CornerRadius(size.width * .08f),
                )
            }
            rotate(45f, pivot = Offset(size.width * .5f, size.height * .25f)) {
                drawRoundRect(
                    color = shapeColor,
                    topLeft = Offset(size.width * .35f, size.height * .1f),
                    size = Size(size.width * .3f, size.height * .3f),
                    cornerRadius = CornerRadius(size.width * .08f),
                )
            }
        }
    }
}
