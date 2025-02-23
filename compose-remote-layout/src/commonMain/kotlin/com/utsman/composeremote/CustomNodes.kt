package com.utsman.composeremote

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object CustomNodes {
    data class NodeParam(
        val data: Map<String, String>,
        val modifier: Modifier,
        val children: List<ComponentWrapper>?,
        val path: String,
        val parentScrollable: Boolean,
        val onClickHandler: (String) -> Unit,
        val bindsValue: BindsValue,
    )

    private val nodes = mutableMapOf<String, @Composable (NodeParam) -> Unit>()

    fun register(
        type: String,
        node: @Composable (NodeParam) -> Unit,
    ) {
        println("cuaks..... register node...")
        nodes[type.lowercase()] = node
    }

    fun get(type: String): (@Composable (NodeParam) -> Unit)? = nodes[type.lowercase()]

    fun exists(type: String): Boolean = nodes.containsKey(type.lowercase())

    fun clear() {
        nodes.clear()
    }
}
