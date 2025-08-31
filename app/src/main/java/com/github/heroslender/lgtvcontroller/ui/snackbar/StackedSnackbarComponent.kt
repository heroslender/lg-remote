package com.github.heroslender.lgtvcontroller.ui.snackbar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
internal fun StackedSnackbar(
    snackbarData: List<StackedSnackbarData>,
    onSnackbarRemoved: (StackedSnackbarData) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        for (data in snackbarData) {
            key(data) {
                println(data.showDuration.toString() + " " + data.showDuration.toMillis())
                if (data.showDuration != StackedSnackbarDuration.Indefinite) {
                    LaunchedEffect(data) {
                        delay(data.showDuration.toMillis())
                        onSnackbarRemoved(data)
                    }
                }

                var offsetX by remember { mutableFloatStateOf(-1f) }
                val draggableModifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            offsetX += delta
                        },
                        onDragStopped = {
                            if (offsetX >= Constant.OFFSET_THRESHOLD_EXIT_RIGHT || offsetX <= Constant.OFFSET_THRESHOLD_EXIT_LEFT) {
                                onSnackbarRemoved(data)
                            } else {
                                offsetX = 0F
                            }
                        },
                    )

                when (data) {
                    is StackedSnackbarData.Custom -> CustomStackedSnackbarItem(
                        data = data,
                        modifier = draggableModifier,
                        onActionClicked = {
                            onSnackbarRemoved(data)
                        },
                    )

                    is StackedSnackbarData.Normal -> NormalStackedSnackbarItem(
                        data = data,
                        modifier = draggableModifier,
                        onActionClicked = {
                            onSnackbarRemoved(data)
                            data.action?.invoke()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomStackedSnackbarItem(
    data: StackedSnackbarData.Custom,
    onActionClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardSnackbarContainer(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            data.content(onActionClicked)
        }
    }
}

@Composable
private fun NormalStackedSnackbarItem(
    data: StackedSnackbarData.Normal,
    onActionClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardSnackbarContainer(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
                    .background(data.type.color.copy(alpha = 0.5f))
                    .padding(8.dp),
            ) {
                Icon(imageVector = data.type.icon, contentDescription = null)
            }

            Column {
                Text(
                    text = data.title,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.Black,
                )
                if (!data.description.isNullOrEmpty()) {
                    Text(
                        text = data.description,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                }
                if (!data.actionTitle.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        Text(
                            text = data.actionTitle,
                            modifier = Modifier.clickable {
                                onActionClicked()
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = data.type.color,
                        )
                    }
                }
            }
        }

        if (data.duration != StackedSnackbarDuration.Indefinite) {
            var currentProgress by remember { mutableFloatStateOf(1f) }
            val progress by animateFloatAsState(
                targetValue = currentProgress,
                animationSpec = tween(
                    durationMillis = data.duration.toMillis().toInt(),
                    easing = LinearEasing
                ),
                label = "progress",
            )

            LaunchedEffect(Unit) {
                // Trigger progressbar annimation
                currentProgress = 0F
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CardSnackbarContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier =
            Modifier
                .padding(bottom = 4.dp)
                .wrapContentHeight()
                .then(modifier),
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        content()
    }
}