package com.utsman.composeremote.router

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationEventContainer {
    private val navigationEvents: MutableStateFlow<NavigationEvent?> =
        MutableStateFlow(null)

    val event: StateFlow<NavigationEvent?> get() = navigationEvents

    private var baseUrl: String = ""

    internal fun setBaseUrl(url: String) {
        baseUrl = url
    }

    fun push(path: String) {
        navigationEvents.value = NavigationEvent.Push(url(path))
    }

    fun replace(path: String) {
        navigationEvents.value =
            NavigationEvent.Replace(url(path))
    }

    fun home(path: String) {
        navigationEvents.value = NavigationEvent.Home(url(path))
    }

    fun pop() {
        navigationEvents.value = NavigationEvent.Pop
    }

    fun reload() {
        navigationEvents.value = NavigationEvent.Reload
    }

    private fun url(path: String): String = "$baseUrl/${path.removePrefix("/")}"

    suspend fun collectNavigationEvent(block: (NavigationEvent) -> Unit) {
        navigationEvents.collect { event ->
            event?.let { block(it) }
        }
    }
}
