package com.utsman.composeremote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val layoutSerializersModule = SerializersModule {
    polymorphic(ScopedModifier::class) {
        subclass(ScopedModifier.Column::class)
        subclass(ScopedModifier.Row::class)
        subclass(ScopedModifier.Box::class)
        subclass(ScopedModifier.Default::class)
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
        val horizontalAlignment: String? = null
    ) : ScopedModifier()

    @Serializable
    @SerialName("row")
    data class Row(
        override val base: BaseModifier = BaseModifier(),
        val horizontalArrangement: String? = null,
        val verticalAlignment: String? = null
    ) : ScopedModifier()

    @Serializable
    @SerialName("box")
    data class Box(
        override val base: BaseModifier = BaseModifier(),
        val contentAlignment: String? = null
    ) : ScopedModifier()

    @Serializable
    @SerialName("default")
    data class Default(
        override val base: BaseModifier = BaseModifier()
    ) : ScopedModifier()
}

@Serializable
data class LayoutModifier(
//    val type: String, // discriminator field
    val base: BaseModifier = BaseModifier(),
    val verticalArrangement: String? = null,
    val horizontalAlignment: String? = null,
    val horizontalArrangement: String? = null,
    val verticalAlignment: String? = null,
    val contentAlignment: String? = null
) {
    fun toScopedModifier(type: String): ScopedModifier {
        return when (type) {
            "column" -> ScopedModifier.Column(
                base = base,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment
            )
            "row" -> ScopedModifier.Row(
                base = base,
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment
            )
            "box" -> ScopedModifier.Box(
                base = base,
                contentAlignment = contentAlignment
            )
            else -> ScopedModifier.Default(base)
        }
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
data class LayoutNode(
    val type: String,
    val text: String? = null,
    val modifier: LayoutModifier? = null,
    val children: List<LayoutNode>? = null,
    val clickId: String? = null
) {
    val scopedModifier: ScopedModifier?
        get() = modifier?.toScopedModifier(type)
}

@Serializable
data class PaddingValues(
    val all: Int? = null,
    val horizontal: Int? = null,
    val vertical: Int? = null,
    val start: Int? = null,
    val top: Int? = null,
    val end: Int? = null,
    val bottom: Int? = null
)

@Serializable
data class MarginValues(
    val all: Int? = null,
    val horizontal: Int? = null,
    val vertical: Int? = null,
    val start: Int? = null,
    val top: Int? = null,
    val end: Int? = null,
    val bottom: Int? = null
)

@Serializable
data class StyleValues(
    val color: String? = null,
    val alpha: Float? = null
)

@Serializable
data class BorderValues(
    val width: Int = 1,
    val color: String? = null,
    val shape: ShapeValues? = null
)

@Serializable
data class ShapeValues(
    val type: String = "rectangle", // rectangle, roundedCorner, circle
    val cornerRadius: Int? = null
)

@Serializable
data class ShadowValues(
    val elevation: Int = 4,
    val shape: ShapeValues? = null
)