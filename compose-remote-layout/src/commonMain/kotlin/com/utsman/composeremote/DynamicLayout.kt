package com.utsman.composeremote

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utsman.composeremote.LayoutParser.parseLayoutJson

val defaultComponent =
    ComponentWrapper(
        column =
            LayoutComponent.Column(
                children =
                    listOf(
                        ComponentWrapper(
                            button =
                                LayoutComponent.Button(
                                    content = "Hello, Compose!",
                                    clickId = "default_button",
                                ),
                        ),
                        ComponentWrapper(
                            text =
                                LayoutComponent.Text(
                                    content = "Hello, Compose!",
                                ),
                        ),
                    ),
            ),
    )

@Composable
fun DynamicLayout(
    component: LayoutComponent?,
    modifier: Modifier = Modifier,
    path: String = "root",
    parentScrollable: Boolean = false,
    bindValue: BindsValue = BindsValue(),
    onClickHandler: (String) -> Unit = {},
) {
    val currentBindsValue = LocalBindsValue.current

    CompositionLocalProvider(
        LocalBindsValue provides bindValue + currentBindsValue,
    ) {
        ChildDynamicLayout(
            component,
            modifier,
            path,
            parentScrollable,
            onClickHandler,
        )
    }
}

@Composable
private fun ChildDynamicLayout(
    component: LayoutComponent?,
    modifier: Modifier = Modifier,
    path: String = "root",
    parentScrollable: Boolean = false,
    onClickHandler: (String) -> Unit = {},
) {
    val componentToRender =
        component
            ?: DynamicLayoutRenderer.getLastValidComponent(path)
            ?: defaultComponent.component

    if (component != null) {
        DynamicLayoutRenderer.saveComponent(path, component)
    }

    DisposableEffect(Unit) {
        onDispose {
            DynamicLayoutRenderer.clearCache()
        }
    }

    val currentModifier = applyJsonModifier(modifier, componentToRender.scopedModifier)

    when (componentToRender) {
        is LayoutComponent.Column ->
            RenderColumn(
                componentToRender,
                currentModifier,
                path,
                parentScrollable,
                onClickHandler,
            )

        is LayoutComponent.Row ->
            RenderRow(
                componentToRender,
                currentModifier,
                path,
                parentScrollable,
                onClickHandler,
            )

        is LayoutComponent.Box ->
            RenderBox(
                componentToRender,
                currentModifier,
                path,
                parentScrollable,
                onClickHandler,
            )

        is LayoutComponent.Text -> RenderText(componentToRender, currentModifier)

        is LayoutComponent.Button ->
            RenderButton(
                componentToRender,
                currentModifier,
                path,
                parentScrollable,
                onClickHandler,
            )

        is LayoutComponent.Card ->
            RenderCard(
                componentToRender,
                currentModifier,
                path,
                parentScrollable,
                onClickHandler,
            )

        is LayoutComponent.Spacer -> RenderSpacer(componentToRender)

        is LayoutComponent.Custom ->
            RenderCustomNode(
                componentToRender,
                currentModifier,
                path,
                parentScrollable,
                onClickHandler,
            )
    }
}

