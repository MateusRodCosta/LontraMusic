package com.mateusrodcosta.apps.lontramusic.ui.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.mutableStateOf

class DefaultPagerState(
    currentPage: Int = 0,
    currentPageOffsetFraction: Float = 0f,
    updatedPageCount: () -> Int,
) : PagerState(currentPage, currentPageOffsetFraction) {
    var pageCountState = mutableStateOf(updatedPageCount)
    override val pageCount: Int
        get() = pageCountState.value.invoke()
}
