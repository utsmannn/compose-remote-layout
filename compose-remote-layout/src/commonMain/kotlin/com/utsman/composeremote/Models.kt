package com.utsman.composeremote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LayoutComponent {
    abstract val modifier: LayoutModifier?

    val scopedModifier: ScopedModifier?
        get() = when (this) {
            is Column -> modifier?.toScopedModifier("column")
            is Row -> modifier?.toScopedModifier("row")
            is Box -> modifier?.toScopedModifier("box")
            else -> modifier?.toScopedModifier("default")
        }

    @Serializable
    data class Column(override val modifier: LayoutModifier? = null, val children: List<ComponentWrapper>? = null) : LayoutComponent()

    @Serializable
    data class Row(override val modifier: LayoutModifier? = null, val children: List<ComponentWrapper>? = null) : LayoutComponent()

    @Serializable
    data class Box(override val modifier: LayoutModifier? = null, val children: List<ComponentWrapper>? = null) : LayoutComponent()

    @Serializable
    data class Text(
        override val modifier: LayoutModifier? = null,
        val content: String,
        val color: String? = null,
        val fontSize: Int? = null,
        val fontWeight: String? = null,
        val fontStyle: String? = null,
        val letterSpacing: Int? = null,
        val lineHeight: Int? = null,
        val textAlign: String? = null,
        val textDecoration: String? = null,
    ) : LayoutComponent()

    @Serializable
    data class Button(
        override val modifier: LayoutModifier? = null,
        val content: String? = null,
        val clickId: String? = null,
        val children: List<ComponentWrapper>? = null,
        val fontColor: String? = null,
        val fontSize: Int? = null,
        val fontWeight: String? = null,
        val fontStyle: String? = null,
        val letterSpacing: Int? = null,
        val lineHeight: Int? = null,
        val textAlign: String? = null,
        val textDecoration: String? = null,
    ) : LayoutComponent()

    @Serializable
    data class Card(override val modifier: LayoutModifier? = null, val children: List<ComponentWrapper>? = null) : LayoutComponent()

    @Serializable
    data class Spacer(
        override val modifier: LayoutModifier? = null,
        val height: Int = 0,
        val width: Int = 0,
    ) : LayoutComponent()

    @Serializable
    data class Custom(
        override val modifier: LayoutModifier? = null,
        val type: String,
        val data: Map<String, String>,
        val children: List<ComponentWrapper>? = null,
    ) : LayoutComponent()
}

@Serializable
data class ComponentWrapper(
    val column: LayoutComponent.Column? = null,
    val row: LayoutComponent.Row? = null,
    val box: LayoutComponent.Box? = null,
    val text: LayoutComponent.Text? = null,
    val button: LayoutComponent.Button? = null,
    val card: LayoutComponent.Card? = null,
    val spacer: LayoutComponent.Spacer? = null,
    val custom: LayoutComponent.Custom? = null,
) {
    val component: LayoutComponent
        get() {
            column?.let { return it }
            row?.let { return it }
            box?.let { return it }
            text?.let { return it }
            button?.let { return it }
            card?.let { return it }
            spacer?.let { return it }
            custom?.let { return it }
            return LayoutComponent.Column()
        }
}

@Serializable
sealed class ScopedModifier {
    abstract val base: BaseModifier

    @Serializable
    @SerialName("column")
    data class Column(
        override val base: BaseModifier = BaseModifier(),
        val verticalArrangement: String? = null,
        val horizontalAlignment: String? = null,
    ) : ScopedModifier()

    @Serializable
    @SerialName("row")
    data class Row(
        override val base: BaseModifier = BaseModifier(),
        val horizontalArrangement: String? = null,
        val verticalAlignment: String? = null,
    ) : ScopedModifier()

    @Serializable
    @SerialName("box")
    data class Box(override val base: BaseModifier = BaseModifier(), val contentAlignment: String? = null) : ScopedModifier()

    @Serializable
    @SerialName("default")
    data class Default(override val base: BaseModifier = BaseModifier()) : ScopedModifier()
}

@Serializable
data class LayoutModifier(
    val base: BaseModifier = BaseModifier(),
    val verticalArrangement: String? = null,
    val horizontalAlignment: String? = null,
    val horizontalArrangement: String? = null,
    val verticalAlignment: String? = null,
    val contentAlignment: String? = null,
) {
    fun toScopedModifier(type: String): ScopedModifier = when (type) {
        "column" -> ScopedModifier.Column(
            base = base,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        )

        "row" -> ScopedModifier.Row(
            base = base,
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment,
        )

        "box" -> ScopedModifier.Box(
            base = base,
            contentAlignment = contentAlignment,
        )

        else -> ScopedModifier.Default(base)
    }
}

@Serializable
data class BaseModifier(
    val width: Int? = null,
    val height: Int? = null,
    val size: Int? = null,
    val fillMaxWidth: Boolean? = false,
    val fillMaxHeight: Boolean? = false,
    val fillMaxSize: Boolean? = false,
    val padding: PaddingValues? = null,
    val margin: MarginValues? = null,
    val background: StyleValues? = null,
    val border: BorderValues? = null,
    val shadow: ShadowValues? = null,
    val scrollable: Boolean? = false,
    val clickable: Boolean? = false,
    val alpha: Float? = null,
    val rotate: Float? = null,
    val scale: ScaleValues? = null,
    val offset: OffsetValues? = null,
    val aspectRatio: Float? = null,
    val clip: Boolean? = false,
    val wrapContentHeight: Boolean? = false,
    val wrapContentWidth: Boolean? = false,
)

@Serializable
data class PaddingValues(
    val all: Int? = null,
    val horizontal: Int? = null,
    val vertical: Int? = null,
    val start: Int? = null,
    val top: Int? = null,
    val end: Int? = null,
    val bottom: Int? = null,
)

@Serializable
data class MarginValues(
    val all: Int? = null,
    val horizontal: Int? = null,
    val vertical: Int? = null,
    val start: Int? = null,
    val top: Int? = null,
    val end: Int? = null,
    val bottom: Int? = null,
)

@Serializable
data class StyleValues(
    val color: String? = null,
    val alpha: Float? = null,
    val shape: String? = null,
    val radius: Int? = null,
)

@Serializable
data class BorderValues(
    val width: Int = 1,
    val color: String? = null,
    val shape: ShapeValues? = null,
)

@Serializable
data class ScaleValues(
    val scaleX: Float? = null,
    val scaleY: Float? = null,
)

@Serializable
data class OffsetValues(
    val x: Int? = null,
    val y: Int? = null,
)

@Serializable
data class ShapeValues(
    val type: String = "rectangle",
    val cornerRadius: Int? = null,
    val topStart: Int? = null,
    val topEnd: Int? = null,
    val bottomStart: Int? = null,
    val bottomEnd: Int? = null,
)

@Serializable
data class ShadowValues(val elevation: Int = 4, val shape: ShapeValues? = null)
