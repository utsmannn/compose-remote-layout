# Compose Remote Layout

Compose Remote Layout is a Kotlin Multiplatform library that enables you to dynamically update your
Compose UI by fetching layout definitions from remote sources. This allows for real-time UI changes
without requiring app updates, offering unprecedented flexibility and control.

## Features

1. **Dynamic UI Updates** <br>Modify your application's UI on-the-fly without needing to release new
   versions.

2. **A/B Testing** <br>Conduct UI experiments and optimize user experiences by serving different
   layouts to different user segments.

3. **Personalization** <br>Tailor the UI based on user preferences, demographics, or other dynamic
   criteria.

4. **Custom Node Support** <br>Integrate your own Composable functions into remote layouts,
   extending the library's capabilities.

5. **Centralized UI Management** <br>Manage UI layouts from a single, remote source, ensuring
   consistency across platforms.

6. **JSON Schema Validation** <br>Benefit from built-in schema validation to ensure that your layout
   definitions are well-formed and error-free.

7. **Data Binding** <br>Update values from remote by using BindsValue.

## Download

Add the following dependency to your `build.gradle.kts`:

```kotlin
// Kotlin Multiplatform
implementation("io.github.utsmannn:compose-remote-layout:0.0.1-alpha01")

// Platform-specific (choose one or more as needed):
implementation("io.github.utsmannn:compose-remote-layout-android:0.0.1-alpha01")
implementation("io.github.utsmannn:compose-remote-layout-jvm:0.0.1-alpha01")
implementation("io.github.utsmannn:compose-remote-layout-iosx64:0.0.1-alpha01") // or iosarm64
implementation("io.github.utsmannn:compose-remote-layout-iosarm64:0.0.1-alpha01")
implementation("io.github.utsmannn:compose-remote-layout-js:0.0.1-alpha01")
```

## Basic Usage

1. **Fetch Layout JSON** <br>Retrieve the JSON layout definition from your remote source (e.g., a
   web server, Firebase Remote Config, or a local file).

2. **Parse the JSON** <br>Use `LayoutParser.parseLayoutJson()` to convert the JSON string into a
   `LayoutComponent`.

3. **Render the UI** <br>Use the `DynamicLayout` composable function to display the UI based on the
   `LayoutComponent`.

```kotlin
import androidx.compose.runtime.Composable
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser

@Composable
fun MyComposable(layoutJson: String?) {
    val layoutComponent = layoutJson?.let { LayoutParser.parseLayoutJson(it) }
    DynamicLayout(component = layoutComponent)
}
```

## Custom Nodes: Extending the UI Vocabulary

Custom nodes allow you to integrate your own Composables into the remotely defined layouts.

1. **Register a Custom Node** <br>Create a Composable function and register it using
   `CustomNodes.register()`, providing a unique type name.

   ```kotlin
   import androidx.compose.material.Text
   import androidx.compose.runtime.Composable
   import androidx.compose.ui.Modifier
   import com.utsman.composeremote.CustomNodes
   import com.utsman.composeremote.CustomNodes.NodeParam

   init {
       CustomNodes.register("my_banner") { param: NodeParam ->
           Text(text = param.data["text"] ?: "Default Banner Text", modifier = param.modifier)
       }
   }
   ```

2. **Use in JSON** <br>Reference the custom node in your JSON layout, providing the type name and
   any necessary data within the `data` object.

   ```json
   {
     "custom": {
       "type": "my_banner",
       "data": {
         "text": "Dynamic Banner Content!"
       },
       "modifier": {
         "base": {
           "padding": { "all": 16 }
         }
       }
     }
   }
   ```

## Why `base` in Modifiers?

The `base` property within modifiers groups core layout properties (width, padding, etc.) for
improved organization, clarity, and future extensibility. It separates core properties from scope
specific properties.

## JSON Schema

<details>
<summary>JSON Schema (Click to expand)</summary>

