package com.mateusrodcosta.apps.lontramusic.utils

import kotlin.time.Duration

fun <T> Iterable<T>.replace(index: Int, value: T): List<T> {
    return mapIndexed { i, old -> if (index == i) value else old }
}

inline fun <T> Iterable<T>.replace(index: Int, crossinline transform: (T) -> T): List<T> {
    return mapIndexed { i, old -> if (index == i) transform(old) else old }
}

fun <T> Iterable<T>.removeAt(index: Int): List<T> {
    return filterIndexed { i, _ -> i != index }
}

fun <T> List<T>.swap(indexA: Int, indexB: Int): List<T> {
    val a = this[indexA]
    val b = this[indexB]
    return mapIndexed { index, value ->
        when (index) {
            indexA -> b
            indexB -> a
            else -> value
        }
    }
}

inline fun <T, R> Iterable<T>.mode(selector: (T) -> R): R {
    return groupBy { selector(it) }.maxBy { it.value.size }.key
}

fun <T> Iterable<T>.mode(): T {
    return mode { it }
}

inline fun <T, R> Iterable<T>.modeOrNull(selector: (T) -> R): R? {
    return groupBy { selector(it) }.maxByOrNull { it.value.size }?.key
}

fun <T> Iterable<T>.modeOrNull(): T? {
    return modeOrNull { it }
}

inline fun <T, R> Iterable<T>.modeOfNotNullOrNull(selector: (T) -> R): R? {
    return groupBy { selector(it) }.filter { it.key != null }.maxByOrNull { it.value.size }?.key
}

/** Not named `sumOf` because [OverloadResolutionByLambdaReturnType] doesn't work. */
inline fun <T> Iterable<T>.sumOfDuration(transform: (T) -> Duration): Duration {
    return map(transform).fold(Duration.ZERO, Duration::plus)
}
