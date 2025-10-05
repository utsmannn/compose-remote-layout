# Animations

Compose Remote Layout supports various animation capabilities through JSON configuration, allowing you to create dynamic and engaging user interfaces without app updates.

## Overview

Animations can be applied to components in several ways:

1. **AnimatedVisibility** - Show/hide components with enter/exit animations
2. **AnimatedContent** - Animate content changes with transitions
3. **Animated Modifiers** - Size, color, and position animations

## AnimatedVisibility

`AnimatedVisibility` allows you to show or hide components with smooth animations. This is perfect for toggling content, expanding/collapsing sections, or showing conditional UI elements.

### Basic Usage

```json
{
  "animated_visibility": {
    "visible": "true",
    "modifier": {
      "base": {
        "fillMaxWidth": true
      }
    },
    "children": [
      {
        "text": {
          "content": "This content fades in and out"
        }
      }
    ]
  }
}
```

### Properties

- `visible`: Controls visibility (accepts `"true"`, `"false"`, or bind values like `"{isVisible}"`)
- `enter`: Enter animation configuration (optional)
- `exit`: Exit animation configuration (optional)
- `children`: The content to show/hide

### Animation Types

#### Enter Animations

You can configure how components appear:

```json
{
  "animated_visibility": {
    "visible": "{showContent}",
    "enter": {
      "type": "fadeIn",
      "durationMillis": 300
    },
    "children": [
      {
        "text": {
          "content": "Fading in!"
        }
      }
    ]
  }
}
```

**Available enter animation types:**

- `fadeIn` - Fade in from transparent to opaque
- `slideInVertically` - Slide in from top or bottom
- `slideInHorizontally` - Slide in from left or right
- `expandVertically` - Expand from collapsed to full height
- `expandHorizontally` - Expand from collapsed to full width
- `scaleIn` - Scale from small to normal size

**Additional properties for enter animations:**

```json
{
  "enter": {
    "type": "slideInVertically",
    "durationMillis": 400,
    "delayMillis": 100,
    "initialOffsetY": "-100",
    "easing": "easeInOut"
  }
}
```

Properties:
- `durationMillis`: Animation duration in milliseconds (default: 300)
- `delayMillis`: Delay before animation starts (default: 0)
- `initialOffsetY`: Starting offset for slide animations (percentage or pixels)
- `initialOffsetX`: Starting offset for horizontal animations
- `easing`: Easing function (`"linear"`, `"easeIn"`, `"easeOut"`, `"easeInOut"`)

#### Exit Animations

Configure how components disappear:

```json
{
  "animated_visibility": {
    "visible": "{showPanel}",
    "exit": {
      "type": "slideOutVertically",
      "durationMillis": 250,
      "targetOffsetY": "100"
    },
    "children": [
      {
        "card": {
          "children": [
            {
              "text": {
                "content": "This slides out when hidden"
              }
            }
          ]
        }
      }
    ]
  }
}
```

**Available exit animation types:**

- `fadeOut` - Fade out from opaque to transparent
- `slideOutVertically` - Slide out to top or bottom
- `slideOutHorizontally` - Slide out to left or right
- `shrinkVertically` - Shrink from full height to collapsed
- `shrinkHorizontally` - Shrink from full width to collapsed
- `scaleOut` - Scale from normal to small size

#### Combined Enter and Exit

You can combine different enter and exit animations:

```json
{
  "animated_visibility": {
    "visible": "{menuOpen}",
    "enter": {
      "type": "expandVertically",
      "durationMillis": 300
    },
    "exit": {
      "type": "shrinkVertically",
      "durationMillis": 300
    },
    "children": [
      {
        "column": {
          "children": [
            {
              "text": {
                "content": "Menu Item 1"
              }
            },
            {
              "text": {
                "content": "Menu Item 2"
              }
            }
          ]
        }
      }
    ]
  }
}
```

## AnimatedContent

`AnimatedContent` automatically animates when its content changes. This is useful for transitioning between different states or values.

### Basic Usage

