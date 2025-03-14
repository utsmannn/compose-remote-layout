package com.utsman.composeremote.router

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ConcurrentHashMap<K, V> : Iterable<V> {
    private val mutex = Mutex()
    private val map = mutableMapOf<K, V>()

    suspend fun put(
        key: K,
        value: V,
    ): V? = mutex.withLock {
        map.put(key, value)
    }

    suspend fun get(key: K): V? = mutex.withLock {
        map[key]
    }

    val size get() = map.size

    suspend fun remove(key: K): V? = mutex.withLock {
        map.remove(key)
    }

    fun clear() = map.clear()

    override fun iterator(): Iterator<V> = map.map { it.value }.iterator()
}
