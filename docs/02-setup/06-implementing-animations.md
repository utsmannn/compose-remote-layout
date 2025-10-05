# Implementing Animation Support

This guide explains how to implement animation support in your Compose Remote Layout application using custom nodes. While the library doesn't include built-in animation components yet, you can easily add them using the custom node system.

## Overview

Animations can be implemented by registering custom nodes that wrap Compose's animation APIs. This allows you to define animations in JSON and control them remotely.

## Prerequisites

Before implementing animations, make sure you're familiar with:

- [Custom Nodes](05a-custom-node.md) - Understanding how to create custom components
- [Value Binding](04-bind-values.md) - How to bind dynamic values
- Compose Animation APIs - Basic understanding of Compose animations

## Implementing AnimatedVisibility

AnimatedVisibility is one of the most useful animation components. Here's how to implement it:

### Step 1: Register the Custom Node

```kotlin
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.utsman.composeremote.CustomNodes
import com.utsman.composeremote.DynamicLayout

fun registerAnimatedVisibility() {
    CustomNodes.register("animated_visibility") { param ->
        // Parse visibility state from bind value or direct value
        val visibleString = param.data["visible"] ?: "true"
        val visible = visibleString.toBoolean()
        
        // Parse enter animation
        val enterAnimation = parseEnterTransition(param.data)
        
        // Parse exit animation
        val exitAnimation = parseExitTransition(param.data)
        
        AnimatedVisibility(
            visible = visible,
            enter = enterAnimation,
            exit = exitAnimation,
            modifier = param.modifier
        ) {
            // Render children
            param.children?.forEach { childWrapper ->
                DynamicLayout(
                    component = childWrapper.component,
                    path = "${param.path}-child-${childWrapper.hashCode()}",
                    parentScrollable = param.parentScrollable,
                    onClickHandler = param.onClickHandler,
                    bindValue = param.bindsValue
                )
            }
        }
    }
}

private fun parseEnterTransition(data: Map<String, String>): EnterTransition {
    val enterType = data["enterType"] ?: "fadeIn"
    val duration = data["enterDuration"]?.toIntOrNull() ?: 300
    val delay = data["enterDelay"]?.toIntOrNull() ?: 0
    
    val animationSpec = tween<Float>(
        durationMillis = duration,
        delayMillis = delay,
        easing = parseEasing(data["enterEasing"] ?: "easeInOut")
    )
    
    return when (enterType) {
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
        "expandVertically" -> expandVertically(animationSpec = animationSpec)
        "expandHorizontally" -> expandHorizontally(animationSpec = animationSpec)
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

private fun parseExitTransition(data: Map<String, String>): ExitTransition {
    val exitType = data["exitType"] ?: "fadeOut"
    val duration = data["exitDuration"]?.toIntOrNull() ?: 300
    val delay = data["exitDelay"]?.toIntOrNull() ?: 0
    
    val animationSpec = tween<Float>(
        durationMillis = duration,
        delayMillis = delay,
        easing = parseEasing(data["exitEasing"] ?: "easeInOut")
    )
    
    return when (exitType) {
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
        "shrinkVertically" -> shrinkVertically(animationSpec = animationSpec)
        "shrinkHorizontally" -> shrinkHorizontally(animationSpec = animationSpec)
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

private fun parseEasing(easing: String): Easing {
    return when (easing) {
        "linear" -> LinearEasing
        "easeIn" -> FastOutSlowInEasing
        "easeOut" -> LinearOutSlowInEasing
        "easeInOut" -> FastOutSlowInEasing
        "easeInOutCubic" -> EaseInOut
        else -> FastOutSlowInEasing
    }
}
```

### Step 2: Register at App Startup

Register the custom node when your app starts:

```kotlin
@Composable
fun App() {
    // Register custom animations
    LaunchedEffect(Unit) {
        registerAnimatedVisibility()
    }
    
    // Your app content
    // ...
}
```

### Step 3: Use in JSON

Now you can use the animation in your JSON layouts:

```json
{
  "animated_visibility": {
    "visible": "{isExpanded}",
    "enterType": "expandVertically",
    "enterDuration": "300",
    "exitType": "shrinkVertically",
    "exitDuration": "300",
    "modifier": {
      "base": {
        "fillMaxWidth": true
      }
    },
    "children": [
      {
        "text": {
          "content": "Animated content"
        }
      }
    ]
  }
}
```

## Implementing AnimatedContent

AnimatedContent allows you to animate between different content states:

