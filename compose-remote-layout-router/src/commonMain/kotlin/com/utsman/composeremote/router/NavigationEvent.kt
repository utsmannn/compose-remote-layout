package com.utsman.composeremote.router

sealed class NavigationEvent {
    data class Push(val url: String) : NavigationEvent()

    data class Replace(val url: String) : NavigationEvent()

    data class Home(val url: String) : NavigationEvent()

    data object Pop : NavigationEvent()

    data object Reload : NavigationEvent()
}
