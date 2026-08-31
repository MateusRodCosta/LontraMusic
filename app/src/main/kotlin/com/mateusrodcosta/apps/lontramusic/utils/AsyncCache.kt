package com.mateusrodcosta.apps.lontramusic.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** This cache is only designed to have a very small size, due to lookup having O(n) complexity. */
class AsyncCache<TKey : Any, TValue : Any>(
    private val coroutineScope: CoroutineScope,
    val size: Int,
) {
    private val mutex = Mutex()
    private val evictionJob = AtomicReference(null as Job?)
    private val creationJobs = ConcurrentHashMap<TKey, Deferred<TValue>>()
    private val entries = mutableListOf<Pair<TKey, TValue>>()

    val actualSize
        get() = entries.size

    suspend fun getOrPut(key: TKey, create: (TKey) -> TValue): TValue {
        val existing =
            mutex.withLock {
                entries
                    .indexOfFirst { it.first == key }
                    .takeIf { it >= 0 }
                    ?.let { entries.removeAt(it) }
                    ?.apply { entries += this }
                    ?.second
            }
        if (existing != null) {
            return existing
        }

        val newCreationJob =
            coroutineScope.async(start = CoroutineStart.LAZY) {
                withContext(Dispatchers.IO) {
                    val value = create(key)
                    mutex.withLock {
                        entries.removeIf { it.first == key }
                        entries += key to value
                    }
                    creationJobs.remove(key)
                    value
                }
            }
        val creationJob = creationJobs.getOrPut(key) { newCreationJob }
        if (creationJob != newCreationJob) {
            newCreationJob.cancel()
        }

        val newEvitionJob = getEvictionJob()
        if (evictionJob.compareAndSet(null, newEvitionJob)) {
            newEvitionJob.start()
        } else {
            newEvitionJob.cancel()
        }

        return creationJob.await()
    }

    fun get(key: TKey): TValue? {
        return runBlocking {
            mutex.withLock {
                entries
                    .indexOfFirst { it.first == key }
                    .takeIf { it >= 0 }
                    ?.let { entries.removeAt(it) }
                    ?.apply { entries += this }
                    ?.second
            }
        }
    }

    private fun getEvictionJob(): Job {
        return coroutineScope.launch(start = CoroutineStart.LAZY) {
            withContext(Dispatchers.IO) {
                mutex.withLock {
                    if (entries.size > size) {
                        entries.subList(0, entries.size - size).clear()
                    }

                    evictionJob.set(null)
                }
            }
        }
    }
}