```kotlin
fun registerAnimatedContent() {
    CustomNodes.register("animated_content") { param ->
        // Parse target state from bind value
        val targetState = param.data["targetState"] ?: ""
        
        // Parse transition spec
        val transitionType = param.data["transitionType"] ?: "fade"
        val duration = param.data["transitionDuration"]?.toIntOrNull() ?: 300
        
        AnimatedContent(
            targetState = targetState,
            transitionSpec = {
                when (transitionType) {
                    "fade" -> fadeIn(animationSpec = tween(duration)) togetherWith 
                             fadeOut(animationSpec = tween(duration))
                    "slideLeft" -> slideInHorizontally(
                        animationSpec = tween(duration),
                        initialOffsetX = { it }
                    ) togetherWith slideOutHorizontally(
                        animationSpec = tween(duration),
                        targetOffsetX = { -it }
                    )
                    "slideRight" -> slideInHorizontally(
                        animationSpec = tween(duration),
                        initialOffsetX = { -it }
                    ) togetherWith slideOutHorizontally(
                        animationSpec = tween(duration),
                        targetOffsetX = { it }
                    )
                    else -> fadeIn(animationSpec = tween(duration)) togetherWith 
                           fadeOut(animationSpec = tween(duration))
                }
            },
            modifier = param.modifier,
            label = param.data["label"] ?: "animated_content"
        ) { state ->
            // Find the matching child based on state
            val matchingChild = param.children?.find { child ->
                child.component.let { component ->
                    // You can add custom logic here to match state to content
                    // For simplicity, render first child for this example
                    true
                }
            }
            
            matchingChild?.let { childWrapper ->
                DynamicLayout(
                    component = childWrapper.component,
                    path = "${param.path}-content-$state",
                    parentScrollable = param.parentScrollable,
                    onClickHandler = param.onClickHandler,
                    bindValue = param.bindsValue
                )
            }
        }
    }
}
```

## Implementing AnimateContentSize

Add smooth size animations when content changes:

```kotlin
fun registerAnimateContentSizeModifier() {
    // Note: This would ideally be integrated into the base modifier system
    // For now, you can create a wrapper component
    
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
            param.children?.forEach { childWrapper ->
                DynamicLayout(
                    component = childWrapper.component,
                    path = "${param.path}-child-${childWrapper.hashCode()}",
                    parentScrollable = param.parentScrollable,
                    onClickHandler = param.onClickHandler,
                    bindValue = param.bindsValue
                )
            }
        }
    }
}
```

## Advanced: State-Based Animation Control

For more complex scenarios, you can use state management with animations:

```kotlin
fun registerAdvancedAnimatedVisibility() {
    CustomNodes.register("stateful_animated_visibility") { param ->
        // Use a state holder that can be controlled
        var visible by remember { 
            mutableStateOf(param.data["visible"]?.toBoolean() ?: true) 
        }
        
        // Update visibility when bind value changes
        LaunchedEffect(param.bindsValue) {
            val bindKey = param.data["visibilityKey"] ?: "visible"
            param.bindsValue.getValue(bindKey)?.let { value ->
                visible = value.toBoolean()
            }
        }
        
        AnimatedVisibility(
            visible = visible,
            enter = parseEnterTransition(param.data),
            exit = parseExitTransition(param.data),
            modifier = param.modifier
        ) {
            param.children?.forEach { childWrapper ->
                DynamicLayout(
                    component = childWrapper.component,
                    path = "${param.path}-child-${childWrapper.hashCode()}",
                    parentScrollable = param.parentScrollable,
                    onClickHandler = param.onClickHandler,
                    bindValue = param.bindsValue
                )
            }
        }
    }
}
```

## Complete Implementation Example

For a production-ready implementation that you can copy directly into your project, see the [AnimationNodes.kt example](../examples/AnimationNodes.kt).

Here's a complete example showing how to set up animations in your app:

