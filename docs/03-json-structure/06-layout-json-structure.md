Compose Remote Layout uses a well-defined JSON structure to represent UI components. This guide
provides a comprehensive reference for the JSON format and how it maps to Compose UI elements.

## JSON Structure Overview

Every component in a Compose Remote Layout follows this basic pattern:

```json
{
  "componentType": {
    "modifier": {
      "base": {
        // Base modifiers common to all components
      }
      // Component-specific modifiers
    },
    // Component-specific properties
    "children": [
      // Child components (for container components)
    ]
  }
}
```

The root of any layout must be a single component, identified by the top-level key (like `"column"`,
`"row"`, etc.).

## Component Types Reference

Compose Remote Layout supports the following built-in component types:

### Container Components

These components can contain other components as children.

#### Column

Creates a vertical arrangement of children elements, similar to Compose's `Column`.

```json
{
  "column": {
    "modifier": {
      "base": {
        "fillMaxWidth": true,
        "padding": {
          "all": 16
        }
      },
      "verticalArrangement": "spaceBetween",
      "horizontalAlignment": "center"
    },
    "children": [
      // Child components
    ]
  }
}
```

**Specific Modifiers:**

- `verticalArrangement`: Controls spacing of children (`"top"`, `"center"`, `"bottom"`,
  `"spaceBetween"`, `"spaceAround"`, `"spaceEvenly"`)
- `horizontalAlignment`: Aligns children horizontally (`"start"`, `"center"`, `"end"`)

#### Row

Creates a horizontal arrangement of children elements, similar to Compose's `Row`.

```json
{
  "row": {
    "modifier": {
      "base": {
        "fillMaxWidth": true
      },
      "horizontalArrangement": "spaceBetween",
      "verticalAlignment": "center"
    },
    "children": [
      // Child components
    ]
  }
}
```

**Specific Modifiers:**

- `horizontalArrangement`: Controls spacing of children (`"start"`, `"center"`, `"end"`,
  `"spaceBetween"`, `"spaceAround"`, `"spaceEvenly"`)
- `verticalAlignment`: Aligns children vertically (`"top"`, `"center"`, `"bottom"`)

#### Box

Overlays children on top of each other, similar to Compose's `Box`.

```json
{
  "box": {
    "modifier": {
      "base": {
        "size": 200
      },
      "contentAlignment": "center"
    },
    "children": [
      // Child components - first child at the bottom, last child on top
    ]
  }
}
```

**Specific Modifiers:**

- `contentAlignment`: Positions children within the box (`"center"`, `"topStart"`, `"topCenter"`,
  `"topEnd"`, `"centerStart"`, `"centerEnd"`, `"bottomStart"`, `"bottomCenter"`, `"bottomEnd"`)

#### Grid

Arranges children in a grid pattern.

```json
{
  "grid": {
    "modifier": {
      "base": {
        "fillMaxWidth": true,
        "scrollable": true
      },
      "columns": 3,
      "rows": 2,
      "orientation": "vertical",
      "horizontalArrangement": "spaceBetween",
      "verticalArrangement": "center",
      "enableSnapHorizontal": false
    },
    "children": [
      // Child components
    ]
  }
}
```

**Specific Modifiers:**

- `columns`: Number of columns (for vertical orientation)
- `rows`: Number of rows (for horizontal orientation)
- `orientation`: Direction of the grid (`"vertical"` or `"horizontal"`)
- `horizontalArrangement`: Controls horizontal spacing
- `verticalArrangement`: Controls vertical spacing
- `enableSnapHorizontal`: Enables snap scrolling for horizontal grids

#### Card

Creates a Material Design card that can contain children.

```json
{
  "card": {
    "modifier": {
      "base": {
        "padding": {
          "all": 16
        }
      }
    },
    "children": [
      // Child components
    ]
  }
}
```

### Content Components

These components typically display content rather than containing other components.

#### Text

Displays text content with various styling options.