```json
{
  "animated_content": {
    "targetState": "{currentStep}",
    "modifier": {
      "base": {
        "fillMaxWidth": true,
        "padding": {
          "all": 16
        }
      }
    },
    "transitionSpec": {
      "type": "slideInOut",
      "durationMillis": 400
    },
    "content": {
      "1": {
        "text": {
          "content": "Step 1: Welcome"
        }
      },
      "2": {
        "text": {
          "content": "Step 2: Configure"
        }
      },
      "3": {
        "text": {
          "content": "Step 3: Complete"
        }
      }
    }
  }
}
```

### Properties

- `targetState`: The current state value (usually a bind value like `"{currentTab}"`)
- `transitionSpec`: Configuration for the transition animation
- `content`: Map of state values to their corresponding layouts
- `label`: Optional label for the animation (for debugging)

### Transition Types

**Fade Transition:**

```json
{
  "transitionSpec": {
    "type": "fade",
    "durationMillis": 300
  }
}
```

**Slide Transition:**

```json
{
  "transitionSpec": {
    "type": "slideInOut",
    "direction": "left",
    "durationMillis": 350
  }
}
```

Available directions: `"left"`, `"right"`, `"up"`, `"down"`

**Scale Transition:**

```json
{
  "transitionSpec": {
    "type": "scale",
    "durationMillis": 300,
    "initialScale": 0.8
  }
}
```

**Custom Combined Transition:**

```json
{
  "transitionSpec": {
    "type": "fadeWithSlide",
    "direction": "up",
    "durationMillis": 400,
    "slideOffset": 50
  }
}
```

## Animated Modifiers

Certain modifiers can be animated automatically when their values change.

### Animated Size

Use `animateContentSize` to smoothly animate size changes:

```json
{
  "column": {
    "modifier": {
      "base": {
        "fillMaxWidth": true,
        "animateContentSize": {
          "enabled": true,
          "durationMillis": 300,
          "easing": "easeInOut"
        }
      }
    },
    "children": [
      {
        "text": {
          "content": "{dynamicContent}"
        }
      }
    ]
  }
}
```

Properties:
- `enabled`: Whether to enable content size animation (default: true)
- `durationMillis`: Animation duration (default: 300)
- `easing`: Easing function (default: "easeInOut")

### Animated Background Color

Animate background color changes:

```json
{
  "box": {
    "modifier": {
      "base": {
        "size": 100,
        "background": {
          "color": "{dynamicColor}",
          "animated": true,
          "animationDuration": 500
        }
      }
    }
  }
}
```

## Practical Examples

### Expandable Section

Create an expandable section that reveals content when clicked:

```json
{
  "column": {
    "modifier": {
      "base": {
        "fillMaxWidth": true
      }
    },
    "children": [
      {
        "row": {
          "modifier": {
            "base": {
              "fillMaxWidth": true,
              "padding": {
                "all": 16
              },
              "clickId": "toggle_section"
            }
          },
          "children": [
            {
              "text": {
                "content": "Tap to expand",
                "fontWeight": "bold"
              }
            }
          ]
        }
      },
      {
        "animated_visibility": {
          "visible": "{isExpanded}",
          "enter": {
            "type": "expandVertically",
            "durationMillis": 300
          },
          "exit": {
            "type": "shrinkVertically",
            "durationMillis": 300
          },
          "children": [
            {
              "column": {
                "modifier": {
                  "base": {
                    "padding": {
                      "all": 16
                    }
                  }
                },
                "children": [
                  {
                    "text": {
                      "content": "Hidden content revealed!"
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

### Tab Navigation with Transitions

Animate between different tabs:

```json
{
  "column": {
    "modifier": {
      "base": {
        "fillMaxWidth": true
      }
    },
    "children": [
      {
        "row": {
          "modifier": {
            "base": {
              "fillMaxWidth": true,
              "padding": {
                "all": 8
              }
            }
          },
          "children": [
            {
              "button": {
                "content": "Home",
                "clickId": "nav_home",
                "modifier": {
                  "base": {
                    "weight": 1
                  }
                }
              }
            },
            {
              "button": {
                "content": "Profile",
                "clickId": "nav_profile",
                "modifier": {
                  "base": {
                    "weight": 1
                  }
                }
              }
            }
          ]
        }
      },
      {
        "animated_content": {
          "targetState": "{currentTab}",
          "transitionSpec": {
            "type": "slideInOut",
            "direction": "left",
            "durationMillis": 300
          },
          "content": {
            "home": {
              "text": {
                "content": "Home Screen"
              }
            },
            "profile": {
              "text": {
                "content": "Profile Screen"
              }
            }
          }
        }
      }
    ]
  }
}
```

### Loading State with Fade

Show loading state with smooth transitions:

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
        "animated_visibility": {
          "visible": "{isLoading}",
          "enter": {
            "type": "fadeIn",
            "durationMillis": 200
          },
          "exit": {
            "type": "fadeOut",
            "durationMillis": 200
          },
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
        "animated_visibility": {
          "visible": "{isLoaded}",
          "enter": {
            "type": "fadeIn",
            "durationMillis": 300,
            "delayMillis": 150
          },
          "children": [
            {
              "column": {
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
    ]
  }
}
```

