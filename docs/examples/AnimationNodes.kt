package com.utsman.composeremote.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.utsman.composeremote.CustomNodes
import com.utsman.composeremote.DynamicLayout

/**
 * Sample implementation of animation support for Compose Remote Layout.
 * 
 * This file demonstrates how to register custom animation nodes that can be
 * controlled via JSON layouts. Copy and modify as needed for your app.
 * 
 * To use:
 * 1. Copy this file to your project
 * 2. Call AnimationNodes.registerAll() at app startup
 * 3. Use the animation components in your JSON layouts
 * 
 * @see <a href="https://utsmannn.github.io/compose-remote-layout/03-json-structure/08-animations/">Animation Documentation</a>
 */
object AnimationNodes {
    
    /**
     * Register all animation custom nodes.
     * Call this once when your app starts.
     */
    fun registerAll() {
        registerAnimatedVisibility()
        registerFadeVisibility()
        registerSlideVisibility()
        registerAnimatedSizeBox()
    }
    
    /**
     * Register AnimatedVisibility with configurable enter/exit animations.
     * 
     * JSON Usage:
     * ```json
     * {
     *   "animated_visibility": {
     *     "visible": "{isVisible}",
     *     "enterType": "expandVertically",
     *     "exitType": "shrinkVertically",
     *     "enterDuration": "300",
     *     "exitDuration": "300",
     *     "children": [...]
     *   }
     * }
     * ```
     */
    private fun registerAnimatedVisibility() {
        CustomNodes.register("animated_visibility") { param ->
            val visible = param.data["visible"]?.toBoolean() ?: true
            
            AnimatedVisibility(
                visible = visible,
                enter = parseEnterTransition(param.data),
                exit = parseExitTransition(param.data),
                modifier = param.modifier
            ) {
                Column {
                    param.children?.forEach { child ->
                        DynamicLayout(
                            component = child.component,
                            path = "${param.path}-child-${child.hashCode()}",
                            parentScrollable = param.parentScrollable,
                            onClickHandler = param.onClickHandler,
                            bindValue = param.bindsValue
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Register fade-only visibility animation.
     * Simpler version with just fade in/out.
     * 
     * JSON Usage:
     * ```json
     * {
     *   "fade_visibility": {
     *     "visible": "{showContent}",
     *     "duration": "400",
     *     "children": [...]
     *   }
     * }
     * ```
     */
    private fun registerFadeVisibility() {
        CustomNodes.register("fade_visibility") { param ->
            val visible = param.data["visible"]?.toBoolean() ?: true
            val duration = param.data["duration"]?.toIntOrNull() ?: 300
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(duration)),
                exit = fadeOut(animationSpec = tween(duration)),
                modifier = param.modifier
            ) {
                Column {
                    param.children?.forEach { child ->
                        DynamicLayout(
                            component = child.component,
                            path = "${param.path}-child-${child.hashCode()}",
                            parentScrollable = param.parentScrollable,
                            onClickHandler = param.onClickHandler,
                            bindValue = param.bindsValue
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Register slide-only visibility animation.
     * 
     * JSON Usage:
     * ```json
     * {
     *   "slide_visibility": {
     *     "visible": "{menuOpen}",
     *     "duration": "350",
     *     "direction": "vertical",
     *     "children": [...]
     *   }
     * }
     * ```
     */
    private fun registerSlideVisibility() {
        CustomNodes.register("slide_visibility") { param ->
            val visible = param.data["visible"]?.toBoolean() ?: true
            val duration = param.data["duration"]?.toIntOrNull() ?: 300
            val direction = param.data["direction"] ?: "vertical"
            
            val (enter, exit) = when (direction) {
                "vertical" -> Pair(
                    slideInVertically(animationSpec = tween(duration)) { -it },
                    slideOutVertically(animationSpec = tween(duration)) { -it }
                )
                "horizontal" -> Pair(
                    slideInHorizontally(animationSpec = tween(duration)) { -it },
                    slideOutHorizontally(animationSpec = tween(duration)) { -it }
                )
                else -> Pair(
                    slideInVertically(animationSpec = tween(duration)) { -it },
                    slideOutVertically(animationSpec = tween(duration)) { -it }
                )
            }
            
            AnimatedVisibility(
                visible = visible,
                enter = enter,
                exit = exit,
                modifier = param.modifier
            ) {
                Column {
                    param.children?.forEach { child ->
                        DynamicLayout(
                            component = child.component,
                            path = "${param.path}-child-${child.hashCode()}",
                            parentScrollable = param.parentScrollable,
                            onClickHandler = param.onClickHandler,
                            bindValue = param.bindsValue
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Register a Box with animateContentSize modifier.
     * Smoothly animates size changes of its content.
     * 
     * JSON Usage:
     * ```json
     * {
     *   "animated_size_box": {
     *     "sizeDuration": "300",
     *     "children": [...]
     *   }
     * }
     * ```
     */
    private fun registerAnimatedSizeBox() {
        CustomNodes.register("animated_size_box") { param ->
            val duration = param.data["sizeDuration"]?.toIntOrNull() ?: 300
            val easing = parseEasing(param.data["sizeEasing"] ?: "easeInOut")
            
            Box(
                modifier = param.modifier.animateContentSize(
                    animationSpec = tween(
                        durationMillis = duration,
                        easing = easing
                    )
                )
            ) {
                param.children?.forEach { child ->
                    DynamicLayout(
                        component = child.component,
                        path = "${param.path}-child-${child.hashCode()}",
                        parentScrollable = param.parentScrollable,
                        onClickHandler = param.onClickHandler,
                        bindValue = param.bindsValue
                    )
                }
            }
        }
    }
    
    /**
     * Parse enter transition from data map.
     */
    private fun parseEnterTransition(data: Map<String, String>): EnterTransition {
        val type = data["enterType"] ?: "fadeIn"
        val duration = data["enterDuration"]?.toIntOrNull() ?: 300
        val delay = data["enterDelay"]?.toIntOrNull() ?: 0
        val easing = parseEasing(data["enterEasing"] ?: "easeInOut")
        
        val animationSpec = tween<Float>(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        )
        
        return when (type) {
            "fadeIn" -> fadeIn(animationSpec = animationSpec)
            "slideInVertically" -> {
                val initialOffsetY = data["initialOffsetY"]?.toIntOrNull() ?: -100
                slideInVertically(
                    animationSpec = animationSpec,
                    initialOffsetY = { initialOffsetY }
                )
            }
            "slideInHorizontally" -> {
                val initialOffsetX = data["initialOffsetX"]?.toIntOrNull() ?: -100
                slideInHorizontally(
                    animationSpec = animationSpec,
                    initialOffsetX = { initialOffsetX }
                )
            }
            "expandVertically" -> expandVertically(
                animationSpec = tween(duration, delay, easing)
            )
            "expandHorizontally" -> expandHorizontally(
                animationSpec = tween(duration, delay, easing)
            )
            "scaleIn" -> {
                val initialScale = data["initialScale"]?.toFloatOrNull() ?: 0.8f
                scaleIn(
                    animationSpec = animationSpec,
                    initialScale = initialScale
                )
            }
            else -> fadeIn(animationSpec = animationSpec)
        }
    }
    
    /**
     * Parse exit transition from data map.
     */
    private fun parseExitTransition(data: Map<String, String>): ExitTransition {
        val type = data["exitType"] ?: "fadeOut"
        val duration = data["exitDuration"]?.toIntOrNull() ?: 300
        val delay = data["exitDelay"]?.toIntOrNull() ?: 0
        val easing = parseEasing(data["exitEasing"] ?: "easeInOut")
        
        val animationSpec = tween<Float>(
            durationMillis = duration,
            delayMillis = delay,
            easing = easing
        )
        
        return when (type) {
            "fadeOut" -> fadeOut(animationSpec = animationSpec)
            "slideOutVertically" -> {
                val targetOffsetY = data["targetOffsetY"]?.toIntOrNull() ?: -100
                slideOutVertically(
                    animationSpec = animationSpec,
                    targetOffsetY = { targetOffsetY }
                )
            }
            "slideOutHorizontally" -> {
                val targetOffsetX = data["targetOffsetX"]?.toIntOrNull() ?: -100
                slideOutHorizontally(
                    animationSpec = animationSpec,
                    targetOffsetX = { targetOffsetX }
                )
            }
            "shrinkVertically" -> shrinkVertically(
                animationSpec = tween(duration, delay, easing)
            )
            "shrinkHorizontally" -> shrinkHorizontally(
                animationSpec = tween(duration, delay, easing)
            )
            "scaleOut" -> {
                val targetScale = data["targetScale"]?.toFloatOrNull() ?: 0.8f
                scaleOut(
                    animationSpec = animationSpec,
                    targetScale = targetScale
                )
            }
            else -> fadeOut(animationSpec = animationSpec)
        }
    }
    
    /**
     * Parse easing function from string.
     */
    private fun parseEasing(easing: String): Easing {
        return when (easing.lowercase()) {
            "linear" -> LinearEasing
            "easein" -> EaseIn
            "easeout" -> EaseOut
            "easeinout" -> EaseInOut
            "fastoutslowIn" -> FastOutSlowInEasing
            "linearoutslowIn" -> LinearOutSlowInEasing
            "fastoutlinearin" -> FastOutLinearInEasing
            else -> FastOutSlowInEasing
        }
    }
}
