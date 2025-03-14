@file:Suppress("ktlint:standard:filename")

package com.utsman.composeremote

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

@Composable
internal fun rememberScrollStopDetector(
    scrollState: ScrollState,
    debounceTime: Long = 100,
    onScrollStart: (() -> Unit)? = null,
    onScrollStopped: ((position: Int) -> Unit)? = null,
): ScrollStopDetectorState {
    var isScrolling by remember { mutableStateOf(false) }
    var scrollStoppedAt by remember { mutableStateOf(0) }
    var lastScrollChangeTime by remember { mutableStateOf(0L) }

    LaunchedEffect(scrollState) {
        var previousScrolling = false

        while (true) {
            val currentlyScrolling =
                scrollState.isScrollInProgress
            val currentPosition = scrollState.value
            val currentTime =
                Clock.System.now().toEpochMilliseconds()

            if (currentlyScrolling && !previousScrolling) {
                isScrolling = true
                lastScrollChangeTime = currentTime
                onScrollStart?.invoke()
            }

            if (scrollStoppedAt != currentPosition) {
                scrollStoppedAt = currentPosition
                lastScrollChangeTime = currentTime
            }

            if (!currentlyScrolling &&
                isScrolling &&
                (currentTime - lastScrollChangeTime > debounceTime)
            ) {
                isScrolling = false
                onScrollStopped?.invoke(currentPosition)
            }

            previousScrolling = currentlyScrolling
            delay(16)
        }
    }

    return remember(
        isScrolling,
        scrollState.value,
        scrollStoppedAt,
    ) {
        ScrollStopDetectorState(
            isScrolling = isScrolling,
            currentPosition = scrollState.value,
            stoppedPosition = scrollStoppedAt,
        )
    }
}

@Composable
internal fun rememberLazyScrollStopDetector(
    lazyListState: LazyListState,
    debounceTime: Long = 100,
    onScrollStart: (() -> Unit)? = null,
    onScrollStopped: ((index: Int, offset: Int) -> Unit)? = null,
): LazyScrollStopDetectorState {
    var isScrolling by remember { mutableStateOf(false) }
    var stoppedIndex by remember { mutableStateOf(0) }
    var stoppedOffset by remember { mutableStateOf(0) }
    var lastScrollChangeTime by remember { mutableStateOf(0L) }

    LaunchedEffect(lazyListState) {
        var previousScrolling = false

        while (true) {
            val currentlyScrolling =
                lazyListState.isScrollInProgress
            val currentIndex =
                lazyListState.firstVisibleItemIndex
            val currentOffset =
                lazyListState.firstVisibleItemScrollOffset
            val currentTime =
                Clock.System.now().toEpochMilliseconds()

            if (currentlyScrolling && !previousScrolling) {
                isScrolling = true
                lastScrollChangeTime = currentTime
                onScrollStart?.invoke()
            }

            if (stoppedIndex != currentIndex || stoppedOffset != currentOffset) {
                stoppedIndex = currentIndex
                stoppedOffset = currentOffset
                lastScrollChangeTime = currentTime
            }

            if (!currentlyScrolling &&
                isScrolling &&
                (currentTime - lastScrollChangeTime > debounceTime)
            ) {
                isScrolling = false
                onScrollStopped?.invoke(
                    currentIndex,
                    currentOffset,
                )
            }

            previousScrolling = currentlyScrolling
            delay(16)
        }
    }

    return remember(
        isScrolling,
        lazyListState.firstVisibleItemIndex,
        lazyListState.firstVisibleItemScrollOffset,
        stoppedIndex,
        stoppedOffset,
    ) {
        LazyScrollStopDetectorState(
            isScrolling = isScrolling,
            currentIndex = lazyListState.firstVisibleItemIndex,
            currentOffset = lazyListState.firstVisibleItemScrollOffset,
            stoppedIndex = stoppedIndex,
            stoppedOffset = stoppedOffset,
        )
    }
}

internal data class LazyScrollStopDetectorState(
    val isScrolling: Boolean,
    val currentIndex: Int,
    val currentOffset: Int,
    val stoppedIndex: Int,
    val stoppedOffset: Int,
) {
    val isAtStoppedPosition: Boolean
        get() = currentIndex == stoppedIndex && currentOffset == stoppedOffset
}

internal data class ScrollStopDetectorState(
    val isScrolling: Boolean,
    val currentPosition: Int,
    val stoppedPosition: Int,
)
