package com.utsman.composeremote.router

import com.utsman.composeremote.BindsValue
import com.utsman.composeremote.LayoutComponent

sealed class RenderEvent {
    data class RenderedLayout(
        val component: LayoutComponent,
        val path: String,
        val bindsValue: BindsValue,
        val clickEvent: (String) -> Unit,
    ) : RenderEvent()

    data class Loading(val path: String) : RenderEvent()

    data class Failure(
        val error: Throwable,
        val path: String,
    ) : RenderEvent()
}
