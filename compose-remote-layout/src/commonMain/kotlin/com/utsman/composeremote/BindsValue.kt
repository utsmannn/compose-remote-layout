package com.utsman.composeremote

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BindsValue {
    private val _textStates: MutableStateFlow<Map<String, Any>> = MutableStateFlow(emptyMap())
    val textStates: StateFlow<Map<String, Any>> get() = _textStates

    fun setValue(
        key: String,
        value: Any,
    ) {
        val currentMap = textStates.value.toMutableMap()
        currentMap[key] = value
        _textStates.value = currentMap
    }

    companion object {
        fun get(
            component: LayoutComponent.Text,
            states: Map<String, Any>,
        ): String? {
            val text =
                if (component.content.startsWith("{") && component.content.endsWith("}")) {
                    val key = component.content.replace("{", "").replace("}", "")
                    val value = states[key]
                    value?.toString()
                } else {
                    component.content
                }
            return text
        }

        fun get(
            component: LayoutComponent.Button,
            states: Map<String, Any>,
        ): String? {
            if (component.content == null) return null

            val text =
                if (component.content.startsWith("{") && component.content.endsWith("}")) {
                    val key = component.content.replace("{", "").replace("}", "")
                    val value = states[key]
                    value?.toString()
                } else {
                    component.content
                }
            return text
        }
    }
}

val LocalBindsValue = staticCompositionLocalOf<BindsValue> { error("No BindsValue provided") }
