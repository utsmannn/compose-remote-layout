# Quick Start: Adding Animations

This guide will help you add animation support to your Compose Remote Layout app in just a few minutes.

## What You'll Build

In this quick start, you'll add:
- Expandable/collapsible sections with smooth animations
- Fade-in/fade-out transitions
- Content that responds to user interactions

## Step 1: Create Animation Registration (5 minutes)

Create a new file `AnimationSupport.kt` in your project:

```kotlin
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import com.utsman.composeremote.CustomNodes
import com.utsman.composeremote.DynamicLayout

object AnimationSupport {
    
    fun register() {
        // Register AnimatedVisibility component
        CustomNodes.register("animated_visibility") { param ->
            val visible = param.data["visible"]?.toBoolean() ?: true
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = param.modifier
            ) {
                Column {
                    param.children?.forEach { child ->
                        DynamicLayout(
                            component = child.component,
                            path = "${param.path}-${child.hashCode()}",
                            onClickHandler = param.onClickHandler,
                            bindValue = param.bindsValue
                        )
                    }
                }
            }
        }
    }
}
```

## Step 2: Register at App Startup (1 minute)

In your main app file, register the animations:

```kotlin
@Composable
fun App() {
    // Register animations once
    LaunchedEffect(Unit) {
        AnimationSupport.register()
    }
    
    // Your app content
    MyContent()
}
```

## Step 3: Use in JSON (2 minutes)

Now create a JSON layout with animations:

```json
{
  "column": {
    "modifier": {
      "base": {
        "fillMaxWidth": true,
        "padding": {
          "all": 16
        }
      }
    },
    "children": [
      {
        "button": {
          "content": "Show Details",
          "clickId": "toggle_details",
          "modifier": {
            "base": {
              "fillMaxWidth": true
            }
          }
        }
      },
      {
        "animated_visibility": {
          "visible": "{showDetails}",
          "children": [
            {
              "card": {
                "modifier": {
                  "base": {
                    "fillMaxWidth": true,
                    "padding": {
                      "all": 16
                    }
                  }
                },
                "children": [
                  {
                    "text": {
                      "content": "These are the details that appear with animation!",
                      "fontSize": 14
                    }
                  }
                ]
              }
            }
          ]
        }
      }
    ]
  }
}
```

## Step 4: Wire Up the Click Handler (3 minutes)

Handle the button click to toggle visibility:

```kotlin
@Composable
fun MyContent() {
    // Create bind value for controlling animation
    val bindsValue = remember { BindsValue() }
    var showDetails by remember { mutableStateOf(false) }
    
    // Update bind value when state changes
    LaunchedEffect(showDetails) {
        bindsValue.setValue("showDetails", showDetails.toString())
    }
    
    // Your JSON layout string
    val layoutJson = """
    {
      "column": {
        "children": [
          {
            "button": {
              "content": "Show Details",
              "clickId": "toggle_details"
            }
          },
          {
            "animated_visibility": {
              "visible": "{showDetails}",
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
                "toggle_details" -> {
                    showDetails = !showDetails
                }
            }
        }
    )
}
```

## That's It!

You now have working animations in your remote layout! The content will smoothly expand and collapse when you click the button.

## Next: Add More Animation Types

Want more animation types? Update your `AnimationSupport.kt`:

```kotlin
object AnimationSupport {
    
    fun register() {
        registerAnimatedVisibility()
        registerFadeOnlyAnimation()
        registerSlideAnimation()
    }
    
    private fun registerAnimatedVisibility() {
        CustomNodes.register("animated_visibility") { param ->
            val visible = param.data["visible"]?.toBoolean() ?: true
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = param.modifier
            ) {
                Column {
                    param.children?.forEach { child ->
                        DynamicLayout(
                            component = child.component,
                            path = "${param.path}-${child.hashCode()}",
                            onClickHandler = param.onClickHandler,
                            bindValue = param.bindsValue
                        )
                    }
                }
            }
        }
    }
    
    private fun registerFadeOnlyAnimation() {
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
                            path = "${param.path}-${child.hashCode()}",
                            onClickHandler = param.onClickHandler,
                            bindValue = param.bindsValue
                        )
                    }
                }
            }
        }
    }
    
    private fun registerSlideAnimation() {
        CustomNodes.register("slide_visibility") { param ->
            val visible = param.data["visible"]?.toBoolean() ?: true
            val duration = param.data["duration"]?.toIntOrNull() ?: 300
            
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(animationSpec = tween(duration)),
                exit = slideOutVertically(animationSpec = tween(duration)),
                modifier = param.modifier
            ) {
                Column {
                    param.children?.forEach { child ->
                        DynamicLayout(
                            component = child.component,
                            path = "${param.path}-${child.hashCode()}",
                            onClickHandler = param.onClickHandler,
                            bindValue = param.bindsValue
                        )
                    }
                }
            }
        }
    }
}
```

Now you can use different animation types in your JSON:

```json
{
  "fade_visibility": {
    "visible": "{showContent}",
    "duration": "500",
    "children": [
      {
        "text": {
          "content": "This fades in and out"
        }
      }
    ]
  }
}
```

## Common Patterns

### Loading State

Show/hide loading indicators:

```json
{
  "box": {
    "modifier": {
      "base": {
        "fillMaxWidth": true
      },
      "contentAlignment": "center"
    },
    "children": [
      {
        "fade_visibility": {
          "visible": "{isLoading}",
          "duration": "200",
          "children": [
            {
              "text": {
                "content": "Loading..."
              }
            }
          ]
        }
      },
      {
        "fade_visibility": {
          "visible": "{isLoaded}",
          "duration": "300",
          "children": [
            {
              "text": {
                "content": "Content loaded!"
              }
            }
          ]
        }
      }
    ]
  }
}
```

### Accordion Menu

Create expandable menu items:

```json
{
  "column": {
    "children": [
      {
        "button": {
          "content": "Menu Section 1",
          "clickId": "toggle_section1"
        }
      },
      {
        "animated_visibility": {
          "visible": "{section1Expanded}",
          "children": [
            {
              "column": {
                "children": [
                  {
                    "text": {
                      "content": "Item 1"
                    }
                  },
                  {
                    "text": {
                      "content": "Item 2"
                    }
                  }
                ]
              }
            }
          ]
        }
      }
    ]
  }
}
```

## Troubleshooting

**Animation not working?**
- Verify `AnimationSupport.register()` is called in `LaunchedEffect`
- Check that bind values are strings ("true"/"false", not boolean)
- Make sure you're updating bindsValue when state changes

**Children not appearing?**
- Wrap children in a Column or Box in the AnimatedVisibility
- Ensure you're calling DynamicLayout for each child
- Pass param.bindsValue to child DynamicLayout calls

## Learn More

- [Full Animation Documentation](../03-json-structure/08-animations.md)
- [Implementing Custom Animations](06-implementing-animations.md)
- [Custom Nodes Guide](05a-custom-node.md)

## What's Next?

- Add more complex animations (scale, rotate, etc.)
- Implement AnimatedContent for state transitions
- Create animated loading skeletons
- Build animated onboarding flows

Happy animating! 🎉
