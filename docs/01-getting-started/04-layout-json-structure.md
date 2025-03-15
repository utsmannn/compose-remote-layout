Compose Remote Layout uses a simple yet powerful JSON structure to define UI components. Here's a
breakdown of the basic structure:

```json
{
  "componentType": {
    "modifier": {
      "base": {
        // Base modifiers like width, height, padding, etc.
      }
      // Scoped modifiers specific to the component type
    },
    "property1": "value1",
    "property2": "value2",
    "children": [
      // Child components (for container components)
    ]
  }
}
```

The top-level key (`componentType`) defines the type of component to render. Built-in component
types include:

- `column` - Vertical arrangement of children
- `row` - Horizontal arrangement of children
- `box` - Overlay arrangement of children
- `grid` - Grid arrangement of children
- `text` - Text display
- `button` - Clickable button
- `card` - Material card component
- `spacer` - Empty space

Each component type has its own set of properties and can have specific scoped modifiers.
