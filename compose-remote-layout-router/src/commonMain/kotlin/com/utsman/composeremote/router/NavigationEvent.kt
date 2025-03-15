package com.utsman.composeremote.router

sealed class NavigationEvent {
    data class Push(val path: String) : NavigationEvent()

    data class Replace(val path: String) : NavigationEvent()

    data class Home(val path: String) : NavigationEvent()

    data object Pop : NavigationEvent()

    data object Reload : NavigationEvent()
}