```json
{
  "text": {
    "content": "Hello World",
    "fontSize": 16,
    "fontWeight": "bold",
    "color": "#0066CC",
    "textAlign": "center",
    "fontStyle": "normal",
    "letterSpacing": 0,
    "lineHeight": 20,
    "maxLines": 2,
    "minLines": 1,
    "overflow": "ellipsis",
    "textDecoration": "underline"
  }
}
```

**Properties:**

- `content`: The text to display (supports bind values with `{key}` syntax)
- `fontSize`: Font size in sp
- `fontWeight`: Font weight (`"thin"`, `"extralight"`, `"light"`, `"normal"`, `"medium"`,
  `"semibold"`, `"bold"`, `"extrabold"`, `"black"`, or numeric weights like `"w100"` through
  `"w900"`)
- `color`: Text color as a hex string
- `textAlign`: Text alignment (`"start"`, `"center"`, `"end"`, `"justify"`)
- `fontStyle`: Font style (`"normal"`, `"italic"`)
- `letterSpacing`: Additional space between letters in sp
- `lineHeight`: Line height in sp
- `maxLines`: Maximum number of lines before truncation
- `minLines`: Minimum number of lines (adds space if content is shorter)
- `overflow`: How to handle text overflow (`"clip"`, `"ellipsis"`, `"visible"`)
- `textDecoration`: Text decoration (`"none"`, `"underline"`, `"linethrough"`,
  `"underline linethrough"`)

#### Button

Creates an interactive button element.

```json
{
  "button": {
    "content": "Click Me",
    "clickId": "primary_button",
    "fontSize": 14,
    "fontWeight": "medium",
    "fontColor": "#FFFFFF"
  }
}
```

**Properties:**

- `content`: The button text (supports bind values)
- `clickId`: Identifier for click handling
- `fontSize`, `fontWeight`, `fontColor`: Text styling properties
- `letterSpacing`, `lineHeight`, `textAlign`, `textDecoration`, `maxLines`, `minLines`, `overflow`:
  Additional text styling properties

Buttons can also contain complex content using `children` instead of `content`:

```json
{
  "button": {
    "clickId": "complex_button",
    "children": [
      {
        "row": {
          "children": [
            {
              "text": {
                "content": "Submit"
              }
            }
          ]
        }
      }
    ]
  }
}
```

#### Spacer

Creates empty space with specified dimensions.

```json
{
  "spacer": {
    "height": 16,
    "width": 0
  }
}
```

**Properties:**

- `height`: Vertical space in dp
- `width`: Horizontal space in dp

### Custom Components

You can also use custom components that you've registered with `CustomNodes`:

```json
{
  "profile_card": {
    "username": "John Doe",
    "avatar_url": "https://example.com/avatar.jpg",
    "modifier": {
      "base": {
        "fillMaxWidth": true
      }
    },
    "children": [
      // Optional children depending on your custom component implementation
    ]
  }
}
```

The properties available for custom components depend on your implementation.

## Modifier System

The modifier system allows you to customize appearance and behavior of components.

### Base Modifiers

These modifiers are common to all components:

#### Size Modifiers

```json
"base": {
    "width": 200, // Fixed width in dp
    "height": 100, // Fixed height in dp
    "size": 150, // Both width and height set to the same value
    
    "fillMaxWidth": true, // Fill available width (equivalent to Modifier.fillMaxWidth())
    "fillMaxHeight": false, // Fill available height
    "fillMaxSize": false, // Fill available width and height
    
    "wrapContentWidth": true, // Size to fit content horizontally
    "wrapContentHeight": true, // Size to fit content vertically
    
    "aspectRatio": 1.5      // Maintain this width to height ratio
}
```

#### Padding and Margin

```json
"padding": {
    "all": 16, // Apply 16dp padding on all sides
    "horizontal": 16, // Left and right padding
    "vertical": 8, // Top and bottom padding
    "start": 16, // Start (left in LTR) padding
    "top": 8, // Top padding
    "end": 16, // End (right in LTR) padding
    "bottom": 8            // Bottom padding
},

"margin": {
    // Same properties as padding
    "all": 8,
    "horizontal": 8,
    "vertical": 4,
    "start": 8,
    "top": 4,
    "end": 8,
    "bottom": 4
}
```

