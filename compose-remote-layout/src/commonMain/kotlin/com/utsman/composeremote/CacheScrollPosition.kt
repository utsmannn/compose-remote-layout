package com.utsman.composeremote

import androidx.compose.runtime.compositionLocalOf

class CacheScrollPosition {
    private val map = mutableMapOf<String, Int>()

    fun put(
        key: String,
        value: Int,
    ): Int? = map.put(key, value)

    fun get(
        key: String?,
    ): Int = map[key] ?: 0

    val size get() = map.size

    fun remove(key: String): Int? = map.remove(key)

    fun clear() = map.clear()

    operator fun plus(other: CacheScrollPosition): CacheScrollPosition {
        val otherMap = other.map
        map.putAll(otherMap)
        return this
    }
}

val LocalCacheScrollPosition = compositionLocalOf { CacheScrollPosition() }
