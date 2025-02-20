import { initializeApp } from 'firebase-admin/app';
import { getRemoteConfig } from 'firebase-admin/remote-config';

const firebaseConfig = {
    apiKey: "AIzaSyDZ9Q1dYmUzLMuG-NOugyees63IGEVq750",
    authDomain: "compose-remote-sample.firebaseapp.com",
    projectId: "compose-remote-sample",
    storageBucket: "compose-remote-sample.firebasestorage.app",
    messagingSenderId: "536588754546",
    appId: "1:536588754546:web:b7b9b292a3652305e26ceb",
    measurementId: "G-7S2R103MBB"
  };


firebase.initializeApp(firebaseConfig);

// Initialize Remote Config with compat version
const remoteConfig = firebase.remoteConfig();
const remoteConfigSettings = {
    minimumFetchIntervalMillis: 3600000, // 1 hour
    fetchTimeoutMillis: 60000 // 1 minute
};
remoteConfig.settings = remoteConfigSettings;


remoteConfig.defaultConfig = {
    'ui_config': JSON.stringify({
        column: {
            modifier: {
                base: {
                    width: 200,
                    padding: {
                        all: 16
                    }
                },
                verticalArrangement: "spaceBetween",
                horizontalAlignment: "center"
            },
            children: [
                {
                    button: {
                        content: "Click me",
                        clickId: "button1",
                        modifier: {
                            base: {
                                fillMaxWidth: true
                            }
                        }
                    }
                },
                {
                    text: {
                        content: "Hello World",
                        modifier: {
                            base: {
                                padding: {
                                    top: 8
                                }
                            }
                        }
                    }
                }
            ]
        }
    })
};

require.config({
    paths: {
        'vs': 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs'
    }
});

require(['vs/editor/editor.main'], async function () {
    const customSchema = {
      uri: "http://myschema/ui-components",
      fileMatch: ["*"],
      schema: {
        type: "object",
        definitions: {
          padding: {
            type: "object",
            properties: {
              all: { type: "number" },
              top: { type: "number" },
              bottom: { type: "number" },
              left: { type: "number" },
              right: { type: "number" }
            }
          },
          margin: {
            type: "object",
            properties: {
              all: { type: "number" },
              top: { type: "number" },
              bottom: { type: "number" },
              left: { type: "number" },
              right: { type: "number" }
            }
          },
          background: {
            type: "object",
            properties: {
              color: { type: "string", pattern: "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$" },
              shape: { type: "string", enum: ["roundedcorner"] },
              radius: { type: "number" }
            }
          },
          shadow: {
            type: "object",
            properties: {
              elevation: { type: "number" },
              shape: {
                type: "object",
                properties: {
                  type: { type: "string", enum: ["roundedcorner"] },
                  cornerRadius: { type: "number" }
                },
                required: ["type"]
              }
            }
          },
          baseModifier: {
            type: "object",
            properties: {
              padding: { "$ref": "#/definitions/padding" },
              margin: { "$ref": "#/definitions/margin" },
              background: { "$ref": "#/definitions/background" },
              shadow: { "$ref": "#/definitions/shadow" },
              width: { type: "number" },
              height: { type: "number" },
              fillMaxWidth: { type: "boolean" },
              fillMaxHeight: { type: "boolean" }
            }
          },
          modifier: {
            type: "object",
            properties: {
              base: { "$ref": "#/definitions/baseModifier" },
              horizontalAlignment: { type: "string", enum: ["start", "center", "end"] },
              verticalAlignment: { type: "string", enum: ["top", "center", "bottom"] },
              horizontalArrangement: { type: "string", enum: ["start", "center", "end", "spaceBetween", "spaceAround", "spaceEvenly"] },
              verticalArrangement: { type: "string", enum: ["top", "center", "bottom", "spaceBetween", "spaceAround", "spaceEvenly"] }
            }
          },
          commonComponentProperties: {
            type: "object",
            properties: {
              modifier: { "$ref": "#/definitions/modifier" },
              children: {
                type: "array",
                items: { "$ref": "#/definitions/component" }
              }
            }
          },
          component: {
            type: "object",
            properties: {
              column: {
                type: "object",
                allOf: [
                  { "$ref": "#/definitions/commonComponentProperties" }
                ]
              },
              row: {
                type: "object",
                allOf: [
                  { "$ref": "#/definitions/commonComponentProperties" }
                ]
              },
              box: {
                type: "object",
                allOf: [
                  { "$ref": "#/definitions/commonComponentProperties" }
                ]
              },
              text: {
                type: "object",
                properties: {
                  content: { type: "string" },
                  modifier: { "$ref": "#/definitions/modifier" }
                },
                required: ["content"]
              },
              button: {
                type: "object",
                properties: {
                  content: { type: "string" },
                  clickId: { type: "string" },
                  modifier: { "$ref": "#/definitions/modifier" }
                },
                required: ["content", "clickId"]
              }
            },
            additionalProperties: {
              type: "object",
              properties: {
                modifier: { "$ref": "#/definitions/modifier" },
                children: {
                  type: "array",
                  items: { "$ref": "#/definitions/component" }
                }
              },
              additionalProperties: true
            }
          }
        },
        allOf: [
          { "$ref": "#/definitions/component" }
        ],
        additionalProperties: true
      }
    }

    // Set the schema in Monaco Editor
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
        tabSize: 2
    });

    // Initial JSON content
    try {
        await remoteConfig.fetchAndActivate();
        const remoteValue = remoteConfig.getValue('layout').asString();
        if (remoteValue) {
            editor.setValue(remoteValue);
        } else {
            editor.setValue(remoteConfig.defaultConfig.ui_config);
        }
    } catch (error) {
        console.error('Error fetching remote config:', error);
        editor.setValue(remoteConfig.defaultConfig.ui_config);
    }


    // Set the value after 300ms delay
//    setTimeout(() => {
//        editor.setValue(initialValue);
//    }, 300);


    document.getElementById('formatBtn').addEventListener('click', function() {
        try {
            editor.getAction('editor.action.formatDocument').run();
        } catch (error) {
            alert('Invalid JSON: ' + error.message);
        }
    });


    document.getElementById('saveToRemoteConfig').addEventListener('click', async function() {
        try {
            const editorContent = editor.getValue();
            // Validate JSON
            JSON.parse(editorContent);

            // Update Remote Config
            remoteConfig.setValue('layout', editorContent);

            // Activate the changes
            await remoteConfig.activate();
            alert('Successfully saved to Remote Config!');
        } catch (error) {
            alert('Error saving to Remote Config: ' + error.message);
        }
    });

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