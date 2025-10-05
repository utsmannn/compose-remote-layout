# Animation Support - Quick Demo

## 🚀 Get Started in 3 Steps

### Step 1: Register Animations (Copy & Paste)

```kotlin
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import com.utsman.composeremote.CustomNodes
import com.utsman.composeremote.DynamicLayout

// In your app initialization
LaunchedEffect(Unit) {
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
```

### Step 2: Define Animation in JSON

```json
{
  "column": {
    "modifier": {
      "base": {
        "fillMaxWidth": true,
        "padding": { "all": 16 }
      }
    },
    "children": [
      {
        "button": {
          "content": "Toggle Content",
          "clickId": "toggle",
          "modifier": {
            "base": {
              "fillMaxWidth": true
            }
          }
        }
      },
      {
        "animated_visibility": {
          "visible": "{showContent}",
          "children": [
            {
              "card": {
                "modifier": {
                  "base": {
                    "fillMaxWidth": true,
                    "padding": { "all": 16 }
                  }
                },
                "children": [
                  {
                    "text": {
                      "content": "This content animates in and out! ✨",
                      "fontSize": 16
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

### Step 3: Control from Code

```kotlin
@Composable
fun MyScreen() {
    val bindsValue = remember { BindsValue() }
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(showContent) {
        bindsValue.setValue("showContent", showContent.toString())
    }
    
    val layoutJson = """{ ... your JSON above ... }"""
    val component = remember(layoutJson) {
        createLayoutComponent(layoutJson)
    }
    
    DynamicLayout(
        component = component,
        bindValue = bindsValue,
        onClickHandler = { clickId ->
            when (clickId) {
                "toggle" -> showContent = !showContent
            }
        }
    )
}
```

## 🎬 What You Get

When you tap the button:
- Content smoothly **fades in** while **expanding** from collapsed
- When you tap again, it **fades out** while **shrinking** back

## 🎨 More Animation Types

### Fade Only
```json
{
  "animated_visibility": {
    "visible": "{show}",
    "children": [...]
  }
}
```

### Slide In/Out
```json
{
  "animated_visibility": {
    "visible": "{show}",
    "enterType": "slideInVertically",
    "exitType": "slideOutVertically",
    "children": [...]
  }
}
```

### Scale Effect
```json
{
  "animated_visibility": {
    "visible": "{show}",
    "enterType": "scaleIn",
    "exitType": "scaleOut",
    "children": [...]
  }
}
```

## 📚 Learn More

- **Quick Start**: [docs/02-setup/06a-quick-start-animations.md](docs/02-setup/06a-quick-start-animations.md)
- **Full Guide**: [docs/03-json-structure/08-animations.md](docs/03-json-structure/08-animations.md)
- **Implementation**: [docs/02-setup/06-implementing-animations.md](docs/02-setup/06-implementing-animations.md)
- **Example Code**: [docs/examples/AnimationNodes.kt](docs/examples/AnimationNodes.kt)

## 🌟 Real-World Examples

### Expandable FAQ
```json
{
  "column": {
    "children": [
      {
        "button": {
          "content": "▶ What is Compose Remote Layout?",
          "clickId": "toggle_faq1"
        }
      },
      {
        "animated_visibility": {
          "visible": "{faq1Expanded}",
          "children": [
            {
              "text": {
                "content": "It's a library for building server-driven UIs with Jetpack Compose!"
              }
            }
          ]
        }
      }
    ]
  }
}
```

### Loading State
```json
{
  "box": {
    "modifier": {
      "base": { "fillMaxSize": true },
      "contentAlignment": "center"
    },
    "children": [
      {
        "animated_visibility": {
          "visible": "{isLoading}",
          "children": [
            { "text": { "content": "Loading..." } }
          ]
        }
      },
      {
        "animated_visibility": {
          "visible": "{isLoaded}",
          "children": [
            { "text": { "content": "Content ready! ✅" } }
          ]
        }
      }
    ]
  }
}
```

## 💡 Key Benefits

✅ **No App Updates Required** - Change animations remotely
✅ **A/B Test Animations** - Test different styles with different users
✅ **Consistent UX** - Define once, use everywhere
✅ **Easy to Customize** - Adjust duration, easing, and effects via JSON

## 🔧 Production Ready

The provided `AnimationNodes.kt` includes:
- ✅ All animation types
- ✅ Configurable options
- ✅ Safe defaults
- ✅ Performance optimized
- ✅ Well documented

Just copy it to your project and start using animations in your JSON layouts!

---

**Ready to add animations to your app?** Start with the [Quick Start Guide](docs/02-setup/06a-quick-start-animations.md)!