@Composable
private fun RenderColumn(
    component: LayoutComponent.Column,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit,
) {
    val scopedMod = component.scopedModifier as? ScopedModifier.Column
    val isScrollable = scopedMod?.base?.scrollable == true && !parentScrollable

    val columnModifier =
        if (isScrollable) {
            if (scopedMod?.base?.height != null) {
                modifier.height(scopedMod.base.height.dp).verticalScroll(rememberScrollState())
            } else {
                modifier.fillMaxHeight().verticalScroll(rememberScrollState())
            }
        } else {
            modifier
        }

    val verticalArrangement =
        scopedMod?.verticalArrangement?.let { arrangement ->
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

    val horizontalAlignment =
        scopedMod?.horizontalAlignment?.let { alignment ->
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
        horizontalAlignment = horizontalAlignment,
    ) {
        component.children?.forEachIndexed { index, wrapper ->
            ChildDynamicLayout(
                wrapper.component,
                modifier = Modifier,
                path = "$path-column-$index",
                parentScrollable = isScrollable,
                onClickHandler = onClickHandler,
            )
        }
    }
}

@Composable
private fun RenderRow(
    component: LayoutComponent.Row,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit,
) {
    val scopedMod = component.scopedModifier as? ScopedModifier.Row
    val isScrollable = scopedMod?.base?.scrollable == true && !parentScrollable

    val rowModifier =
        if (isScrollable) {
            if (scopedMod?.base?.width != null) {
                modifier.width(scopedMod.base.width.dp).horizontalScroll(rememberScrollState())
            } else {
                modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
            }
        } else {
            modifier
        }

    val horizontalArrangement =
        scopedMod?.horizontalArrangement?.let { arrangement ->
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

    val verticalAlignment =
        scopedMod?.verticalAlignment?.let { alignment ->
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
        verticalAlignment = verticalAlignment,
    ) {
        component.children?.forEachIndexed { index, wrapper ->
            ChildDynamicLayout(
                wrapper.component,
                modifier = Modifier,
                path = "$path-row-$index",
                parentScrollable = isScrollable,
                onClickHandler = onClickHandler,
            )
        }
    }
}

@Composable
private fun RenderBox(
    component: LayoutComponent.Box,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit,
) {
    val scopedMod = component.scopedModifier as? ScopedModifier.Box

    val contentAlignment =
        scopedMod?.contentAlignment?.let { alignment ->
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
        contentAlignment = contentAlignment,
    ) {
        component.children?.forEachIndexed { index, wrapper ->
            ChildDynamicLayout(
                wrapper.component,
                modifier = Modifier,
                path = "$path-box-$index",
                parentScrollable = parentScrollable,
                onClickHandler = onClickHandler,
            )
        }
    }
}

@Composable
private fun RenderText(
    component: LayoutComponent.Text,
    modifier: Modifier,
) {
    val bindsValue = LocalBindsValue.current

    Text(
        text = bindsValue.getValue(component) ?: component.content,
        modifier = modifier,
    )
}

@Composable
private fun RenderButton(
    component: LayoutComponent.Button,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = {
            component.clickId?.let { onClickHandler(it) }
        },
    ) {
        val bindsValue = LocalBindsValue.current

        if (component.content != null) {
            Text(text = bindsValue.getValue(component) ?: component.content)
        } else {
            component.children?.forEachIndexed { index, wrapper ->
                ChildDynamicLayout(
                    wrapper.component,
                    modifier = Modifier,
                    path = "$path-button-$index",
                    parentScrollable = parentScrollable,
                    onClickHandler = onClickHandler,
                )
            }
        }
    }
}

@Composable
private fun RenderCard(
    component: LayoutComponent.Card,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
    ) {
        component.children?.forEachIndexed { index, wrapper ->
            ChildDynamicLayout(
                wrapper.component,
                modifier = Modifier,
                path = "$path-card-$index",
                parentScrollable = parentScrollable,
                onClickHandler = onClickHandler,
            )
        }
    }
}

@Composable
private fun RenderSpacer(
    component: LayoutComponent.Spacer,
) {
    Spacer(
        modifier = Modifier
            .height(component.height.dp)
            .width(component.width.dp),
    )
}

@Composable
private fun RenderCustomNode(
    component: LayoutComponent.Custom,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    onClickHandler: (String) -> Unit,
) {
    val bindsValue = LocalBindsValue.current
    CustomNodes.get(component.type)?.let { renderer ->
        val newData = component.data.mapValues { (_, value) ->
            bindsValue.getValue<String>(component, value) ?: value
        }

        renderer(
            CustomNodes.NodeParam(
                data = newData,
                modifier = modifier,
                children = component.children,
                path = path,
                parentScrollable = parentScrollable,
                onClickHandler = onClickHandler,
                bindsValue = bindsValue,
            ),
        )
    }
}

@Composable
fun createLayoutComponent(textJson: String): LayoutComponent? {
    val layoutNode by remember(textJson) { derivedStateOf { parseLayoutJson(textJson) } }
    return layoutNode
}
