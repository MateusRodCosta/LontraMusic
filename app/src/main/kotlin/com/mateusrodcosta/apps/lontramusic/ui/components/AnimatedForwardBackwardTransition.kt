package com.mateusrodcosta.apps.lontramusic.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.mateusrodcosta.apps.lontramusic.ui.theme.emphasizedEnter
import com.mateusrodcosta.apps.lontramusic.ui.theme.emphasizedExit
import kotlin.math.absoluteValue

const val FORWARD_BACKWARD_SLIDE_FRACTION = 0.5f

@Stable
fun forwardBackwardTransitionAlpha(position: Float, index: Int): Float {
    return (1 - (position - index).absoluteValue * 2).coerceAtLeast(0f)
}

@Stable
fun forwardBackwardTransitionTranslation(
    position: Float,
    index: Int,
    width: Float,
    ltr: Boolean,
): Float {
    return (index - position).coerceIn(-1f, 1f) *
        width *
        FORWARD_BACKWARD_SLIDE_FRACTION *
        (if (ltr) 1 else -1)
}

@Composable
inline fun <T> AnimatedForwardBackwardTransition(
    stack: List<T>,
    modifier: Modifier = Modifier,
    slide: Boolean = true,
    keepRoot: Boolean = true,
    crossinline content: @Composable (T?) -> Unit,
) {
    val position = remember { Animatable(0f) }
    var lastAndCurrentStack by remember { mutableStateOf(stack to stack) }
    var largerStack by remember { mutableStateOf(stack) }

    LaunchedEffect(stack) {
        lastAndCurrentStack = lastAndCurrentStack.second to stack
        largerStack =
            if (lastAndCurrentStack.first.size > lastAndCurrentStack.second.size)
                lastAndCurrentStack.first
            else lastAndCurrentStack.second
    }

    LaunchedEffect(lastAndCurrentStack.second.size) {
        val size = lastAndCurrentStack.second.size
        if (position.value < size) {
            position.animateTo(size.toFloat(), emphasizedEnter())
        } else {
            position.animateTo(size.toFloat(), emphasizedExit())
        }
    }

    Box(modifier = modifier) {
        val rootAlpha = forwardBackwardTransitionAlpha(position.value, 0)
        val ltr = LocalLayoutDirection.current == LayoutDirection.Ltr
        if (keepRoot || rootAlpha > 0) {
            Box(
                modifier =
                    Modifier.graphicsLayer {
                        alpha = rootAlpha
                        if (slide) {
                            translationX =
                                forwardBackwardTransitionTranslation(
                                    position.value,
                                    0,
                                    size.width,
                                    ltr,
                                )
                        }
                    }
            ) {
                content(null)
            }
        } else {
            Box {}
        }

        largerStack.forEachIndexed { index, item ->
            val alpha = forwardBackwardTransitionAlpha(position.value, index + 1)
            key(index + 1) {
                if (alpha > 0) {
                    Box(
                        modifier =
                            Modifier.graphicsLayer {
                                this.alpha = alpha

                                if (slide) {
                                    translationX =
                                        forwardBackwardTransitionTranslation(
                                            position.value,
                                            index + 1,
                                            size.width,
                                            ltr,
                                        )
                                }
                            }
                    ) {
                        content(item)
                    }
                }
            }
        }
    }
}
