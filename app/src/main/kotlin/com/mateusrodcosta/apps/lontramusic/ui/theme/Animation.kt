package com.mateusrodcosta.apps.lontramusic.ui.theme

import android.view.animation.PathInterpolator
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.core.graphics.PathParser

const val ENTER_DURATION = 500
const val EXIT_DURATION = 200
const val STANDARD_DURATION = 300

fun <T> emphasized(durationMillis: Int, delayMillis: Int = 0): TweenSpec<T> {
    return tween(
        durationMillis = durationMillis,
        delayMillis = delayMillis,
        easing = EmphasizedEasing(),
    )
}

fun <T> emphasizedEnter(delayMillis: Int = 0): TweenSpec<T> {
    return emphasized(ENTER_DURATION, delayMillis)
}

fun <T> emphasizedExit(delayMillis: Int = 0): TweenSpec<T> {
    return emphasized(EXIT_DURATION, delayMillis)
}

fun <T> emphasizedStandard(delayMillis: Int = 0): TweenSpec<T> {
    return emphasized(STANDARD_DURATION, delayMillis)
}

// region AnimatedVisibility

val EnterFromTop =
    fadeIn(emphasizedEnter()) + expandVertically(emphasizedEnter(), expandFrom = Alignment.Top)

val EnterFromBottom =
    fadeIn(emphasizedEnter()) + expandVertically(emphasizedEnter(), expandFrom = Alignment.Bottom)

val ExitToTop =
    shrinkVertically(emphasizedExit(), shrinkTowards = Alignment.Top) + fadeOut(emphasizedExit())

val ExitToBottom =
    shrinkVertically(emphasizedExit(), shrinkTowards = Alignment.Bottom) + fadeOut(emphasizedExit())

val AnimatedContentEnter =
    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))

val AnimatedContentExit = fadeOut(animationSpec = tween(90))

// endregion

@Immutable
class EmphasizedEasing : Easing {
    override fun transform(fraction: Float): Float {
        return emphasizedInterpolator.getInterpolation(fraction)
    }

    override fun equals(other: Any?): Boolean {
        return other is EmphasizedEasing
    }

    override fun hashCode(): Int {
        return 0
    }
}

private val emphasizedInterpolator =
    PathInterpolator(
        PathParser.createPathFromPathData(
            "M 0,0 C 0.05, 0, 0.133333, 0.06, 0.166666, 0.4 C 0.208333, 0.82, 0.25, 1, 1, 1"
        )
    )