#### Background and Styling

```json
"background": {
    "color": "#FF0000", // Background color as hex
    "alpha": 0.8, // Opacity (0.0 - 1.0)
    "shape": "roundedcorner", // Shape type ("rectangle", "roundedcorner", "circle")
    "radius": 8               // Corner radius for rounded corners
},
"border": {
    "width": 2, // Border width in dp
    "color": "#000000", // Border color as hex
    "shape": {
        "type": "roundedcorner",
        "cornerRadius": 8,   // Even corner radius
        "topStart": 8, // Individual corner radii
        "topEnd": 8,
        "bottomStart": 8,
        "bottomEnd": 8
    }
},
"shadow": {
    "elevation": 4, // Shadow elevation in dp
    "shape": {
      "type": "roundedcorner",
      "cornerRadius": 8
    }
}
```

#### Scrolling

```json
"scrollable": true  // Make this component scrollable
```

#### Interaction

```json
"clickId": "component_click"  // Component will be clickable with this ID
```

#### Transformations

```json
"alpha": 0.9, // Component opacity (0.0 - 1.0)
"rotate": 45, // Rotation in degrees
"scale": {
    "scaleX": 1.2, // Horizontal scale factor
    "scaleY": 0.8         // Vertical scale factor
},
"offset": {
    "x": 10, // Horizontal offset in dp
    "y": 5                // Vertical offset in dp
},
"clip": true            // Clip content to component bounds
```

## Complete Examples

### Simple Profile Card

Here's an example of a simple profile card:

```json
{
  "card": {
    "modifier": {
      "base": {
        "fillMaxWidth": true,
        "padding": {
          "all": 16
        },
        "clickId": "view_profile"
      }
    },
    "children": [
      {
        "row": {
          "modifier": {
            "verticalAlignment": "center"
          },
          "children": [
            {
              "box": {
                "modifier": {
                  "base": {
                    "size": 64,
                    "background": {
                      "color": "#EEEEEE",
                      "shape": "circle"
                    }
                  },
                  "contentAlignment": "center"
                },
                "children": [
                  {
                    "text": {
                      "content": "{initials}",
                      "fontSize": 24,
                      "fontWeight": "bold",
                      "color": "#666666"
                    }
                  }
                ]
              }
            },
            {
              "spacer": {
                "width": 16,
                "height": 0
              }
            },
            {
              "column": {
                "children": [
                  {
                    "text": {
                      "content": "{username}",
                      "fontSize": 18,
                      "fontWeight": "bold"
                    }
                  },
                  {
                    "text": {
                      "content": "{email}",
                      "fontSize": 14,
                      "color": "#666666"
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

### Interactive Counter

```json
{
  "column": {
    "modifier": {
      "base": {
        "fillMaxWidth": true,
        "padding": {
          "all": 24
        }
      },
      "horizontalAlignment": "center",
      "verticalArrangement": "spaceBetween"
    },
    "children": [
      {
        "text": {
          "content": "Counter",
          "fontSize": 24,
          "fontWeight": "bold"
        }
      },
      {
        "box": {
          "modifier": {
            "base": {
              "padding": {
                "vertical": 32
              }
            },
            "contentAlignment": "center"
          },
          "children": [
            {
              "text": {
                "content": "{count}",
                "fontSize": 64,
                "fontWeight": "bold",
                "color": "{countColor}"
              }
            }
          ]
        }
      },
      {
        "row": {
          "modifier": {
            "base": {
              "fillMaxWidth": true
            },
            "horizontalArrangement": "spaceEvenly"
          },
          "children": [
            {
              "button": {
                "content": "-",
                "clickId": "decrement",
                "fontSize": 20
              }
            },
            {
              "button": {
                "content": "+",
                "clickId": "increment",
                "fontSize": 20
              }
            }
          ]
        }
      },
      {
        "button": {
          "content": "Reset",
          "clickId": "reset",
          "modifier": {
            "base": {
              "fillMaxWidth": true,
              "padding": {
                "vertical": 8
              }
            }
          }
        }
      }
    ]
  }
}
```

### Scrollable Grid Layout

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
        "text": {
          "content": "Product Categories",
          "fontSize": 24,
          "fontWeight": "bold"
        }
      },
      {
        "spacer": {
          "height": 16
        }
      },
      {
        "grid": {
          "modifier": {
            "base": {
              "fillMaxWidth": true,
              "scrollable": true,
              "height": 120
            },
            "orientation": "horizontal",
            "rows": 1,
            "enableSnapHorizontal": true,
            "horizontalArrangement": "spaceBetween"
          },
          "children": [
            {
              "card": {
                "modifier": {
                  "base": {
                    "width": 180,
                    "height": 100,
                    "clickId": "category:electronics"
                  }
                },
                "children": [
                  {
                    "box": {
                      "modifier": {
                        "contentAlignment": "center"
                      },
                      "children": [
                        {
                          "text": {
                            "content": "Electronics",
                            "fontSize": 18,
                            "fontWeight": "medium"
                          }
                        }
                      ]
                    }
                  }
                ]
              }
            },
            {
              "card": {
                "modifier": {
                  "base": {
                    "width": 180,
                    "height": 100,
                    "clickId": "category:clothing"
                  }
                },
                "children": [
                  {
                    "box": {
                      "modifier": {
                        "contentAlignment": "center"
                      },
                      "children": [
                        {
                          "text": {
                            "content": "Clothing",
                            "fontSize": 18,
                            "fontWeight": "medium"
                          }
                        }
                      ]
                    }
                  }
                ]
              }
            },
            {
              "card": {
                "modifier": {
                  "base": {
                    "width": 180,
                    "height": 100,
                    "clickId": "category:home"
                  }
                },
                "children": [
                  {
                    "box": {
                      "modifier": {
                        "contentAlignment": "center"
                      },
                      "children": [
                        {
                          "text": {
                            "content": "Home",
                            "fontSize": 18,
                            "fontWeight": "medium"
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
    ]
  }
}
```

