require.config({
        paths: {
            'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs'
        }
    });

    require(['vs/editor/editor.main'], async function () {
        // Mendefinisikan schema JSON kustom

        const customSchema = {
            uri: "compose-remote-schema",
            fileMatch: ["*"],
            schema: {
                type: "object",
                properties: {
                    type: {
                        type: "string",
                        enum: ["column", "row", "box", "text", "button", "card"],
                        description: "Type of component"
                    },
                    text: {
                        type: "string",
                        description: "Text content for text and button components"
                    },
                    clickId: {
                        type: "string",
                        description: "Identifier for click events"
                    },
                    modifier: {
                        type: "object",
                        properties: {
                            base: {
                                type: "object",
                                properties: {
                                    width: {
                                        type: "integer",
                                        description: "Width in dp"
                                    },
                                    height: {
                                        type: "integer",
                                        description: "Height in dp"
                                    },
                                    fillMaxWidth: {
                                        type: "boolean",
                                        description: "Fill maximum width"
                                    },
                                    fillMaxHeight: {
                                        type: "boolean",
                                        description: "Fill maximum height"
                                    },
                                    scrollable: {
                                        type: "boolean",
                                        description: "Enable scrolling"
                                    },
                                    clickable: {
                                        type: "boolean",
                                        description: "Make component clickable"
                                    },
                                    padding: {
                                        oneOf: [
                                            {
                                                type: "integer",
                                                description: "Padding in dp"
                                            },
                                            {
                                                type: "object",
                                                properties: {
                                                    all: {
                                                        type: "integer",
                                                        description: "Padding for all sides"
                                                    },
                                                    horizontal: {
                                                        type: "integer",
                                                        description: "Horizontal padding"
                                                    },
                                                    vertical: {
                                                        type: "integer",
                                                        description: "Vertical padding"
                                                    }
                                                }
                                            }
                                        ]
                                    },
                                    background: {
                                        type: "object",
                                        properties: {
                                            color: {
                                                type: "string",
                                                description: "Background color in hex format"
                                            },
                                            alpha: {
                                                type: "number",
                                                description: "Background opacity (0.0 to 1.0)"
                                            }
                                        }
                                    },
                                    shape: {
                                        type: "object",
                                        properties: {
                                            type: {
                                                type: "string",
                                                enum: ["rectangle", "roundedCorner", "circle"],
                                                description: "Shape type"
                                            },
                                            cornerRadius: {
                                                type: "integer",
                                                description: "Corner radius for rounded corners"
                                            }
                                        }
                                    }
                                }
                            },
                            verticalArrangement: {
                                type: "string",
                                enum: ["top", "bottom", "center", "spaceBetween", "spaceAround", "spaceEvenly"],
                                description: "Vertical arrangement for Column"
                            },
                            horizontalAlignment: {
                                type: "string",
                                enum: ["start", "end", "center"],
                                description: "Horizontal alignment for Column"
                            },
                            horizontalArrangement: {
                                type: "string",
                                enum: ["start", "end", "center", "spaceBetween", "spaceAround", "spaceEvenly"],
                                description: "Horizontal arrangement for Row"
                            },
                            verticalAlignment: {
                                type: "string",
                                enum: ["top", "bottom", "center"],
                                description: "Vertical alignment for Row"
                            },
                            contentAlignment: {
                                type: "string",
                                enum: ["center", "topStart", "topCenter", "topEnd", "centerStart", "centerEnd", "bottomStart", "bottomCenter", "bottomEnd"],
                                description: "Content alignment for Box"
                            }
                        }
                    },
                    children: {
                        type: "array",
                        description: "Child components",
                        items: {
                            $schema: "http://json-schema.org/draft-07/schema#",
                            $ref: "#"
                        }
                    }
                },
                required: ["type"]
            }
        };

        // Mendaftarkan schema
        monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
            validate: true,
            schemas: [customSchema],
            enableSchemaRequest: true
        });

        // Membuat editor dengan konfigurasi
        var editor = monaco.editor.create(document.getElementById('editor'), {
            value: `{
    "type": "column",
    "modifier": {
        "padding": 16,
        "fillMaxWidth": true,
        "scrollable": true
    },
    "children": [
        {
            "type": "button",
            "text": "Hello, Compose!"
        },
        {
            "type": "text",
            "text": "Hello, Compose!"
        }
    ]
}`,
            language: 'json',
            theme: 'vs-dark',
            automaticLayout: true,
            formatOnPaste: true,
            formatOnType: true,
            tabSize: 4
        });

        editor.onDidChangeModelContent(function(e) {
            var newValue = editor.getValue();
            document.getElementById('text_container').textContent = newValue;

            if (typeof remoteWeb !== 'undefined' && typeof remoteWeb.updateEditorContent === 'function') {
                remoteWeb.updateEditorContent(newValue);
            } else {
                console.log('remoteWeb not loaded yet');
            }
        });
    });