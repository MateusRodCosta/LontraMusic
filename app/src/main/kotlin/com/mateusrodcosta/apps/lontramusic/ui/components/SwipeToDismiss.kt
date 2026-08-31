package com.mateusrodcosta.apps.lontramusic.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import com.mateusrodcosta.apps.lontramusic.ui.theme.emphasizedExit
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Yes, [androidx.compose.material3.SwipeToDismissBox] is yet another Google's useless s***. */
@Composable
inline fun <T> SwipeToDismiss(
    key: T,
    enabled: Boolean,
    swipeThreshold: Dp,
    crossinline onDismiss: (T) -> Unit,
    crossinline content: @Composable BoxScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val dispatcher = Dispatchers.Main.limitedParallelism(1)
    val updatedKey by rememberUpdatedState(key)
    val updatedSwipeThreshold by rememberUpdatedState(swipeThreshold)
    val offset = remember { Animatable(0f) }

    Box(
        modifier =
            if (enabled) {
                    Modifier.pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {},
                            onDragCancel = {
                                coroutineScope.launch(dispatcher) {
                                    offset.animateTo(0f, emphasizedExit())
                                }
                            },
                            onDragEnd = {
                                val positionalThreshold =
                                    updatedSwipeThreshold.roundToPx().coerceAtMost(size.width / 2)
                                val value = offset.value
                                if (value.absoluteValue >= positionalThreshold) {
                                    coroutineScope.launch(dispatcher) {
                                        offset.animateTo(value.sign * size.width, emphasizedExit())
                                        onDismiss(updatedKey)
                                    }
                                } else {
                                    coroutineScope.launch(dispatcher) {
                                        offset.animateTo(0f, emphasizedExit())
                                    }
                                }
                            },
                        ) { change, dragAmount ->
                            coroutineScope.launch(dispatcher) {
                                offset.snapTo(offset.value + dragAmount)
                            }
                        }
                    }
                } else {
                    Modifier
                }
                .graphicsLayer {
                    translationX = offset.value
                    alpha =
                        (1 - (offset.value / size.width).absoluteValue)
                            .takeIf { it.isFinite() }
                            ?.coerceIn(0f, 1f) ?: 1f
                }
    ) {
        content()
    }
}
