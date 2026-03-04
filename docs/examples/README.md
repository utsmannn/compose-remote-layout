# Animation Examples

This directory contains example implementations for adding animation support to Compose Remote Layout.

## Files

### AnimationNodes.kt

A complete, production-ready implementation of animation custom nodes. This file provides:

- **AnimatedVisibility** - Show/hide animations with configurable enter/exit transitions
- **FadeVisibility** - Simple fade in/out animations
- **SlideVisibility** - Slide animations (vertical or horizontal)
- **AnimatedSizeBox** - Animated content size changes

## How to Use

1. Copy `AnimationNodes.kt` to your project
2. Register the animations at app startup:

```kotlin
@Composable
fun App() {
    LaunchedEffect(Unit) {
        AnimationNodes.registerAll()
    }
    
    // Your app content
}
```

3. Use in your JSON layouts:

```json
{
  "animated_visibility": {
    "visible": "{isExpanded}",
    "enterType": "expandVertically",
    "exitType": "shrinkVertically",
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

## Customization

Feel free to modify the code to suit your needs:

- Add new animation types
- Change default durations
- Customize easing functions
- Add platform-specific optimizations

## Documentation

For comprehensive documentation, see:
- [Animation Guide](../03-json-structure/08-animations.md)
- [Implementation Guide](../02-setup/06-implementing-animations.md)
- [Quick Start](../02-setup/06a-quick-start-animations.md)

## License

This example code is provided as-is for use in your projects. Feel free to modify and distribute as needed.
