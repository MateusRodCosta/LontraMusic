@file:OptIn(ExperimentalCoroutinesApi::class)

package com.mateusrodcosta.apps.lontramusic.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.stateIn

const val STOP_TIMEOUT = 5000L

/**
 * [kotlinx.coroutines#2514](https://github.com/Kotlin/kotlinx.coroutines/issues/2514#issuecomment-944078630)
 */
inline fun <T, R> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    hot: Boolean = false,
    crossinline transform: (value: T) -> R,
): StateFlow<R> {
    return map { transform(it) }
        .stateIn(
            coroutineScope,
            if (hot) SharingStarted.Eagerly else SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            transform(value),
        )
}

inline fun <T1, T2, R> StateFlow<T1>.combine(
    coroutineScope: CoroutineScope,
    flow: StateFlow<T2>,
    hot: Boolean = false,
    crossinline transform: (a: T1, b: T2) -> R,
): StateFlow<R> {
    return combine(flow) { a, b -> transform(a, b) }
        .stateIn(
            coroutineScope,
            if (hot) SharingStarted.Eagerly else SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            transform(value, flow.value),
        )
}

inline fun <T1, T2, T3, R> StateFlow<T1>.combine(
    coroutineScope: CoroutineScope,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    hot: Boolean = false,
    crossinline transform: (a: T1, b: T2, c: T3) -> R,
): StateFlow<R> {
    return combine(flow2) { a, b -> Pair(a, b) }
        .combine(flow3) { (a, b), c -> transform(a, b, c) }
        .stateIn(
            coroutineScope,
            if (hot) SharingStarted.Eagerly else SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            transform(value, flow2.value, flow3.value),
        )
}

inline fun <T1, T2, T3, T4, R> StateFlow<T1>.combine(
    coroutineScope: CoroutineScope,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    hot: Boolean = false,
    crossinline transform: (a: T1, b: T2, c: T3, d: T4) -> R,
): StateFlow<R> {
    return combine(flow2) { a, b -> Pair(a, b) }
        .combine(flow3) { (a, b), c -> Triple(a, b, c) }
        .combine(flow4) { (a, b, c), d -> transform(a, b, c, d) }
        .stateIn(
            coroutineScope,
            if (hot) SharingStarted.Eagerly else SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            transform(value, flow2.value, flow3.value, flow4.value),
        )
}

fun <T> List<StateFlow<T>>.combine(
    coroutineScope: CoroutineScope,
    hot: Boolean = false,
): StateFlow<List<T>> {
    return if (isEmpty()) {
        MutableStateFlow(emptyList())
    } else {
        drop(1).fold(this[0].map(coroutineScope, hot) { listOf(it) }) { flowA, flowB ->
            flowA.combine(coroutineScope, flowB, hot) { a, b -> a + b }
        }
    }
}

inline fun <T, R> StateFlow<T>.flatMapLatest(
    coroutineScope: CoroutineScope,
    hot: Boolean = false,
    crossinline transform: (value: T) -> StateFlow<R>,
): StateFlow<R> {
    return flatMapLatest { transform(it) }
        .stateIn(
            coroutineScope,
            if (hot) SharingStarted.Eagerly else SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            transform(value).value,
        )
}

fun <T> StateFlow<T>.runningReduce(
    coroutineScope: CoroutineScope,
    hot: Boolean = false,
    operation: (accumulator: T, value: T) -> T,
): StateFlow<T> {
    return runningReduce(operation)
        .stateIn(
            coroutineScope,
            if (hot) SharingStarted.Eagerly else SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            value,
        )
}
