package com.mateusrodcosta.apps.lontramusic.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.negativePadding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): Modifier {
    return layout { measurable, constraints ->
        val overriddenWidth = constraints.maxWidth + (start + end).roundToPx()
        val overriddenHeight = constraints.maxHeight + (top + bottom).roundToPx()
        val placeable =
            measurable.measure(
                constraints.copy(maxWidth = overriddenWidth, maxHeight = overriddenHeight)
            )
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(
                ((end - start) / 2).roundToPx(),
                ((bottom - top) / 2).roundToPx(),
            )
        }
    }
}

fun Modifier.negativePadding(all: Dp): Modifier {
    return negativePadding(all, all, all, all)
}

fun Modifier.negativePadding(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): Modifier {
    return negativePadding(horizontal, vertical, horizontal, vertical)
}
