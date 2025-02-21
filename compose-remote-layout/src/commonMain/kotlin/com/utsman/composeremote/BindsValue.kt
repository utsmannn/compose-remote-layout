package com.utsman.composeremote

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow

class BindsValue {
    private val valueStates: MutableStateFlow<Map<String, Any>> = MutableStateFlow(emptyMap())

    fun setValue(
        key: String,
        value: Any,
    ) {
        val currentMap = valueStates.value.toMutableMap()
        currentMap[key] = value
        valueStates.value = currentMap
    }

    @Composable
    fun <T> getValue(
        component: LayoutComponent,
        key: String = "",
    ): T? {
        val states by valueStates.collectAsState()
        return when (component) {
            is LayoutComponent.Text -> {
                get(component.content, states) as? T
            }
            is LayoutComponent.Button -> {
                get(component.content, states) as? T
            }
            is LayoutComponent.Custom -> {
                if (key.isEmpty()) return null
                get(key, states) as? T
            }
            else -> {
                null
            }
        }
    }

    operator fun plus(other: BindsValue): BindsValue {
        val currentMap = valueStates.value.toMutableMap()
        val otherMap = other.valueStates.value
        valueStates.value = currentMap + otherMap
        return this
    }

    private fun get(
        rawKey: String? = "",
        states: Map<String, Any>,
    ): String? {
        if (rawKey == null) return null

        val value =
            if (rawKey.startsWith("{") && rawKey.endsWith("}")) {
                val key = rawKey.replace("{", "").replace("}", "")
                val value = states[key]
                value?.toString()
            } else {
                rawKey
            }
        return value
    }
}

val LocalBindsValue = compositionLocalOf { BindsValue() }
