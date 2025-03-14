@file:Suppress("ktlint:standard:filename")

package com.utsman.composeremote

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

@Composable
fun rememberScrollStopDetector(
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

data class ScrollStopDetectorState(
    val isScrolling: Boolean,
    val currentPosition: Int,
    val stoppedPosition: Int,
)