```kotlin
// AnimationNodes.kt
object AnimationNodes {
    fun registerAll() {
        registerAnimatedVisibility()
        registerAnimatedContent()
        registerAnimateContentSizeModifier()
    }
    
    private fun registerAnimatedVisibility() {
        CustomNodes.register("animated_visibility") { param ->
            val visibleString = param.data["visible"] ?: "true"
            val visible = visibleString.toBoolean()
            
            AnimatedVisibility(
                visible = visible,
                enter = parseEnterTransition(param.data),
                exit = parseExitTransition(param.data),
                modifier = param.modifier
            ) {
                Column {
                    param.children?.forEach { childWrapper ->
                        DynamicLayout(
                            component = childWrapper.component,
                            path = "${param.path}-child-${childWrapper.hashCode()}",
                            parentScrollable = param.parentScrollable,
                            onClickHandler = param.onClickHandler,
                            bindValue = param.bindsValue
                        )
                    }
                }
            }
        }
    }
    
    private fun parseEnterTransition(data: Map<String, String>): EnterTransition {
        val type = data["enterType"] ?: "fadeIn"
        val duration = data["enterDuration"]?.toIntOrNull() ?: 300
        val animationSpec = tween<Float>(durationMillis = duration)
        
        return when (type) {
            "fadeIn" -> fadeIn(animationSpec = animationSpec)
            "slideInVertically" -> slideInVertically(animationSpec = animationSpec)
            "expandVertically" -> expandVertically(animationSpec = animationSpec)
            else -> fadeIn(animationSpec = animationSpec)
        }
    }
    
    private fun parseExitTransition(data: Map<String, String>): ExitTransition {
        val type = data["exitType"] ?: "fadeOut"
        val duration = data["exitDuration"]?.toIntOrNull() ?: 300
        val animationSpec = tween<Float>(durationMillis = duration)
        
        return when (type) {
            "fadeOut" -> fadeOut(animationSpec = animationSpec)
            "slideOutVertically" -> slideOutVertically(animationSpec = animationSpec)
            "shrinkVertically" -> shrinkVertically(animationSpec = animationSpec)
            else -> fadeOut(animationSpec = animationSpec)
        }
    }
    
    // Add other helper functions...
}

// In your main app file:
@Composable
fun App() {
    LaunchedEffect(Unit) {
        AnimationNodes.registerAll()
    }
    
    val bindsValue = remember { BindsValue() }
    bindsValue.setValue("isExpanded", "false")
    
    val layoutJson = """
    {
      "column": {
        "children": [
          {
            "button": {
              "content": "Toggle",
              "clickId": "toggle"
            }
          },
          {
            "animated_visibility": {
              "visible": "{isExpanded}",
              "enterType": "expandVertically",
              "exitType": "shrinkVertically",
              "children": [
                {
                  "text": {
                    "content": "Animated content!"
                  }
                }
              ]
            }
          }
        ]
      }
    }
    """.trimIndent()
    
    val component = remember(layoutJson) {
        createLayoutComponent(layoutJson)
    }
    
    DynamicLayout(
        component = component,
        bindValue = bindsValue,
        onClickHandler = { clickId ->
            when (clickId) {
                "toggle" -> {
                    val current = bindsValue.getValue("isExpanded")?.toBoolean() ?: false
                    bindsValue.setValue("isExpanded", (!current).toString())
                }
            }
        }
    )
}
```

## Best Practices

### 1. Create a Dedicated Animation Module

Organize your animation implementations in a dedicated file or module:

```
app/
  src/
    main/
      kotlin/
        animations/
          AnimationNodes.kt
          AnimationParsers.kt
          AnimationTypes.kt
```

### 2. Provide Sensible Defaults

Always provide default values for animation parameters:

```kotlin
val duration = param.data["duration"]?.toIntOrNull() ?: 300
val easing = param.data["easing"] ?: "easeInOut"
```

### 3. Support Bind Values

Make sure your animations support bind values for dynamic control:

```kotlin
val visibleString = param.bindsValue.getValue("isVisible") 
                   ?: param.data["visible"] 
                   ?: "true"
val visible = visibleString.toBoolean()
```

### 4. Document Your Custom Animation Nodes

Create clear documentation for your team on how to use the custom animation nodes in JSON:

```markdown
## Available Animation Types

### animated_visibility
- `visible`: "true" or "false" or bind value
- `enterType`: "fadeIn", "slideInVertically", "expandVertically"
- `exitType`: "fadeOut", "slideOutVertically", "shrinkVertically"
- `enterDuration`: milliseconds (default: 300)
- `exitDuration`: milliseconds (default: 300)
```

### 5. Test Animations on Different Devices

Ensure animations perform well across different devices:

```kotlin
// Consider device capabilities
val duration = if (isLowEndDevice()) 200 else 300
val enableAnimations = !AccessibilitySettings.areAnimationsDisabled()
```

## Troubleshooting

### Animation Not Triggering

If your animation isn't triggering:

1. Check that the bind value is actually changing
2. Verify the visibility string is correctly parsed as boolean
3. Ensure the custom node is registered before rendering

### Performance Issues

If animations are choppy:

1. Reduce animation complexity for low-end devices
2. Use simpler animation types (fade vs slide)
3. Limit the number of simultaneously animating elements

### Children Not Rendering

If children aren't appearing:

1. Ensure you're iterating through `param.children`
2. Pass the correct parameters to `DynamicLayout`
3. Check that the parent container (Column, Box) is properly set up

## Next Steps

Now that you've implemented animations:

1. Create a library of reusable animation patterns
2. Document the available animations for your team
3. Build an animation showcase in your app
4. Consider contributing animation support back to the library

For more information:
- [Custom Nodes Documentation](05a-custom-node.md)
- [Value Binding](04-bind-values.md)
- [JSON Structure](../03-json-structure/06-layout-json-structure.md)