### Notification Badge with Scale

Animate a notification badge appearing:

```json
{
  "box": {
    "modifier": {
      "base": {
        "size": 48
      },
      "contentAlignment": "topEnd"
    },
    "children": [
      {
        "text": {
          "content": "🔔"
        }
      },
      {
        "animated_visibility": {
          "visible": "{hasNotifications}",
          "enter": {
            "type": "scaleIn",
            "durationMillis": 200
          },
          "exit": {
            "type": "scaleOut",
            "durationMillis": 150
          },
          "children": [
            {
              "box": {
                "modifier": {
                  "base": {
                    "size": 16,
                    "background": {
                      "color": "#FF0000",
                      "shape": "circle"
                    }
                  },
                  "contentAlignment": "center"
                },
                "children": [
                  {
                    "text": {
                      "content": "{notificationCount}",
                      "fontSize": 10,
                      "color": "#FFFFFF"
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

## Best Practices

### 1. Keep Animation Durations Consistent

Use consistent animation durations throughout your app for a cohesive feel:

- Fast interactions: 150-200ms
- Standard transitions: 250-350ms
- Complex animations: 400-500ms

### 2. Match Enter and Exit Animations

For a natural feel, use complementary enter and exit animations:

```json
{
  "enter": {
    "type": "slideInVertically",
    "initialOffsetY": "-100"
  },
  "exit": {
    "type": "slideOutVertically",
    "targetOffsetY": "-100"
  }
}
```

### 3. Use Delays Strategically

Add small delays to create staggered animations:

```json
{
  "animated_visibility": {
    "visible": "{show}",
    "enter": {
      "type": "fadeIn",
      "durationMillis": 300,
      "delayMillis": 100
    }
  }
}
```

### 4. Consider Performance

- Avoid animating too many elements simultaneously
- Use simpler animations for complex layouts
- Test animations on lower-end devices

### 5. Bind to Dynamic Values

Leverage bind values to control animations from your app:

```kotlin
val bindsValue = BindsValue()
bindsValue.setValue("isExpanded", "false")
bindsValue.setValue("currentTab", "home")
bindsValue.setValue("showModal", "true")
```

## Controlling Animations from Code

You can control animations by updating bind values:

```kotlin
// Toggle visibility
val isVisible = remember { mutableStateOf(false) }
val bindsValue = remember { BindsValue() }

// Update bind value to trigger animation
LaunchedEffect(isVisible.value) {
    bindsValue.setValue("menuVisible", isVisible.value.toString())
}

DynamicLayout(
    component = component,
    bindValue = bindsValue
)
```

## Animation Easing Functions

Available easing functions for smooth animations:

- `linear` - Constant speed throughout
- `easeIn` - Slow start, fast end
- `easeOut` - Fast start, slow end
- `easeInOut` - Slow start and end, fast middle
- `easeInOutCubic` - Smooth cubic curve
- `easeInBack` - Slight overshoot at start
- `easeOutBack` - Slight overshoot at end
- `fastOutSlowIn` - Material Design standard

## Next Steps

Now that you understand animations, you can:

1. Create engaging, dynamic UIs without app updates
2. Implement smooth transitions between states
3. Build interactive components with visual feedback
4. Enhance user experience with thoughtful animations

For more information on other features, see:
- [Layout JSON Structure](06-layout-json-structure.md)
- [Custom Nodes](../02-setup/05a-custom-node.md)
- [Value Binding](../02-setup/04-bind-values.md)
