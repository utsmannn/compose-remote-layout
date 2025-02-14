package com.utsman.composeremote.app.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.utsman.composeremote.BaseModifier
import com.utsman.composeremote.ScopedModifier

fun applyJsonModifier(
    base: Modifier = Modifier,
    scopedModifier: ScopedModifier?
): Modifier {
    if (scopedModifier == null) return base

    // Apply base modifiers first
    var mod = applyBaseModifier(base, scopedModifier.base)

    // Apply scope-specific modifiers
    mod = when (scopedModifier) {
        is ScopedModifier.Column -> applyColumnModifier(mod, scopedModifier)
        is ScopedModifier.Row -> applyRowModifier(mod, scopedModifier)
        is ScopedModifier.Box -> applyBoxModifier(mod, scopedModifier)
        is ScopedModifier.Default -> mod
    }

    return mod
}

private fun applyBaseModifier(mod: Modifier, base: BaseModifier): Modifier {
    var currentMod = mod

    // Size modifiers
    base.width?.let { currentMod = currentMod.width(it.dp) }
    base.height?.let { currentMod = currentMod.height(it.dp) }
    base.size?.let { currentMod = currentMod.size(it.dp) }

    // Fill modifiers
    if (base.fillMaxWidth == true) currentMod = currentMod.fillMaxWidth()
    if (base.fillMaxHeight == true) currentMod = currentMod.fillMaxHeight()
    if (base.fillMaxSize == true) currentMod = currentMod.fillMaxSize()

    // Apply padding and margin
    currentMod = applySpacingModifiers(currentMod, base)

    // Apply visual styling
    currentMod = applyVisualModifiers(currentMod, base)

    // Apply behavior
    if (base.clickable == true) {
        currentMod = currentMod.clickable { }
    }

    return currentMod
}

private fun applyColumnModifier(mod: Modifier, scopedMod: ScopedModifier.Column): Modifier {
    var currentMod = mod

    // Apply vertical arrangement
    scopedMod.verticalArrangement?.let { arrangement ->
        val verticalArrangement = when (arrangement.lowercase()) {
            "top" -> Arrangement.Top
            "bottom" -> Arrangement.Bottom
            "center" -> Arrangement.Center
            "spacebetween" -> Arrangement.SpaceBetween
            "spacearound" -> Arrangement.SpaceAround
            "spaceevenly" -> Arrangement.SpaceEvenly
            else -> null
        }
        verticalArrangement?.let {
            // The arrangement will be applied in the Column composable
            // We store it to be used later
        }
    }

    // Apply horizontal alignment
    scopedMod.horizontalAlignment?.let { alignment ->
        val horizontalAlignment = when (alignment.lowercase()) {
            "start" -> Alignment.Start
            "end" -> Alignment.End
            "center" -> Alignment.CenterHorizontally
            else -> null
        }
        horizontalAlignment?.let {
            // The alignment will be applied in the Column composable
            // We store it to be used later
        }
    }

    return currentMod
}

private fun applyRowModifier(mod: Modifier, scopedMod: ScopedModifier.Row): Modifier {
    var currentMod = mod

    // Apply horizontal arrangement
    scopedMod.horizontalArrangement?.let { arrangement ->
        val horizontalArrangement = when (arrangement.lowercase()) {
            "start" -> Arrangement.Start
            "end" -> Arrangement.End
            "center" -> Arrangement.Center
            "spacebetween" -> Arrangement.SpaceBetween
            "spacearound" -> Arrangement.SpaceAround
            "spaceevenly" -> Arrangement.SpaceEvenly
            else -> null
        }
        horizontalArrangement?.let {
            // The arrangement will be applied in the Row composable
            // We store it to be used later
        }
    }

    // Apply vertical alignment
    scopedMod.verticalAlignment?.let { alignment ->
        val verticalAlignment = when (alignment.lowercase()) {
            "top" -> Alignment.Top
            "bottom" -> Alignment.Bottom
            "center" -> Alignment.CenterVertically
            else -> null
        }
        verticalAlignment?.let {
            // The alignment will be applied in the Row composable
            // We store it to be used later
        }
    }

    return currentMod
}

private fun applyBoxModifier(mod: Modifier, scopedMod: ScopedModifier.Box): Modifier {
    var currentMod = mod

    // Apply content alignment
    scopedMod.contentAlignment?.let { alignment ->
        val boxAlignment = when (alignment.lowercase()) {
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
        boxAlignment?.let {
            // The alignment will be applied in the Box composable
            // We store it to be used later
        }
    }

    return currentMod
}

// Helper functions from previous implementation
private fun applySpacingModifiers(mod: Modifier, base: BaseModifier): Modifier {
    var currentMod = mod

    base.padding?.let { padding ->
        currentMod = when {
            padding.all != null -> currentMod.padding(padding.all.dp)
            else -> currentMod.padding(
                start = padding.start?.dp ?: padding.horizontal?.dp ?: 0.dp,
                top = padding.top?.dp ?: padding.vertical?.dp ?: 0.dp,
                end = padding.end?.dp ?: padding.horizontal?.dp ?: 0.dp,
                bottom = padding.bottom?.dp ?: padding.vertical?.dp ?: 0.dp
            )
        }
    }

    base.margin?.let { margin ->
        currentMod = when {
            margin.all != null -> currentMod.padding(margin.all.dp)
            else -> currentMod.padding(
                start = margin.start?.dp ?: margin.horizontal?.dp ?: 0.dp,
                top = margin.top?.dp ?: margin.vertical?.dp ?: 0.dp,
                end = margin.end?.dp ?: margin.horizontal?.dp ?: 0.dp,
                bottom = margin.bottom?.dp ?: margin.vertical?.dp ?: 0.dp
            )
        }
    }

    return currentMod
}

private fun applyVisualModifiers(mod: Modifier, base: BaseModifier): Modifier {
    var currentMod = mod

    // Apply background
    base.background?.let { bg ->
        bg.color?.let { colorStr ->
            try {
                val color = ColorParser.parseColor(colorStr)
                currentMod = currentMod.background(
                    color = color.copy(alpha = bg.alpha ?: 1f)
                )
            } catch (e: Exception) {
                // Handle invalid color string
            }
        }
    }

    // Apply shape
    base.shape?.let { shape ->
        val composableShape = when (shape.type.lowercase()) {
            "circle" -> CircleShape
            "roundedcorner" -> RoundedCornerShape(shape.cornerRadius?.dp ?: 8.dp)
            else -> RoundedCornerShape(0.dp)
        }
        currentMod = currentMod.clip(composableShape)
    }

    // Apply border and shadow
    base.border?.let { border ->
        border.color?.let { colorStr ->
            try {
                val color = ColorParser.parseColor(colorStr)
                currentMod = currentMod.border(
                    width = border.width.dp,
                    color = color,
                    shape = when (border.shape?.type?.lowercase()) {
                        "circle" -> CircleShape
                        "roundedcorner" -> RoundedCornerShape(border.shape.cornerRadius?.dp ?: 8.dp)
                        else -> RoundedCornerShape(0.dp)
                    }
                )
            } catch (e: Exception) {
                // Handle invalid color string
            }
        }
    }

    base.shadow?.let { shadow ->
        currentMod = currentMod.shadow(
            elevation = shadow.elevation.dp,
            shape = when (shadow.shape?.type?.lowercase()) {
                "circle" -> CircleShape
                "roundedcorner" -> RoundedCornerShape(shadow.shape.cornerRadius?.dp ?: 8.dp)
                else -> RoundedCornerShape(0.dp)
            }
        )
    }

    return currentMod
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