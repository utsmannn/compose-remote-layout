package com.utsman.composeremote

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utsman.composeremote.app.modifier.applyJsonModifier

class DynamicLayoutRenderer {
    companion object {
        private val nodeCache = mutableMapOf<String, LayoutNode>()

        fun saveNode(path: String, node: LayoutNode) {
            nodeCache[path] = node
        }

        fun getLastValidNode(path: String): LayoutNode? {
            return nodeCache[path]
        }

        fun clearCache() {
            nodeCache.clear()
        }
    }
}

val defaultNode = LayoutNode(
    type = "column",
    children = listOf(
        LayoutNode(
            type = "button",
            text = "Hello, Compose!",
            clickId = "default_button"
        ),
        LayoutNode(
            type = "text",
            text = "Hello, Compose!"
        )
    )
)

@Composable
fun DynamicLayout(
    node: LayoutNode?,
    modifier: Modifier = Modifier,
    path: String = "root",
    parentScrollable: Boolean = false,
    onClickHandler: (String) -> Unit = {}
) {
    val nodeToRender = node ?: DynamicLayoutRenderer.getLastValidNode(path) ?: defaultNode

    if (node != null) {
        DynamicLayoutRenderer.saveNode(path, node)
    }

    DisposableEffect(Unit) {
        onDispose {
            DynamicLayoutRenderer.clearCache()
        }
    }

    val currentModifier = applyJsonModifier(modifier, nodeToRender.scopedModifier)

    when (nodeToRender.type.lowercase()) {
        "column" -> RenderColumn(nodeToRender, currentModifier, path, parentScrollable, onClickHandler)
        "row" -> RenderRow(nodeToRender, currentModifier, path, parentScrollable, onClickHandler)
        "box" -> RenderBox(nodeToRender, currentModifier, path, parentScrollable, onClickHandler)
        "text" -> RenderText(nodeToRender, currentModifier)
        "button" -> RenderButton(nodeToRender, currentModifier, path, parentScrollable, onClickHandler)
        "card" -> RenderCard(nodeToRender, currentModifier, path, parentScrollable, onClickHandler)
        else -> Text(
            text = "Unsupported component: ${nodeToRender.type}",
            modifier = currentModifier
        )
    }
}

@Composable
private fun RenderColumn(
    node: LayoutNode,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit
) {
    val scopedMod = node.modifier as? ScopedModifier.Column
    val isScrollable = scopedMod?.base?.scrollable == true && !parentScrollable

    val columnModifier = if (isScrollable) {
        if (scopedMod?.base?.height != null) {
            modifier.height(scopedMod.base.height.dp).verticalScroll(rememberScrollState())
        } else {
            modifier.fillMaxHeight().verticalScroll(rememberScrollState())
        }
    } else {
        modifier
    }

    // Get arrangement and alignment from scoped modifier
    val verticalArrangement = scopedMod?.verticalArrangement?.let { arrangement ->
        when (arrangement.lowercase()) {
            "top" -> Arrangement.Top
            "bottom" -> Arrangement.Bottom
            "center" -> Arrangement.Center
            "spacebetween" -> Arrangement.SpaceBetween
            "spacearound" -> Arrangement.SpaceAround
            "spaceevenly" -> Arrangement.SpaceEvenly
            else -> Arrangement.Top
        }
    } ?: Arrangement.Top

    val horizontalAlignment = scopedMod?.horizontalAlignment?.let { alignment ->
        when (alignment.lowercase()) {
            "start" -> Alignment.Start
            "end" -> Alignment.End
            "center" -> Alignment.CenterHorizontally
            else -> Alignment.Start
        }
    } ?: Alignment.Start

    Column(
        modifier = columnModifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        node.children?.forEachIndexed { index, child ->
            DynamicLayout(
                child,
                modifier = Modifier,
                path = "$path-column-$index",
                parentScrollable = isScrollable,
                onClickHandler = onClickHandler
            )
        }
    }
}

@Composable
private fun RenderRow(
    node: LayoutNode,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit
) {
    val scopedMod = node.modifier as? ScopedModifier.Row
    val isScrollable = scopedMod?.base?.scrollable == true && !parentScrollable

    val rowModifier = if (isScrollable) {
        if (scopedMod?.base?.width != null) {
            modifier.width(scopedMod.base.width.dp).horizontalScroll(rememberScrollState())
        } else {
            modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
        }
    } else {
        modifier
    }

    // Get arrangement and alignment from scoped modifier
    val horizontalArrangement = scopedMod?.horizontalArrangement?.let { arrangement ->
        when (arrangement.lowercase()) {
            "start" -> Arrangement.Start
            "end" -> Arrangement.End
            "center" -> Arrangement.Center
            "spacebetween" -> Arrangement.SpaceBetween
            "spacearound" -> Arrangement.SpaceAround
            "spaceevenly" -> Arrangement.SpaceEvenly
            else -> Arrangement.Start
        }
    } ?: Arrangement.Start

    val verticalAlignment = scopedMod?.verticalAlignment?.let { alignment ->
        when (alignment.lowercase()) {
            "top" -> Alignment.Top
            "bottom" -> Alignment.Bottom
            "center" -> Alignment.CenterVertically
            else -> Alignment.Top
        }
    } ?: Alignment.Top

    Row(
        modifier = rowModifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        node.children?.forEachIndexed { index, child ->
            DynamicLayout(
                child,
                modifier = Modifier,
                path = "$path-row-$index",
                parentScrollable = isScrollable,
                onClickHandler = onClickHandler
            )
        }
    }
}

@Composable
private fun RenderBox(
    node: LayoutNode,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit
) {
    val scopedMod = node.modifier as? ScopedModifier.Box

    // Get content alignment from scoped modifier
    val contentAlignment = scopedMod?.contentAlignment?.let { alignment ->
        when (alignment.lowercase()) {
            "center" -> Alignment.Center
            "topstart" -> Alignment.TopStart
            "topcenter" -> Alignment.TopCenter
            "topend" -> Alignment.TopEnd
            "centerstart" -> Alignment.CenterStart
            "centerend" -> Alignment.CenterEnd
            "bottomstart" -> Alignment.BottomStart
            "bottomcenter" -> Alignment.BottomCenter
            "bottomend" -> Alignment.BottomEnd
            else -> Alignment.Center
        }
    } ?: Alignment.Center

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        node.children?.forEachIndexed { index, child ->
            DynamicLayout(
                child,
                modifier = Modifier,
                path = "$path-box-$index",
                parentScrollable = parentScrollable,
                onClickHandler = onClickHandler
            )
        }
    }
}

@Composable
private fun RenderText(
    node: LayoutNode,
    modifier: Modifier
) {
    Text(
        text = node.text ?: "",
        modifier = modifier
    )
}

@Composable
private fun RenderButton(
    node: LayoutNode,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit
) {
    Button(
        modifier = modifier,
        onClick = {
            node.clickId?.let { onClickHandler(it) }
        }
    ) {
        if (node.text != null) {
            Text(text = node.text)
        } else {
            node.children?.forEachIndexed { index, child ->
                DynamicLayout(
                    child,
                    modifier = Modifier,
                    path = "$path-button-$index",
                    parentScrollable = parentScrollable,
                    onClickHandler = onClickHandler
                )
            }
        }
    }
}

@Composable
private fun RenderCard(
    node: LayoutNode,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 4.dp
    ) {
        node.children?.forEachIndexed { index, child ->
            DynamicLayout(
                child,
                modifier = Modifier,
                path = "$path-card-$index",
                parentScrollable = parentScrollable,
                onClickHandler = onClickHandler
            )
        }
    }
}