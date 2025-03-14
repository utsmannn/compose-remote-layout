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
}

class CacheScrollOffsetPosition {
    private val map = mutableMapOf<String, Pair<Int, Int>>()

    fun put(
        key: String,
        value: Pair<Int, Int>,
    ): Pair<Int, Int>? = map.put(key, value)

    fun get(
        key: String?,
    ): Pair<Int, Int> = map[key] ?: Pair(0, 0)

    val size get() = map.size

    fun remove(key: String): Pair<Int, Int>? = map.remove(key)

    fun clear() = map.clear()
}

val LocalCacheScrollPosition = compositionLocalOf { CacheScrollPosition() }
val LocalCacheScrollOffsetPosition = compositionLocalOf { CacheScrollOffsetPosition() }