```json
{
  "uri": "http://remote-web/ui-components",
  "fileMatch": [
    "*"
  ],
  "schema": {
    "type": "object",
    "definitions": {
      "padding": {
        "type": "object",
        "properties": {
          "all": {
            "type": "number"
          },
          "top": {
            "type": "number"
          },
          "bottom": {
            "type": "number"
          },
          "left": {
            "type": "number"
          },
          "right": {
            "type": "number"
          }
        }
      },
      "margin": {
        "type": "object",
        "properties": {
          "all": {
            "type": "number"
          },
          "top": {
            "type": "number"
          },
          "bottom": {
            "type": "number"
          },
          "left": {
            "type": "number"
          },
          "right": {
            "type": "number"
          }
        }
      },
      "background": {
        "type": "object",
        "properties": {
          "color": {
            "type": "string",
            "pattern": "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
          },
          "shape": {
            "type": "string",
            "enum": [
              "roundedcorner"
            ]
          },
          "radius": {
            "type": "number"
          }
        }
      },
      "shadow": {
        "type": "object",
        "properties": {
          "elevation": {
            "type": "number"
          },
          "shape": {
            "type": "object",
            "properties": {
              "type": {
                "type": "string",
                "enum": [
                  "roundedcorner"
                ]
              },
              "cornerRadius": {
                "type": "number"
              }
            },
            "required": [
              "type"
            ]
          }
        }
      },
      "baseModifier": {
        "type": "object",
        "properties": {
          "padding": {
            "$ref": "#/definitions/padding"
          },
          "margin": {
            "$ref": "#/definitions/margin"
          },
          "background": {
            "$ref": "#/definitions/background"
          },
          "shadow": {
            "$ref": "#/definitions/shadow"
          },
          "width": {
            "type": "number"
          },
          "height": {
            "type": "number"
          },
          "fillMaxWidth": {
            "type": "boolean"
          },
          "fillMaxHeight": {
            "type": "boolean"
          }
        }
      },
      "modifier": {
        "type": "object",
        "properties": {
          "base": {
            "$ref": "#/definitions/baseModifier"
          },
          "horizontalAlignment": {
            "type": "string",
            "enum": [
              "start",
              "center",
              "end"
            ]
          },
          "verticalAlignment": {
            "type": "string",
            "enum": [
              "top",
              "center",
              "bottom"
            ]
          },
          "horizontalArrangement": {
            "type": "string",
            "enum": [
              "start",
              "center",
              "end",
              "spaceBetween",
              "spaceAround",
              "spaceEvenly"
            ]
          },
          "verticalArrangement": {
            "type": "string",
            "enum": [
              "top",
              "center",
              "bottom",
              "spaceBetween",
              "spaceAround",
              "spaceEvenly"
            ]
          }
        }
      },
      "commonComponentProperties": {
        "type": "object",
        "properties": {
          "modifier": {
            "$ref": "#/definitions/modifier"
          },
          "children": {
            "type": "array",
            "items": {
              "$ref": "#/definitions/component"
            }
          }
        }
      },
      "component": {
        "type": "object",
        "properties": {
          "column": {
            "type": "object",
            "allOf": [
              {
                "$ref": "#/definitions/commonComponentProperties"
              }
            ]
          },
          "row": {
            "type": "object",
            "allOf": [
              {
                "$ref": "#/definitions/commonComponentProperties"
              }
            ]
          },
          "box": {
            "type": "object",
            "allOf": [
              {
                "$ref": "#/definitions/commonComponentProperties"
              }
            ]
          },
          "text": {
            "type": "object",
            "properties": {
              "content": {
                "type": "string"
              },
              "modifier": {
                "$ref": "#/definitions/modifier"
              }
            },
            "required": [
              "content"
            ]
          },
          "button": {
            "type": "object",
            "properties": {
              "content": {
                "type": "string"
              },
              "clickId": {
                "type": "string"
              },
              "modifier": {
                "$ref": "#/definitions/modifier"
              }
            },
            "required": [
              "content",
              "clickId"
            ]
          }
        },
        "additionalProperties": {
          "type": "object",
          "properties": {
            "modifier": {
              "$ref": "#/definitions/modifier"
            },
            "children": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/component"
              }
            }
          },
          "additionalProperties": true
        }
      }
    },
    "allOf": [
      {
        "$ref": "#/definitions/component"
      }
    ],
    "additionalProperties": true
  }
}
```

</details>

## How to Use the `remoteWeb` Module

1. **Build and Run** <br>Build the `remoteWeb` module using Gradle. This will generate the
   necessary JavaScript files and prepare the web application for deployment.

2. **Open `index.html` in a Web Browser:** <br>Launch a web browser and open the `index.html` file.
   This will load the `remoteWeb` interface, featuring the Monaco Editor and the Compose rendering
   area.

3. **Create or Edit JSON Layout Definitions:** <br>Utilize the Monaco Editor to create new JSON
   layout definitions or modify existing ones. The editor's syntax highlighting, autocompletion, and
   validation features will assist you in crafting well-formed and semantically correct JSON.

4. **Visualize Changes in Real-Time:** <br>As you make changes to the JSON within the Monaco Editor,
   the `remoteWeb` module will automatically transmit those changes to the Compose application. The
   application will then re-render the UI, allowing you to visualize the impact of your
   modifications in real-time.