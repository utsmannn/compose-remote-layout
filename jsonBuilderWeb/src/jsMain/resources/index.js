require.config({
    paths: {
        'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs'
    }
});

require(['vs/editor/editor.main'], async function () {
    // Fetch the schema file
    const schemaResponse = await fetch('schema.json');
    const customSchema = await schemaResponse.json();

    // Set the schema URI
    const schemaWithUri = {
        uri: "http://remote-web/ui-components",
        fileMatch: ["*"],
        schema: customSchema
    };

    // Set the schema in Monaco Editor
    monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
        validate: true,
        schemas: [schemaWithUri],
        enableSchemaRequest: true
    });

    // Create editor instance
    var editor = monaco.editor.create(document.getElementById('editor'), {
        language: 'json',
        theme: 'vs-dark',
        automaticLayout: true,
        formatOnPaste: true,
        formatOnType: true,
        tabSize: 2
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

    const statusMessage = document.getElementById('statusMessage');
    const loadingBar = document.getElementById('loadingBar');
    const progressBar = loadingBar.querySelector('.progress');

    function showStatus(message, isError = false) {
        statusMessage.textContent = message;
        statusMessage.style.color = isError ? '#ff4444' : '#e0e0e0';
        statusMessage.classList.add('show');
        setTimeout(() => {
            statusMessage.classList.remove('show');
        }, 3000);
    }

    document.getElementById('formatBtn').addEventListener('click', function() {
        try {
            editor.getAction('editor.action.formatDocument').run();
            showStatus('JSON formatted successfully');
        } catch (error) {
            showStatus('Invalid JSON: ' + error.message, true);
        }
    });

    document.getElementById('saveToRemoteConfig').addEventListener('click', async function() {
        try {
            const editorValue = editor.getValue();

            // Show loading bar
            loadingBar.style.display = 'block';
            progressBar.style.width = '30%';

            const response = await fetch('http://localhost:3000/parameter?key=layout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: editorValue
            });

            progressBar.style.width = '100%';

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            showStatus('Successfully saved to Remote Config');

            // Hide loading bar after animation
            setTimeout(() => {
                loadingBar.style.display = 'none';
                progressBar.style.width = '0%';
            }, 500);
        } catch (error) {
            showStatus('Error saving to Remote Config: ' + error.message, true);
            loadingBar.style.display = 'none';
            progressBar.style.width = '0%';
        }
    });

    // Change event handler
    editor.onDidChangeModelContent(function(e) {
        var newValue = editor.getValue();
        document.getElementById('text_container').textContent = newValue;

        if (typeof jsonBuilderWeb !== 'undefined' && typeof jsonBuilderWeb.updateEditorContent === 'function') {
            jsonBuilderWeb.updateEditorContent(newValue);
        } else {
            console.log('remoteWeb not loaded yet');
        }
    });
});