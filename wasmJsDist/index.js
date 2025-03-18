

require.config({
    paths: {
        'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs'
    }
});

require(['vs/editor/editor.main'], async function () {
    const defaultValue = {
      "column": {
        "modifier": {
          "base": {
            "padding": {
              "all": 12
            }
          }
        },
        "children": [
          {
            "text": {
              "content": "Hello from Compose"
            }
          },
          {
            "button": {
              "content": "Click Me"
            }
          }
        ]
      }
    };

    // Load schema and create editor
    const editor = await initializeEditor();

    // DOM Elements
    const statusMessage = document.getElementById('statusMessage');
    const loadingBar = document.getElementById('loadingBar');
    const progressBar = loadingBar.querySelector('.progress');

    // Initialize event listeners
    initializeEventListeners();

    setTimeout(() => {
        console.log("Setting editor value after 1-second delay");
        // Set default value with delay
        editor.setValue(JSON.stringify(defaultValue, null, 2));
        // Update editor with delay
        updateEditor(JSON.stringify(defaultValue, null, 2));
    }, 1000);

    async function initializeEditor() {
        try {
            // Fetch schema (still loading schema locally)
            const schemaResponse = await fetch('schema.json');
            const customSchema = await schemaResponse.json();

            // Configure schema
            const schemaWithUri = {
                uri: "http://local-web/ui-components",
                fileMatch: ["*"],
                schema: customSchema
            };

            // Set Monaco validation options
            monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
                validate: true,
                schemas: [schemaWithUri],
                enableSchemaRequest: true
            });
        } catch (error) {
            console.error('Error loading schema:', error);
            // Continue without schema validation if schema fails to load
            showStatus('Schema validation unavailable', true);
        }

        // Create editor instance
        const editor = monaco.editor.create(document.getElementById('editor'), {
            language: 'json',
            theme: 'vs-dark',
            automaticLayout: true,
            formatOnPaste: true,
            formatOnType: true,
            tabSize: 2
        });

        return editor;
    }

    function initializeEventListeners() {
        // Format JSON button
        document.getElementById('formatBtn').addEventListener('click', formatJson);

        // Editor change handler
        editor.onDidChangeModelContent((e) => {
            updateEditor(editor.getValue());
        });
    }

    function formatJson() {
        try {
            editor.getAction('editor.action.formatDocument').run();
            showStatus('JSON formatted successfully');
        } catch (error) {
            showStatus('Invalid JSON: ' + error.message, true);
        }
    }

    function showStatus(message, isError = false) {
        statusMessage.textContent = message;
        statusMessage.style.color = isError ? '#ff4444' : '#e0e0e0';
        statusMessage.classList.add('show');
        setTimeout(() => {
            statusMessage.classList.remove('show');
        }, 3000);
    }

    function updateEditor(value) {
        document.getElementById('text_container').textContent = value;

        if (typeof window.updateJsonContent === 'function') {
            try {
                window.updateJsonContent(value);
                return;
            } catch (e) {
                console.error("Error calling window.updateJsonContent:", e);
            }
        }
    }
});