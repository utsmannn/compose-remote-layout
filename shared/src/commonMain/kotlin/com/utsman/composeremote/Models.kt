package com.utsman.composeremote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LayoutComponent {
    abstract val modifier: LayoutModifier?

    val scopedModifier: ScopedModifier?
        get() =
            when (this) {
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
    data class Text(override val modifier: LayoutModifier? = null, val text: String) : LayoutComponent()

    @Serializable
    data class Button(
        override val modifier: LayoutModifier? = null,
        val text: String? = null,
        val clickId: String? = null,
        val children: List<ComponentWrapper>? = null,
    ) : LayoutComponent()

    @Serializable
    data class Card(override val modifier: LayoutModifier? = null, val children: List<ComponentWrapper>? = null) : LayoutComponent()
}

@Serializable
data class ComponentWrapper(
    val column: LayoutComponent.Column? = null,
    val row: LayoutComponent.Row? = null,
    val box: LayoutComponent.Box? = null,
    val text: LayoutComponent.Text? = null,
    val button: LayoutComponent.Button? = null,
    val card: LayoutComponent.Card? = null,
) {
    val component: LayoutComponent
        get() =
            column ?: row ?: box ?: text ?: button ?: card
                ?: throw IllegalStateException("Component wrapper must contain exactly one component")
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
        "column" ->
            ScopedModifier.Column(
                base = base,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            )
        "row" ->
            ScopedModifier.Row(
                base = base,
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment,
            )
        "box" ->
            ScopedModifier.Box(
                base = base,
                contentAlignment = contentAlignment,
            )
        else -> ScopedModifier.Default(base)
    }
}

@Serializable
data class BaseModifier(
    // Size modifiers
    val width: Int? = null,
    val height: Int? = null,
    val size: Int? = null,
    // Fill modifiers
    val fillMaxWidth: Boolean? = false,
    val fillMaxHeight: Boolean? = false,
    val fillMaxSize: Boolean? = false,
    // Padding and spacing
    val padding: PaddingValues? = null,
    val margin: MarginValues? = null,
    // Visual styling
    val background: StyleValues? = null,
    val border: BorderValues? = null,
    val shape: ShapeValues? = null,
    val shadow: ShadowValues? = null,
    // Basic behavior
    val scrollable: Boolean? = false,
    val clickable: Boolean? = false,
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
data class StyleValues(val color: String? = null, val alpha: Float? = null)

@Serializable
data class BorderValues(
    val width: Int = 1,
    val color: String? = null,
    val shape: ShapeValues? = null,
)

@Serializable
data class ShapeValues(
    val type: String = "rectangle", // rectangle, roundedCorner, circle
    val cornerRadius: Int? = null,
)

@Serializable
data class ShadowValues(val elevation: Int = 4, val shape: ShapeValues? = null)
