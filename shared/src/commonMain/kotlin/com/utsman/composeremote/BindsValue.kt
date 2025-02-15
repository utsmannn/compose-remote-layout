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
}

val LocalBindsValue = staticCompositionLocalOf<BindsValue> { error("No BindsValue provided") }
