package com.utsman.composeremote

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    bindValue: BindsValue = remember { BindsValue() },
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
            parentOrientation = "unknown",
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
    parentOrientation: String,
    onClickHandler: (String) -> Unit,
) {
    val componentToRender =
        component
            ?: DynamicLayoutRenderer.getLastValidComponent(
                path,
            )
            ?: defaultComponent.component

    if (component != null) {
        DynamicLayoutRenderer.saveComponent(path, component)
    }

    DisposableEffect(Unit) {
        onDispose {
            DynamicLayoutRenderer.clearCache()
        }
    }

    val currentModifier = applyJsonModifier(
        modifier,
        componentToRender.scopedModifier,
        onClickHandler,
    )

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

        is LayoutComponent.Grid -> {
            RenderGrid(
                componentToRender,
                currentModifier,
                path,
                parentScrollable,
                parentOrientation,
                onClickHandler,
            )
        }

        is LayoutComponent.Box ->
            RenderBox(
                componentToRender,
                currentModifier,
                path,
                parentScrollable,
                onClickHandler,
            )

        is LayoutComponent.Text -> RenderText(
            componentToRender,
            currentModifier,
        )

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

        is LayoutComponent.Spacer -> RenderSpacer(
            componentToRender,
        )

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
    val scopedMod =
        component.scopedModifier as? ScopedModifier.Column
    val isScrollable =
        scopedMod?.base?.scrollable == true && !parentScrollable

    val currentScrollCache =
        LocalCacheScrollPosition.current

    val scrollState = rememberScrollState()

    val scrollDetector = rememberScrollStopDetector(
        scrollState = scrollState,
    )

    LaunchedEffect(scrollDetector) {
        if (isScrollable) {
            val scrollPosition = scrollState.value
            if (scrollPosition > 0) {
                currentScrollCache.put(path, scrollPosition)
            }
        }
    }

    LaunchedEffect(scrollState, isScrollable) {
        if (isScrollable) {
            val scrollPosition =
                currentScrollCache.get(path)
            scrollState.scrollTo(scrollPosition)
        }
    }

    val columnModifier =
        if (isScrollable) {
            if (scopedMod?.base?.height != null) {
                modifier.height(scopedMod.base.height.dp)
                    .verticalScroll(scrollState)
            } else {
                modifier.fillMaxHeight()
                    .verticalScroll(scrollState)
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
                parentOrientation = "vertical",
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
    val scopedMod =
        component.scopedModifier as? ScopedModifier.Row
    val isScrollable =
        scopedMod?.base?.scrollable == true && !parentScrollable

    val currentScrollCache =
        LocalCacheScrollPosition.current

    val scrollState = rememberScrollState()

    val scrollDetector = rememberScrollStopDetector(
        scrollState = scrollState,
    )

    LaunchedEffect(scrollDetector) {
        if (isScrollable) {
            val scrollPosition = scrollState.value
            if (scrollPosition > 0) {
                currentScrollCache.put(path, scrollPosition)
            }
        }
    }

    LaunchedEffect(scrollState, isScrollable) {
        if (isScrollable) {
            val scrollPosition =
                currentScrollCache.get(path)
            scrollState.scrollTo(scrollPosition)
        }
    }

    val rowModifier =
        if (isScrollable) {
            if (scopedMod?.base?.width != null) {
                modifier.width(scopedMod.base.width.dp)
                    .horizontalScroll(scrollState)
            } else {
                modifier
                    .horizontalScroll(scrollState)
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
                parentOrientation = "horizontal",
                onClickHandler = onClickHandler,
            )
        }
    }
}

@Composable
private fun RenderGrid(
    component: LayoutComponent.Grid,
    modifier: Modifier,
    path: String,
    parentScrollable: Boolean,
    parentOrientation: String,
    onClickHandler: (String) -> Unit,
) {
    val scopedMod =
        component.scopedModifier as? ScopedModifier.Grid

    val orientation = scopedMod?.orientation

    val isScrollable =
        scopedMod?.base?.scrollable == true && (!parentScrollable || (parentOrientation != orientation))

    val horizontalArrangement =
        scopedMod?.horizontalArrangement?.let { arrangement ->
            when (arrangement.lowercase()) {
                "start" -> Arrangement.Start
                "end" -> Arrangement.End
                "center" -> Arrangement.Center
                "spacebetween" -> Arrangement.SpaceBetween
                "spacearound" -> Arrangement.SpaceAround
                "spaceevenly" -> Arrangement.SpaceEvenly
                else -> {
                    val spacedBy = arrangement.toIntOrNull()
                    if (spacedBy != null) {
                        Arrangement.spacedBy(spacedBy.dp)
                    } else {
                        Arrangement.SpaceAround
                    }
                }
            }
        } ?: Arrangement.Start

    val verticalArrangement =
        scopedMod?.verticalArrangement?.let { arrangement ->
            when (arrangement.lowercase()) {
                "top" -> Arrangement.Top
                "center" -> Arrangement.Center
                "bottom" -> Arrangement.Bottom
                "spacebetween" -> Arrangement.SpaceBetween
                "spacearound" -> Arrangement.SpaceAround
                "spaceevenly" -> Arrangement.SpaceEvenly
                else -> {
                    val spacedBy = arrangement.toIntOrNull()
                    if (spacedBy != null) {
                        Arrangement.spacedBy(spacedBy.dp)
                    } else {
                        Arrangement.SpaceAround
                    }
                }
            }
        } ?: Arrangement.SpaceAround

    val padding = scopedMod?.base?.padding ?: com.utsman.composeremote.PaddingValues()
    val contentPadding = padding.toComposePaddingValue()

    if (orientation == "horizontal") {
        val gridModifier =
            if (scopedMod.base.height != null) {
                modifier
                    .height(scopedMod.base.height.dp)
            } else {
                modifier
            }

        if (isScrollable) {
            val enableSnap = scopedMod.enableSnapHorizontal ?: false
            ScrollableHorizontalGrid(
                items = component.children.orEmpty(),
                rows = scopedMod.rows ?: 1,
                modifier = gridModifier,
                horizontalArrangement = horizontalArrangement,
                verticalArrangement = verticalArrangement,
                enableSnap = enableSnap,
                contentPadding = contentPadding,
                path = path,
            ) { index, wrapper ->
                ChildDynamicLayout(
                    wrapper.component,
                    modifier = Modifier,
                    path = "$path-horizontal-grid-$index",
                    parentScrollable = isScrollable,
                    parentOrientation = parentOrientation,
                    onClickHandler = onClickHandler,
                )
            }
        } else {
            val paddingModifier = gridModifier.then(
                Modifier.padding(contentPadding),
            )

            val isFillMaxWidth = scopedMod.base.fillMaxWidth
            HorizontalGrid(
                items = component.children.orEmpty(),
                rows = scopedMod.rows ?: 1,
                modifier = paddingModifier,
                horizontalArrangement = horizontalArrangement,
                isFillMaxWidth = isFillMaxWidth ?: false,
                verticalArrangement = verticalArrangement,
            ) { index, wrapper ->
                ChildDynamicLayout(
                    wrapper.component,
                    modifier = Modifier,
                    path = "$path-horizontal-grid-$index",
                    parentScrollable = isScrollable,
                    parentOrientation = parentOrientation,
                    onClickHandler = onClickHandler,
                )
            }
        }
    } else {
        val currentScrollCache =
            LocalCacheScrollPosition.current

        val scrollState = rememberScrollState()

        val scrollDetector = rememberScrollStopDetector(
            scrollState = scrollState,
        )

        LaunchedEffect(scrollDetector) {
            if (isScrollable) {
                val scrollPosition = scrollState.value
                if (scrollPosition > 0) {
                    currentScrollCache.put(path, scrollPosition)
                }
            }
        }

        LaunchedEffect(scrollState, isScrollable) {
            if (isScrollable) {
                val scrollPosition =
                    currentScrollCache.get(path)
                scrollState.scrollTo(scrollPosition)
            }
        }

        val gridModifier =
            if (isScrollable) {
                if (scopedMod?.base?.width != null) {
                    modifier.width(scopedMod.base.width.dp)
                        .verticalScroll(scrollState)
                } else {
                    modifier
                        .verticalScroll(scrollState)
                }
            } else {
                modifier
            }

        val paddingModifier = gridModifier.then(
            Modifier.padding(contentPadding),
        )

        VerticalGrid(
            items = component.children.orEmpty(),
            columns = scopedMod?.columns ?: 1,
            modifier = paddingModifier,
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
        ) { index, wrapper ->
            ChildDynamicLayout(
                wrapper.component,
                modifier = Modifier,
                path = "$path-vertical-grid-$index",
                parentScrollable = isScrollable,
                parentOrientation = parentOrientation,
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
    val scopedMod =
        component.scopedModifier as? ScopedModifier.Box

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
                parentOrientation = "unknown",
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
    val defaultTextStyle = LocalTextStyle.current

    val color =
        component.color?.let { ColorParser.parseColor(it) }
            ?: defaultTextStyle.color
    val fontSize =
        component.fontSize?.sp ?: defaultTextStyle.fontSize
    val fontWeight = component.fontWeight?.toFontWeight()
        ?: defaultTextStyle.fontWeight
    val fontStyle = component.fontStyle?.toFontStyle()
        ?: defaultTextStyle.fontStyle
    val letterSpacing = component.letterSpacing?.sp
        ?: defaultTextStyle.letterSpacing
    val lineHeight = component.lineHeight?.sp
        ?: defaultTextStyle.lineHeight
    val textAlign = component.textAlign?.toTextAlign()
        ?: defaultTextStyle.textAlign
    val textDecoration =
        component.textDecoration?.toTextDecoration()
            ?: defaultTextStyle.textDecoration

    val maxLines = component.maxLines ?: Int.MAX_VALUE
    val minLines = component.minLines ?: 1
    val overflow = when (component.overflow?.lowercase()) {
        "visible" -> TextOverflow.Visible
        "ellipsis" -> TextOverflow.Ellipsis
        else -> TextOverflow.Clip
    }

    Text(
        text = bindsValue.getValue(component)
            ?: component.content,
        modifier = modifier,
        style = LocalTextStyle.current
            .copy(
                color = color,
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                letterSpacing = letterSpacing,
                lineHeight = lineHeight,
                textAlign = textAlign,
                textDecoration = textDecoration,
            ),
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
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
            val defaultTextStyle = LocalTextStyle.current

            val color = component.fontColor?.let {
                ColorParser.parseColor(it)
            } ?: defaultTextStyle.color
            val fontSize = component.fontSize?.sp
                ?: defaultTextStyle.fontSize
            val fontWeight =
                component.fontWeight?.toFontWeight()
                    ?: defaultTextStyle.fontWeight
            val fontStyle =
                component.fontStyle?.toFontStyle()
                    ?: defaultTextStyle.fontStyle
            val letterSpacing = component.letterSpacing?.sp
                ?: defaultTextStyle.letterSpacing
            val lineHeight = component.lineHeight?.sp
                ?: defaultTextStyle.lineHeight
            val textAlign =
                component.textAlign?.toTextAlign()
                    ?: defaultTextStyle.textAlign
            val textDecoration =
                component.textDecoration?.toTextDecoration()
                    ?: defaultTextStyle.textDecoration

            val maxLines =
                component.maxLines ?: Int.MAX_VALUE
            val minLines = component.minLines ?: 1
            val overflow =
                when (component.overflow?.lowercase()) {
                    "visible" -> TextOverflow.Visible
                    "ellipsis" -> TextOverflow.Ellipsis
                    else -> TextOverflow.Clip
                }

            Text(
                text = bindsValue.getValue(component)
                    ?: component.content,
                style = LocalTextStyle.current
                    .copy(
                        color = color,
                        fontSize = fontSize,
                        fontWeight = fontWeight,
                        fontStyle = fontStyle,
                        letterSpacing = letterSpacing,
                        lineHeight = lineHeight,
                        textAlign = textAlign,
                        textDecoration = textDecoration,
                    ),
                maxLines = maxLines,
                minLines = minLines,
                overflow = overflow,
            )
        } else {
            component.children?.forEachIndexed { index, wrapper ->
                ChildDynamicLayout(
                    wrapper.component,
                    modifier = Modifier,
                    path = "$path-button-$index",
                    parentScrollable = parentScrollable,
                    parentOrientation = "unknown",
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
                parentOrientation = "unknown",
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
        val newData =
            component.data.mapValues { (_, value) ->
                bindsValue.getValue<String>(
                    component,
                    value,
                ) ?: value
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

private fun String.toFontWeight(): FontWeight? = when (lowercase()) {
    "thin", "w100" -> FontWeight.W100
    "extralight", "w200" -> FontWeight.W200
    "light", "w300" -> FontWeight.W300
    "normal", "regular", "w400" -> FontWeight.W400
    "medium", "w500" -> FontWeight.W500
    "semibold", "w600" -> FontWeight.W600
    "bold", "w700" -> FontWeight.W700
    "extrabold", "w800" -> FontWeight.W800
    "black", "w900" -> FontWeight.W900
    else -> null
}

private fun String.toFontStyle(): FontStyle? = when (lowercase()) {
    "normal" -> FontStyle.Normal
    "italic" -> FontStyle.Italic
    else -> null
}

private fun String.toTextAlign(): TextAlign? = when (lowercase()) {
    "start" -> TextAlign.Start
    "end" -> TextAlign.End
    "center" -> TextAlign.Center
    "justify" -> TextAlign.Justify
    else -> null
}

private fun String.toTextDecoration(): TextDecoration? = when (lowercase()) {
    "none" -> TextDecoration.None
    "underline" -> TextDecoration.Underline
    "linethrough" -> TextDecoration.LineThrough
    "underline linethrough" -> TextDecoration.Underline + TextDecoration.LineThrough
    else -> null
}

@Composable
fun createLayoutComponent(textJson: String): LayoutComponent? {
    val layoutNode by remember(textJson) {
        derivedStateOf {
            parseLayoutJson(
                textJson,
            )
        }
    }
    return layoutNode
}

@Composable
private fun <T> VerticalGrid(
    items: List<T>,
    columns: Int,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical,
    horizontalArrangement: Arrangement.Horizontal,
    itemContent: @Composable (Int, T) -> Unit,
) {
    val rows = (items.size + columns - 1) / columns

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
    ) {
        for (rowIndex in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = horizontalArrangement,
            ) {
                for (columnIndex in 0 until columns) {
                    val itemIndex =
                        rowIndex * columns + columnIndex
                    if (itemIndex < items.size) {
                        Box(
                            modifier = Modifier
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            itemContent(
                                itemIndex,
                                items[itemIndex],
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> HorizontalGrid(
    items: List<T>,
    rows: Int,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal,
    verticalArrangement: Arrangement.Vertical,
    isFillMaxWidth: Boolean,
    itemContent: @Composable RowScope.(Int, T) -> Unit,
) {
    val columns = (items.size + rows - 1) / rows

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
    ) {
        val itemMod = if (isFillMaxWidth) {
            Modifier.weight(1f / rows)
        } else {
            Modifier.wrapContentWidth()
        }
        for (columnIndex in 0 until columns) {
            Column(
                modifier = itemMod,
                verticalArrangement = verticalArrangement,
            ) {
                for (rowIndex in 0 until rows) {
                    val itemIndex = columnIndex * rows + rowIndex
                    if (itemIndex < items.size) {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            itemContent(
                                this@Row,
                                itemIndex,
                                items[itemIndex],
                            )
                        }
                    } else {
                        Spacer(
                            modifier = Modifier.height(
                                IntrinsicSize.Min,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> ScrollableHorizontalGrid(
    items: List<T>,
    rows: Int,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    enableSnap: Boolean,
    path: String,
    contentPadding: PaddingValues = PaddingValues(),
    itemContent: @Composable (Int, T) -> Unit,
) {
    val columns = (items.size + rows - 1) / rows
    val listState = rememberLazyListState()

    val currentScrollCache =
        LocalCacheScrollOffsetPosition.current

    val scrollDetector = rememberLazyScrollStopDetector(
        lazyListState = listState,
    )

    LaunchedEffect(scrollDetector) {
        listState.layoutInfo
        val itemPosition = listState.firstVisibleItemIndex
        val scrollPosition = listState.firstVisibleItemScrollOffset
        if (scrollPosition > 0) {
            currentScrollCache.put(path, Pair(itemPosition, scrollPosition))
        }
    }

    LaunchedEffect(listState) {
        val (scrollPosition, offset) =
            currentScrollCache.get(path)

        listState.scrollToItem(scrollPosition, offset)
    }

    val flingBehavior = if (enableSnap) {
        rememberSnapFlingBehavior(listState)
    } else {
        ScrollableDefaults.flingBehavior()
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        flingBehavior = flingBehavior,
        contentPadding = contentPadding,
    ) {
        items(columns) { columnIndex ->
            Column(
                verticalArrangement = verticalArrangement,
            ) {
                for (rowIndex in 0 until rows) {
                    val itemIndex = columnIndex * rows + rowIndex
                    if (itemIndex < items.size) {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            itemContent(
                                itemIndex,
                                items[itemIndex],
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(IntrinsicSize.Min))
                    }
                }
            }
        }
    }
}