## Best Practices

### 1. Keep Layout JSON Clean and Readable

- Use consistent indentation
- Group related components together
- Add comments in your code when generating JSON
- Consider using a JSON formatter for better readability

### 2. Start Simple and Build Up Complexity

- Begin with simple layouts
- Test each component individually
- Gradually combine components into more complex layouts
- Validate layout rendering at each step

### 3. Reuse Common Structures

- Identify repeating patterns
- Extract common layouts into reusable functions
- Use template systems if generating JSON dynamically

### 4. Consider Performance

- Avoid deeply nested structures when possible
- Be mindful of the number of components in scrollable containers
- Use lazy loading patterns for large lists (custom components with LazyColumn)

### 5. Test on Multiple Devices

- Verify layouts on different screen sizes
- Ensure your responsive design works as expected
- Check behavior with different font sizes (accessibility)

## Troubleshooting Common Issues

### Problem: Component Not Displaying

**Possible causes:**

- Invalid JSON structure
- Missing required properties
- Nested structure too complex

**Solutions:**

- Validate JSON with a JSON validator
- Start with a simpler version and add complexity gradually
- Check for typos in component type names

### Problem: Layout Not Responding to Clicks

**Possible causes:**

- Missing `clickId` property
- Incorrect handler implementation

**Solutions:**

- Ensure buttons have the `clickId` property set
- For non-button components, add `clickId` to the base modifier
- Verify click handler function is implemented correctly

### Problem: Unexpected Layout Appearance

**Possible causes:**

- Conflicting modifiers
- Incorrect nesting of components
- Misunderstanding of component behavior

**Solutions:**

- Test individual components separately
- Review the documentation for specific components
- Use simpler alternatives if a component behaves unexpectedly

## Next Steps

Now that you understand the JSON structure for layouts, you can:

1. Try the [Live Editor JSON](../07-live-editor) for build and create your dynamic content