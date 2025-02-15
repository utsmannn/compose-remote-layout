require.config({
    paths: {
        'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs'
    }
});

require(['vs/editor/editor.main'], async function () {
    // Custom schema definition remains the same
    const customSchema = {
        // ... (schema definition stays the same)
    };

    // Register schema
    monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
        validate: true,
        schemas: [customSchema],
        enableSchemaRequest: true
    });

    // Create editor instance
    var editor = monaco.editor.create(document.getElementById('editor'), {
        language: 'json',
        theme: 'vs-dark',
        automaticLayout: true,
        formatOnPaste: true,
        formatOnType: true,
        tabSize: 4
    });

    // Initial JSON content
    const initialValue = `{
  "column": {
    "modifier": {
      "base": {
        "width": 200,
        "padding": {
          "all": 16
        }
      },
      "verticalArrangement": "spaceBetween",
      "horizontalAlignment": "center"
    },
    "children": [
      {
        "button": {
          "content": "Click me",
          "clickId": "button1",
          "modifier": {
            "base": {
              "fillMaxWidth": true
            }
          }
        }
      },
      {
        "text": {
          "content": "Hello World",
          "modifier": {
            "base": {
              "padding": {
                "top": 8
              }
            }
          }
        }
      }
    ]
  }
}`;

    // Set the value after 300ms delay
    setTimeout(() => {
        editor.setValue(initialValue);
    }, 300);

    // Change event handler
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