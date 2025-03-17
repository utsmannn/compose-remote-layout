require.config({
    paths: {
        'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs'
    }
});

require(['vs/editor/editor.main'], async function () {
    // Config state
    let config = {
        get: {
            url: 'http://localhost:3000/remote-config/parameter?key=layout',
            headers: [],
            mappings: [
                { path: 'data.layout', operator: 'direct' }
            ]
        },
        post: {
            url: 'http://localhost:3000/remote-config/parameter?key=layout',
            headers: [
                { key: 'Content-Type', value: 'application/json' }
            ]
        }
    };

    // Load schema and create editor
    const editor = await initializeEditor();

    // Initial loading with delay
    setTimeout(async () => {
        await loadInitialData();
    }, 1000);

    // DOM Elements
    const statusMessage = document.getElementById('statusMessage');
    const loadingBar = document.getElementById('loadingBar');
    const progressBar = loadingBar.querySelector('.progress');
    const configDialog = document.getElementById('configDialog');

    // Initialize all event listeners
    initializeEventListeners();

    async function loadInitialData() {
        try {
            const success = await initializeGetConfig();
            if (!success) {
                // Set default value if initialization fails
                const defaultValue = {
                    text: {
                        content: "Hello from Remote Compose"
                    }
                };
                editor.setValue(JSON.stringify(defaultValue, null, 2));
                showStatus('Using default configuration', true);
            }
        } catch (error) {
            console.error('Error in loadInitialData:', error);
            const defaultValue = {
                text: {
                    content: "Hello from Remote Compose"
                }
            };
            editor.setValue(JSON.stringify(defaultValue, null, 2));
            showStatus('Using default configuration', true);
        }
    }

    async function initializeEditor() {
        // Fetch schema
        const schemaResponse = await fetch('schema.json');
        const customSchema = await schemaResponse.json();

        // Configure schema
        const schemaWithUri = {
            uri: "http://remote-web/ui-components",
            fileMatch: ["*"],
            schema: customSchema
        };

        // Set Monaco validation options
        monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
            validate: true,
            schemas: [schemaWithUri],
            enableSchemaRequest: true
        });

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
        // Tab switching
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', () => switchTab(btn.dataset.tab));
        });

        // Config dialog buttons
        document.getElementById('configBtn').addEventListener('click', openConfigDialog);
        document.getElementById('addGetHeaderBtn').addEventListener('click', () => addHeaderRow('get'));
        document.getElementById('addPostHeaderBtn').addEventListener('click', () => addHeaderRow('post'));
        document.getElementById('addMappingBtn').addEventListener('click', addMappingRow);
        document.querySelector('.dialog-close').addEventListener('click', closeConfigDialog);
        document.getElementById('cancelConfigBtn').addEventListener('click', closeConfigDialog);
        document.getElementById('saveConfigBtn').addEventListener('click', saveConfig);
        document.getElementById('testGetConfig').addEventListener('click', () => initializeGetConfig(true));
        document.getElementById('formatBtn').addEventListener('click', formatJson);
        document.getElementById('saveToRemoteConfig').addEventListener('click', saveToRemoteConfig);

        // Close dialog when clicking outside
        configDialog.addEventListener('click', (e) => {
            if (e.target === configDialog) closeConfigDialog();
        });

        // Editor change handler
        editor.onDidChangeModelContent((e) => {
            updateEditorContent(editor.getValue());
        });
    }

    function switchTab(tabId) {
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.tab === tabId);
        });
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.toggle('active', content.dataset.tab === tabId);
        });
    }

    function addHeaderRow(type) {
        const container = document.getElementById(`${type}HeadersContainer`);
        const headerRow = document.createElement('div');
        headerRow.className = 'header-row';
        headerRow.innerHTML = `
            <input type="text" class="form-control" placeholder="Key">
            <input type="text" class="form-control" placeholder="Value">
            <button class="remove-header-btn">&times;</button>
        `;

        headerRow.querySelector('.remove-header-btn').addEventListener('click', () => {
            headerRow.remove();
        });

        container.appendChild(headerRow);
    }

    function addMappingRow() {
        const container = document.getElementById('responseMappingContainer');
        const row = document.createElement('div');
        row.className = 'mapping-row';
        row.innerHTML = `
            <input type="text" class="form-control" placeholder="Response path">
            <select class="form-control mapping-operator">
                <option value="direct">Direct</option>
                <option value="parse">Parse JSON</option>
                <option value="stringify">Stringify</option>
            </select>
            <button class="remove-header-btn">&times;</button>
        `;

        row.querySelector('.remove-header-btn').addEventListener('click', () => {
            row.remove();
        });

        container.appendChild(row);
    }

    function openConfigDialog() {
        const getConfigUrl = document.getElementById('getConfigUrl');
        const postConfigUrl = document.getElementById('postConfigUrl');
        const configBody = document.getElementById('configBody');

        // Set current values
        getConfigUrl.value = config.get.url;
        postConfigUrl.value = config.post.url;
        configBody.value = editor.getValue();

        // Clear and rebuild headers and mappings
        rebuildHeadersAndMappings();

        configDialog.style.display = 'flex';
    }

    function rebuildHeadersAndMappings() {
        // Rebuild GET headers
        const getHeadersContainer = document.getElementById('getHeadersContainer');
        getHeadersContainer.innerHTML = '';
        config.get.headers.forEach(header => {
            const row = document.createElement('div');
            row.className = 'header-row';
            row.innerHTML = `
                <input type="text" class="form-control" placeholder="Key" value="${header.key || ''}">
                <input type="text" class="form-control" placeholder="Value" value="${header.value || ''}">
                <button class="remove-header-btn">&times;</button>
            `;
            row.querySelector('.remove-header-btn').addEventListener('click', () => row.remove());
            getHeadersContainer.appendChild(row);
        });

        // Rebuild POST headers
        const postHeadersContainer = document.getElementById('postHeadersContainer');
        postHeadersContainer.innerHTML = '';
        config.post.headers.forEach(header => {
            const row = document.createElement('div');
            row.className = 'header-row';
            row.innerHTML = `
                <input type="text" class="form-control" placeholder="Key" value="${header.key || ''}">
                <input type="text" class="form-control" placeholder="Value" value="${header.value || ''}">
                <button class="remove-header-btn">&times;</button>
            `;
            row.querySelector('.remove-header-btn').addEventListener('click', () => row.remove());
            postHeadersContainer.appendChild(row);
        });

        // Rebuild mappings
        const mappingContainer = document.getElementById('responseMappingContainer');
        mappingContainer.innerHTML = '';
        config.get.mappings.forEach(mapping => {
            const row = document.createElement('div');
            row.className = 'mapping-row';
            row.innerHTML = `
                <input type="text" class="form-control" placeholder="Response path" value="${mapping.path || ''}">
                <select class="form-control mapping-operator">
                    <option value="direct" ${mapping.operator === 'direct' ? 'selected' : ''}>Direct</option>
                    <option value="parse" ${mapping.operator === 'parse' ? 'selected' : ''}>Parse JSON</option>
                    <option value="stringify" ${mapping.operator === 'stringify' ? 'selected' : ''}>Stringify</option>
                </select>
                <button class="remove-header-btn">&times;</button>
            `;
            row.querySelector('.remove-header-btn').addEventListener('click', () => row.remove());
            mappingContainer.appendChild(row);
        });
    }

    function closeConfigDialog() {
        configDialog.style.display = 'none';
    }

    function getHeadersFromContainer(type) {
        const container = document.getElementById(`${type}HeadersContainer`);
        const headerRows = container.querySelectorAll('.header-row');
        return Array.from(headerRows).map(row => {
            const inputs = row.querySelectorAll('input');
            return {
                key: inputs[0].value.trim(),
                value: inputs[1].value.trim()
            };
        }).filter(header => header.key && header.value);
    }

    function getMappingsFromContainer() {
        const mappingRows = document.getElementById('responseMappingContainer').querySelectorAll('.mapping-row');
        return Array.from(mappingRows).map(row => {
            return {
                path: row.querySelector('input').value.trim(),
                operator: row.querySelector('select').value
            };
        }).filter(mapping => mapping.path);
    }

    function saveConfig(close = true) {
        const getConfigUrl = document.getElementById('getConfigUrl');
        const postConfigUrl = document.getElementById('postConfigUrl');

        if (getConfigUrl.value.trim()) {
            config.get.url = getConfigUrl.value.trim();
        }

        if (postConfigUrl.value.trim()) {
            config.post.url = postConfigUrl.value.trim();
        }

        // Update headers and mappings
        config.get.headers = getHeadersFromContainer('get');
        config.post.headers = getHeadersFromContainer('post');
        config.get.mappings = getMappingsFromContainer();

        if (close) {
            closeConfigDialog();
            showStatus('Configuration saved');
        }
    }

    function getValueFromPath(obj, path) {
        return path.split('.').reduce((acc, part) => acc && acc[part], obj);
    }

    function processResponse(response, mappings) {
        for (const mapping of mappings) {
            let value = getValueFromPath(response, mapping.path);

            if (value !== undefined) {
                switch (mapping.operator) {
                    case 'parse':
                        try {
                            if (typeof value === 'string') {
                                value = JSON.parse(value);
                            }
                        } catch (e) {
                            console.error('Error parsing JSON:', e);
                            return null;
                        }
                        break;
                    case 'stringify':
                        if (typeof value !== 'string') {
                            value = JSON.stringify(value, null, 2);
                        }
                        break;
                }
                return value;
            }
        }
        return null;
    }

    async function initializeGetConfig(isFromTest = false) {
        try {
            if (isFromTest) {
                saveConfig(false);
            }
            showStatus('Getting the data...');
            loadingBar.style.display = 'block';
            progressBar.style.width = '30%';

            const headers = {};
            config.get.headers.forEach(header => {
                if (header.key && header.value) {
                    headers[header.key] = header.value;
                }
            });

            const response = await fetch(config.get.url, {
                method: 'GET',
                headers: headers
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            progressBar.style.width = '50%';

            const fullResponse = await response.json();
            console.log('Full response:', fullResponse);

            const layoutValue = processResponse(fullResponse, config.get.mappings);

            if (!layoutValue) {
                throw new Error('No value field found in response');
            }
            console.log('Mapped layout value:', layoutValue);

            const editorContent = typeof layoutValue === 'string'
                ? layoutValue
                : JSON.stringify(layoutValue, null, 2);

            progressBar.style.width = '80%';

            editor.setValue(editorContent);
            showStatus('Data initialized successfully');
            progressBar.style.width = '100%';
            return true;

        } catch (error) {
            console.error('Initialization error:', error);
            return false;
        } finally {
            setTimeout(() => {
                loadingBar.style.display = 'none';
                progressBar.style.width = '0%';
            }, 500);
        }
    }

    async function saveToRemoteConfig() {
        try {
            const editorValue = editor.getValue();

            loadingBar.style.display = 'block';
            progressBar.style.width = '30%';

            const headers = {};
            config.post.headers.forEach(header => {
                if (header.key && header.value) {
                    headers[header.key] = header.value;
                }
            });

            const response = await fetch(config.post.url, {
                method: 'POST',
                headers: headers,
                body: editorValue
            });

            progressBar.style.width = '100%';

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            showStatus('Successfully saved to Remote Config');

        } catch (error) {
            showStatus('Error saving to Remote Config: ' + error.message, true);
        } finally {
            setTimeout(() => {
                loadingBar.style.display = 'none';
                progressBar.style.width = '0%';
            }, 500);
        }
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

    function updateEditorContent(value) {
        document.getElementById('text_container').textContent = value;
        const configBody = document.getElementById('configBody');
        if (configDialog.style.display === 'flex') {
            configBody.value = value;
        }

        if (typeof jsonBuilderWeb !== 'undefined' && typeof jsonBuilderWeb.updateEditorContent === 'function') {
            jsonBuilderWeb.updateEditorContent(value);
        }
    }
});