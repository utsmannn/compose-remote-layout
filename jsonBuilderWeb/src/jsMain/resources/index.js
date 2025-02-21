require.config({
    paths: {
        'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs'
    }
});

require(['vs/editor/editor.main'], async function () {
    // Config state
    let config = {
        url: 'http://localhost:3000/parameter?key=layout',
        headers: [
            { key: 'Content-Type', value: 'application/json' }
        ]
    };

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
    const configDialog = document.getElementById('configDialog');

    function showStatus(message, isError = false) {
        statusMessage.textContent = message;
        statusMessage.style.color = isError ? '#ff4444' : '#e0e0e0';
        statusMessage.classList.add('show');
        setTimeout(() => {
            statusMessage.classList.remove('show');
        }, 3000);
    }

    // Config Dialog Functions
    function openConfigDialog() {
        const configUrl = document.getElementById('configUrl');
        const configBody = document.getElementById('configBody');
        const headersContainer = document.getElementById('headersContainer');

        // Set current values
        configUrl.value = config.url;
        configBody.value = editor.getValue();

        // Clear and rebuild headers
        headersContainer.innerHTML = '';
        config.headers.forEach(header => {
            addHeaderRow(header.key, header.value);
        });

        configDialog.style.display = 'flex';
    }

    function closeConfigDialog() {
        configDialog.style.display = 'none';
    }

    function addHeaderRow(key = '', value = '') {
        const headersContainer = document.getElementById('headersContainer');
        const headerRow = document.createElement('div');
        headerRow.className = 'header-row';
        headerRow.innerHTML = `
            <input type="text" class="form-control" placeholder="Key" value="${key}">
            <input type="text" class="form-control" placeholder="Value" value="${value}">
            <button class="remove-header-btn">&times;</button>
        `;

        headerRow.querySelector('.remove-header-btn').addEventListener('click', () => {
            headerRow.remove();
        });

        headersContainer.appendChild(headerRow);
    }

    function saveConfig() {
        const configUrl = document.getElementById('configUrl');
        const headersContainer = document.getElementById('headersContainer');
        const headerRows = headersContainer.querySelectorAll('.header-row');

        config.url = configUrl.value;
        config.headers = Array.from(headerRows).map(row => {
            const inputs = row.querySelectorAll('input');
            return {
                key: inputs[0].value,
                value: inputs[1].value
            };
        });

        closeConfigDialog();
        showStatus('Configuration saved');
    }

    // Event Listeners
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

            const headers = {};
            config.headers.forEach(header => {
                headers[header.key] = header.value;
            });

            const response = await fetch(config.url, {
                method: 'POST',
                headers: headers,
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

    // Config button event listeners
    document.getElementById('configBtn').addEventListener('click', openConfigDialog);
    document.getElementById('addHeaderBtn').addEventListener('click', () => addHeaderRow());
    document.querySelector('.dialog-close').addEventListener('click', closeConfigDialog);
    document.getElementById('cancelConfigBtn').addEventListener('click', closeConfigDialog);
    document.getElementById('saveConfigBtn').addEventListener('click', saveConfig);

    // Editor change event handler
    editor.onDidChangeModelContent(function(e) {
        const newValue = editor.getValue();
        document.getElementById('text_container').textContent = newValue;

        // Update config dialog body if it's open
        const configBody = document.getElementById('configBody');
        if (configDialog.style.display === 'flex') {
            configBody.value = newValue;
        }

        if (typeof jsonBuilderWeb !== 'undefined' && typeof jsonBuilderWeb.updateEditorContent === 'function') {
            jsonBuilderWeb.updateEditorContent(newValue);
        } else {
            console.log('remoteWeb not loaded yet');
        }
    });

    // Close dialog when clicking outside
    configDialog.addEventListener('click', function(e) {
        if (e.target === configDialog) {
            closeConfigDialog();
        }
    });

    // Initialize the editor with default config values
    const configBody = document.getElementById('configBody');
    configBody.value = initialValue;
});