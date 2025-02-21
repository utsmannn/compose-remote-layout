require.config({
    paths: {
        'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs'
    }
});

require(['vs/editor/editor.main'], async function () {
    try {
        const schemaResponse = await fetch('schema.json');
        if (!schemaResponse.ok) {
            throw new Error(`HTTP error! status: ${schemaResponse.status}`);
        }
        const schemaData = await schemaResponse.json();

        const customSchema = {
            uri: "http://remote-web/ui-components",
            fileMatch: ["*"],
            schema: schemaData
        };

        // Set the schema in Monaco Editor
        monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
            validate: true,
            schemas: [customSchema],
            enableSchemaRequest: true
        });

        // Create editor instance
        const editor = monaco.editor.create(document.getElementById('editor'), {
            language: 'json',
            theme: 'vs-dark',
            automaticLayout: true,
            formatOnPaste: true,
            formatOnType: true,
            tabSize: 2,
            minimap: {
                enabled: true
            },
            scrollbar: {
                vertical: 'visible',
                horizontal: 'visible'
            }
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

        // Device size handling
        const composeContainer = document.getElementById('compose');
        const deviceSelector = document.getElementById('deviceSize');
        const composeSection = document.querySelector('.compose-section');

        function updateComposeSize() {
            try {
                const [width, height] = deviceSelector.value.split('x').map(Number);
                const maxScale = 1;
                const minScale = 0.25;

                const previewContainer = document.querySelector('.device-preview-container');
                const availableWidth = previewContainer.clientWidth - 40;
                const availableHeight = previewContainer.clientHeight - 40;

                // Calculate scale based on both width and height constraints
                let scale = Math.min(
                    availableWidth / width,
                    availableHeight / height,
                    maxScale
                );

                scale = Math.max(scale, minScale);

                // Set the actual dimensions
                composeContainer.style.width = `${width}px`;
                composeContainer.style.height = `${height}px`;

                // Apply scaling transform
                composeContainer.style.transform = `scale(${scale})`;

                // Adjust container position to center
                const scaledWidth = width * scale;
                const scaledHeight = height * scale;

                composeContainer.style.position = 'absolute';
                composeContainer.style.left = `${(availableWidth - width * scale) / 2 + 20}px`;
                composeContainer.style.top = `${(availableHeight - height * scale) / 2 + 20}px`;

                // Update scale indicator
                let scaleIndicator = document.querySelector('.scale-indicator');
                if (!scaleIndicator) {
                    scaleIndicator = document.createElement('div');
                    scaleIndicator.className = 'scale-indicator';
                    previewContainer.appendChild(scaleIndicator);
                }
                scaleIndicator.textContent = `${Math.round(scale * 100)}%`;

            } catch (error) {
                console.error('Error updating compose size:', error);
            }
        }

        // Add resize observer to handle container size changes
        const resizeObserver = new ResizeObserver(entries => {
            updateComposeSize();
        });

        resizeObserver.observe(composeSection);

        deviceSelector.addEventListener('change', updateComposeSize);
        updateComposeSize(); // Initial size update

        // Format button handler
        document.getElementById('formatBtn').addEventListener('click', function() {
            try {
                editor.getAction('editor.action.formatDocument').run();
            } catch (error) {
                console.error('Format error:', error);
                alert('Invalid JSON: ' + error.message);
            }
        });

        // Save to remote config handler
        document.getElementById('saveToRemoteConfig').addEventListener('click', async function() {
            const loadingContainer = document.querySelector('.loading-container');
            const notification = document.getElementById('notification');

            try {
                loadingContainer.style.display = 'flex';
                const editorValue = editor.getValue();

                // Validate JSON before sending
                JSON.parse(editorValue);

                const response = await fetch('http://localhost:3000/parameter?key=layout', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: editorValue
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                // Show success notification
                notification.style.display = 'block';
                setTimeout(() => {
                    notification.style.display = 'none';
                }, 3000);

            } catch (error) {
                console.error('Save error:', error);
                alert('Error saving to Remote Config: ' + error.message);
            } finally {
                loadingContainer.style.display = 'none';
            }
        });

        setTimeout(() => {
            // Set initial value
            editor.setValue(initialValue);
            editor.getAction('editor.action.formatDocument').run();
        }, 600);

        // Editor change event handler
        let updateTimeout;
        editor.onDidChangeModelContent(function(e) {
            try {
                const newValue = editor.getValue();
                document.getElementById('text_container').textContent = newValue;

                // Debounce the update to prevent too frequent calls
                if (updateTimeout) {
                    clearTimeout(updateTimeout);
                }

                updateTimeout = setTimeout(() => {
                    if (typeof jsonBuilderWeb !== 'undefined' &&
                        typeof jsonBuilderWeb.updateEditorContent === 'function') {
                        try {
                            const parsedValue = JSON.parse(newValue);
                            jsonBuilderWeb.updateEditorContent(newValue);
                        } catch (parseError) {
                            console.warn('Invalid JSON:', parseError);
                        }
                    } else {
                        console.warn('jsonBuilderWeb not loaded yet');
                    }
                }, 300);

            } catch (error) {
                console.error('Update error:', error);
            }
        });

        // Keyboard shortcuts
        editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, function() {
            document.getElementById('saveToRemoteConfig').click();
        });

        editor.addCommand(monaco.KeyMod.Alt | monaco.KeyCode.KeyF, function() {
            document.getElementById('formatBtn').click();
        });

        // Clean up
        window.addEventListener('beforeunload', () => {
            resizeObserver.disconnect();
            if (updateTimeout) {
                clearTimeout(updateTimeout);
            }
            editor.dispose();
        });

    } catch (error) {
        console.error('Initialization error:', error);
        alert('Failed to initialize the editor. Please check the console for details.');
    }
});