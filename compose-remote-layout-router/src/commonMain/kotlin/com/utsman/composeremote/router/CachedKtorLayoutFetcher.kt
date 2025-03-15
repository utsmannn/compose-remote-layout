package com.utsman.composeremote.router

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

class CachedKtorLayoutFetcher(
    private val delegate: LayoutFetcher,
    private val maxCacheSize: Int = 100,
    private val cacheTtlMillis: Long = 15.minutes.inWholeMilliseconds,
) : LayoutFetcher {
    private val cache =
        ConcurrentHashMap<String, CacheEntry>()

    private val cacheMutex = Mutex()

    override suspend fun fetchLayout(url: String): Result<String> {
        val cachedEntry = cache.get(url)
        if (cachedEntry != null && !isExpired(cachedEntry)) {
            return Result.success(cachedEntry.layout)
        }

        val result = delegate.fetchLayout(url)

        if (result.isSuccess) {
            val layout = result.getOrThrow()
            cacheMutex.withLock {
                if (cache.size >= maxCacheSize) {
                    val oldestKey = findOldestEntry()
                    oldestKey?.let { cache.remove(it) }
                }

                cache.put(
                    url,
                    CacheEntry(
                        layout,
                        Clock.System.now()
                            .toEpochMilliseconds(),
                    ),
                )
            }
        }

        return result
    }

    override fun fetchLayoutAsFlow(url: String): Flow<ResultLayout<String>> = flow {
        emit(ResultLayout.Loading)

        try {
            val cachedEntry = cache.get(url)
            if (cachedEntry != null &&
                !isExpired(
                    cachedEntry,
                )
            ) {
                emit(ResultLayout.success(cachedEntry.layout))
                return@flow
            }

            delegate.fetchLayoutAsFlow(url)
                .collect { result ->
                    if (result is ResultLayout.Success) {
                        cacheMutex.withLock {
                            if (cache.size >= maxCacheSize) {
                                val oldestKey =
                                    findOldestEntry()
                                oldestKey?.let {
                                    cache.remove(
                                        it,
                                    )
                                }
                            }

                            cache.put(
                                url,
                                CacheEntry(
                                    result.data,
                                    Clock.System.now()
                                        .toEpochMilliseconds(),
                                ),
                            )
                        }
                    }

                    emit(result)
                }
        } catch (e: Exception) {
            emit(ResultLayout.failure(e))
        }
    }

    suspend fun clearCache() {
        cacheMutex.withLock {
            cache.clear()
        }
    }

    suspend fun invalidateCache(url: String) {
        cacheMutex.withLock {
            cache.remove(url)
        }
    }

    private fun isExpired(entry: CacheEntry): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return now - entry.timestamp > cacheTtlMillis
    }

    private fun findOldestEntry(): String? {
        var oldestKey: String? = null
        var oldestTimestamp = Long.MAX_VALUE

        for ((key, entry) in cache) {
            if (entry < oldestTimestamp) {
                oldestTimestamp = entry
                oldestKey = key
            }
        }

        return oldestKey
    }

    private data class CacheEntry(
        val layout: String,
        val timestamp: Long,
    )
}

fun KtorHttpLayoutFetcher.cached(
    maxCacheSize: Int = 100,
    cacheTtlMillis: Long = 15.minutes.inWholeMilliseconds,
): CachedKtorLayoutFetcher = CachedKtorLayoutFetcher(
    this,
    maxCacheSize,
    cacheTtlMillis,
)
