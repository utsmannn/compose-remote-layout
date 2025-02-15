package com.utsman.composeremote

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

fun applyJsonModifier(
    base: Modifier = Modifier,
    scopedModifier: ScopedModifier?,
): Modifier {
    if (scopedModifier == null) return base

    var mod = applyBaseModifier(base, scopedModifier.base)

    mod =
        when (scopedModifier) {
            is ScopedModifier.Column -> applyColumnModifier(mod, scopedModifier)
            is ScopedModifier.Row -> applyRowModifier(mod, scopedModifier)
            is ScopedModifier.Box -> applyBoxModifier(mod, scopedModifier)
            is ScopedModifier.Default -> mod
        }

    return mod
}

private fun applyBaseModifier(
    mod: Modifier,
    base: BaseModifier,
): Modifier {
    val modifierOrder = ModifierOrderTracker.getCurrentOrder()
    val modifierMap = mutableMapOf<String, Modifier>()

    if (base.width != null || base.height != null || base.size != null) {
        var sizeModifier: Modifier = Modifier
        base.width?.let { width ->
            sizeModifier = sizeModifier.then(Modifier.width(width.dp))
        }
        base.height?.let { height ->
            sizeModifier = sizeModifier.then(Modifier.height(height.dp))
        }
        base.size?.let { size ->
            sizeModifier = sizeModifier.then(Modifier.size(size.dp))
        }
        if (sizeModifier != Modifier) {
            modifierMap["width"] = sizeModifier
            modifierMap["height"] = sizeModifier
            modifierMap["size"] = sizeModifier
        }
    }

    if (base.fillMaxWidth == true || base.fillMaxHeight == true || base.fillMaxSize == true) {
        var fillModifier: Modifier = Modifier
        if (base.fillMaxWidth == true) {
            fillModifier = fillModifier.then(Modifier.fillMaxWidth())
        }
        if (base.fillMaxHeight == true) {
            fillModifier = fillModifier.then(Modifier.fillMaxHeight())
        }
        if (base.fillMaxSize == true) {
            fillModifier = fillModifier.then(Modifier.fillMaxSize())
        }
        modifierMap["fillMaxWidth"] = fillModifier
        modifierMap["fillMaxHeight"] = fillModifier
        modifierMap["fillMaxSize"] = fillModifier
    }

    base.background?.let { bg ->
        var backgroundModifier: Modifier = Modifier

        // Create shape if specified
        val shape = when (bg.shape?.lowercase()) {
            "circle" -> CircleShape
            "roundedcorner" -> RoundedCornerShape(bg.radius?.dp ?: 8.dp)
            else -> null
        }

        // Apply background color
        bg.color?.let { colorStr ->
            try {
                val color = ColorParser.parseColor(colorStr)
                backgroundModifier = backgroundModifier.then(
                    Modifier.background(
                        color = color.copy(alpha = bg.alpha ?: 1f),
                        shape = shape ?: RectangleShape,
                    ),
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Apply clip if shape is specified
        if (shape != null) {
            backgroundModifier = backgroundModifier.then(Modifier.clip(shape))
        }

        modifierMap["background"] = backgroundModifier
    }

    base.border?.let { border ->
        border.color?.let { colorStr ->
            try {
                val color = ColorParser.parseColor(colorStr)
                modifierMap["border"] =
                    Modifier.border(
                        width = border.width.dp,
                        color = color,
                        shape =
                            when (border.shape?.type?.lowercase()) {
                                "circle" -> CircleShape
                                "roundedcorner" -> RoundedCornerShape(border.shape.cornerRadius?.dp ?: 8.dp)
                                else -> RoundedCornerShape(0.dp)
                            },
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    base.shadow?.let { shadow ->
        modifierMap["shadow"] =
            Modifier.shadow(
                elevation = shadow.elevation.dp,
                shape =
                    when (shadow.shape?.type?.lowercase()) {
                        "circle" -> CircleShape
                        "roundedcorner" -> RoundedCornerShape(shadow.shape.cornerRadius?.dp ?: 8.dp)
                        else -> RoundedCornerShape(0.dp)
                    },
            )
    }

    base.padding?.let { padding ->
        modifierMap["padding"] =
            when {
                padding.all != null -> Modifier.padding(padding.all.dp)
                else ->
                    Modifier.padding(
                        start = padding.start?.dp ?: padding.horizontal?.dp ?: 0.dp,
                        top = padding.top?.dp ?: padding.vertical?.dp ?: 0.dp,
                        end = padding.end?.dp ?: padding.horizontal?.dp ?: 0.dp,
                        bottom = padding.bottom?.dp ?: padding.vertical?.dp ?: 0.dp,
                    )
            }
    }

    base.margin?.let { margin ->
        modifierMap["margin"] =
            when {
                margin.all != null -> Modifier.padding(margin.all.dp)
                else ->
                    Modifier.padding(
                        start = margin.start?.dp ?: margin.horizontal?.dp ?: 0.dp,
                        top = margin.top?.dp ?: margin.vertical?.dp ?: 0.dp,
                        end = margin.end?.dp ?: margin.horizontal?.dp ?: 0.dp,
                        bottom = margin.bottom?.dp ?: margin.vertical?.dp ?: 0.dp,
                    )
            }
    }

    if (base.clickable == true) {
        modifierMap["clickable"] = Modifier.clickable { }
    }
    if (base.scrollable == true) {
        modifierMap["scrollable"] = Modifier
    }

    base.alpha?.let { alpha ->
        modifierMap["alpha"] = Modifier.alpha(alpha)
    }

    base.rotate?.let { degrees ->
        modifierMap["rotate"] = Modifier.rotate(degrees)
    }

    base.scale?.let { scale ->
        val scaleModifier: Modifier
        if (scale.scaleX != null || scale.scaleY != null) {
            scaleModifier = Modifier.scale(
                scaleX = scale.scaleX ?: 1f,
                scaleY = scale.scaleY ?: 1f,
            )
            modifierMap["scale"] = scaleModifier
        }
    }

    base.offset?.let { offset ->
        modifierMap["offset"] = Modifier.offset(
            x = offset.x?.dp ?: 0.dp,
            y = offset.y?.dp ?: 0.dp,
        )
    }

    base.aspectRatio?.let { ratio ->
        modifierMap["aspectRatio"] = Modifier.aspectRatio(ratio)
    }

    if (base.wrapContentHeight == true) {
        modifierMap["wrapContentHeight"] = Modifier.wrapContentHeight()
    }
    if (base.wrapContentWidth == true) {
        modifierMap["wrapContentWidth"] = Modifier.wrapContentWidth()
    }

    var currentMod = mod
    modifierOrder.forEach { key ->
        modifierMap[key]?.let { modifier ->
            currentMod = currentMod.then(modifier)
        }
    }

    modifierMap.forEach { (key, modifier) ->
        if (!modifierOrder.contains(key)) {
            currentMod = currentMod.then(modifier)
        }
    }

    return currentMod
}

private fun applyColumnModifier(
    mod: Modifier,
    scopedMod: ScopedModifier.Column,
): Modifier {
    scopedMod.verticalArrangement?.let { arrangement ->
        val verticalArrangement =
            when (arrangement.lowercase()) {
                "top" -> Arrangement.Top
                "bottom" -> Arrangement.Bottom
                "center" -> Arrangement.Center
                "spacebetween" -> Arrangement.SpaceBetween
                "spacearound" -> Arrangement.SpaceAround
                "spaceevenly" -> Arrangement.SpaceEvenly
                else -> null
            }
        verticalArrangement
    }

    scopedMod.horizontalAlignment?.let { alignment ->
        val horizontalAlignment =
            when (alignment.lowercase()) {
                "start" -> Alignment.Start
                "end" -> Alignment.End
                "center" -> Alignment.CenterHorizontally
                else -> null
            }
        horizontalAlignment
    }

    return mod
}

private fun applyRowModifier(
    mod: Modifier,
    scopedMod: ScopedModifier.Row,
): Modifier {
    scopedMod.horizontalArrangement?.let { arrangement ->
        val horizontalArrangement =
            when (arrangement.lowercase()) {
                "start" -> Arrangement.Start
                "end" -> Arrangement.End
                "center" -> Arrangement.Center
                "spacebetween" -> Arrangement.SpaceBetween
                "spacearound" -> Arrangement.SpaceAround
                "spaceevenly" -> Arrangement.SpaceEvenly
                else -> null
            }
        horizontalArrangement
    }

    scopedMod.verticalAlignment?.let { alignment ->
        val verticalAlignment =
            when (alignment.lowercase()) {
                "top" -> Alignment.Top
                "bottom" -> Alignment.Bottom
                "center" -> Alignment.CenterVertically
                else -> null
            }
        verticalAlignment
    }

    return mod
}

private fun applyBoxModifier(
    mod: Modifier,
    scopedMod: ScopedModifier.Box,
): Modifier {
    scopedMod.contentAlignment?.let { alignment ->
        val boxAlignment =
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
                else -> null
            }
        boxAlignment
    }

    return mod
}

object ColorParser {
    /**
     * Parse color from hexadecimal string format
     * Supported formats:
     * - #RGB
     * - #RGBA
     * - #RRGGBB
     * - #RRGGBBAA
     */
    fun parseColor(colorString: String): Color {
        val formattedColor = colorString.trim().removePrefix("#")

        return when (formattedColor.length) {
            3 -> parseRGB(formattedColor)
            4 -> parseRGBA(formattedColor)
            6 -> parseRRGGBB(formattedColor)
            8 -> parseRRGGBBAA(formattedColor)
            else -> throw IllegalArgumentException("Invalid color format: $colorString")
        }
    }

    private fun parseRGB(colorString: String): Color {
        val r = colorString[0].toString().repeat(2).toInt(16)
        val g = colorString[1].toString().repeat(2).toInt(16)
        val b = colorString[2].toString().repeat(2).toInt(16)
        return Color(r, g, b)
    }

    private fun parseRGBA(colorString: String): Color {
        val r = colorString[0].toString().repeat(2).toInt(16)
        val g = colorString[1].toString().repeat(2).toInt(16)
        val b = colorString[2].toString().repeat(2).toInt(16)
        val a = colorString[3].toString().repeat(2).toInt(16)
        return Color(r, g, b, a)
    }

    private fun parseRRGGBB(colorString: String): Color {
        val r = colorString.substring(0, 2).toInt(16)
        val g = colorString.substring(2, 4).toInt(16)
        val b = colorString.substring(4, 6).toInt(16)
        return Color(r, g, b)
    }

    private fun parseRRGGBBAA(colorString: String): Color {
        val r = colorString.substring(0, 2).toInt(16)
        val g = colorString.substring(2, 4).toInt(16)
        val b = colorString.substring(4, 6).toInt(16)
        val a = colorString.substring(6, 8).toInt(16)
        return Color(r, g, b, a)
    }
}
