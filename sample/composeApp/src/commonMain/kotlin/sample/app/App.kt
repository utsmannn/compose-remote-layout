package sample.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson

@Composable
fun App() {
    val textJson = """
{
  "type": "column",
  "modifier": {
    "base": {
      "fillMaxWidth": true,
      "background": {
        "color": "#F5F5F5",
        "alpha": 1.0
      },
      "padding": {
        "horizontal": 16,
        "vertical": 20
      }
    },
    "verticalArrangement": "top",
    "horizontalAlignment": "start"
  },
  "children": [
    {
      "type": "card",
      "modifier": {
        "base": {
          "fillMaxWidth": true,
          "margin": {
            "bottom": 16
          },
          "shadow": {
            "elevation": 8,
            "shape": {
              "type": "roundedCorner",
              "cornerRadius": 16
            }
          }
        }
      },
      "children": [
        {
          "type": "column",
          "modifier": {
            "base": {
              "fillMaxWidth": true,
              "padding": {
                "all": 16
              }
            },
            "verticalArrangement": "spaceBetween",
            "horizontalAlignment": "start"
          },
          "children": [
            {
              "type": "row",
              "modifier": {
                "base": {
                  "fillMaxWidth": true
                },
                "horizontalArrangement": "spaceBetween",
                "verticalAlignment": "center"
              },
              "children": [
                {
                  "type": "box",
                  "modifier": {
                    "base": {
                      "size": 48,
                      "shape": {
                        "type": "circle"
                      },
                      "background": {
                        "color": "#2196F3",
                        "alpha": 0.1
                      },
                      "border": {
                        "width": 2,
                        "color": "#2196F3",
                        "shape": {
                          "type": "circle"
                        }
                      }
                    },
                    "contentAlignment": "center"
                  },
                  "children": [
                    {
                      "type": "text",
                      "text": "JD"
                    }
                  ]
                },
                {
                  "type": "button",
                  "text": "Follow",
                  "clickId": "follow_button",
                  "modifier": {
                    "base": {
                      "shape": {
                        "type": "roundedCorner",
                        "cornerRadius": 20
                      },
                      "background": {
                        "color": "#2196F3"
                      },
                      "padding": {
                        "horizontal": 24,
                        "vertical": 8
                      }
                    }
                  }
                }
              ]
            },
            {
              "type": "row",
              "modifier": {
                "base": {
                  "fillMaxWidth": true,
                  "margin": {
                    "top": 16,
                    "bottom": 16
                  }
                },
                "horizontalArrangement": "spaceBetween",
                "verticalAlignment": "center"
              },
              "children": [
                {
                  "type": "column",
                  "modifier": {
                    "base": {
                      "weight": 1
                    },
                    "horizontalAlignment": "center"
                  },
                  "children": [
                    {
                      "type": "text",
                      "text": "Posts",
                      "modifier": {
                        "base": {
                          "margin": {
                            "bottom": 4
                          }
                        }
                      }
                    },
                    {
                      "type": "text",
                      "text": "248"
                    }
                  ]
                },
                {
                  "type": "column",
                  "modifier": {
                    "base": {
                      "weight": 1
                    },
                    "horizontalAlignment": "center"
                  },
                  "children": [
                    {
                      "type": "text",
                      "text": "Followers",
                      "modifier": {
                        "base": {
                          "margin": {
                            "bottom": 4
                          }
                        }
                      }
                    },
                    {
                      "type": "text",
                      "text": "12.4K"
                    }
                  ]
                },
                {
                  "type": "column",
                  "modifier": {
                    "base": {
                      "weight": 1
                    },
                    "horizontalAlignment": "center"
                  },
                  "children": [
                    {
                      "type": "text",
                      "text": "Following",
                      "modifier": {
                        "base": {
                          "margin": {
                            "bottom": 4
                          }
                        }
                      }
                    },
                    {
                      "type": "text",
                      "text": "284"
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    },
    {
      "type": "row",
      "modifier": {
        "base": {
          "fillMaxWidth": true,
          "margin": {
            "bottom": 16
          },
          "scrollable": true
        },
        "horizontalArrangement": "start",
        "verticalAlignment": "center"
      },
      "children": [
        {
          "type": "card",
          "modifier": {
            "base": {
              "width": 120,
              "margin": {
                "end": 8
              },
              "shadow": {
                "elevation": 2,
                "shape": {
                  "type": "roundedCorner",
                  "cornerRadius": 8
                }
              }
            }
          },
          "children": [
            {
              "type": "column",
              "modifier": {
                "base": {
                  "padding": {
                    "all": 12
                  }
                },
                "horizontalAlignment": "center"
              },
              "children": [
                {
                  "type": "box",
                  "modifier": {
                    "base": {
                      "size": 40,
                      "shape": {
                        "type": "circle"
                      },
                      "background": {
                        "color": "#4CAF50",
                        "alpha": 0.1
                      },
                      "margin": {
                        "bottom": 8
                      }
                    },
                    "contentAlignment": "center"
                  },
                  "children": [
                    {
                      "type": "text",
                      "text": "hiya"
                    }
                  ]
                },
                {
                  "type": "text",
                  "text": "${'$'}1,234",
                  "modifier": {
                    "base": {
                      "margin": {
                        "bottom": 4
                      }
                    }
                  }
                },
                {
                  "type": "text",
                  "text": "Revenue",
                  "modifier": {
                    "type": "default"
                  }
                }
              ]
            }
          ]
        },
        {
          "type": "card",
          "modifier": {
            "base": {
              "width": 120,
              "margin": {
                "end": 8
              },
              "shadow": {
                "elevation": 2,
                "shape": {
                  "type": "roundedCorner",
                  "cornerRadius": 8
                }
              }
            }
          },
          "children": [
            {
              "type": "column",
              "modifier": {
                "base": {
                  "padding": {
                    "all": 12
                  }
                },
                "horizontalAlignment": "center"
              },
              "children": [
                {
                  "type": "box",
                  "modifier": {
                    "base": {
                      "size": 40,
                      "shape": {
                        "type": "circle"
                      },
                      "background": {
                        "color": "#F44336",
                        "alpha": 0.1
                      },
                      "margin": {
                        "bottom": 8
                      }
                    },
                    "contentAlignment": "center"
                  },
                  "children": [
                    {
                      "type": "text",
                      "text": "uy"
                    }
                  ]
                },
                {
                  "type": "text",
                  "text": "86%",
                  "modifier": {
                    "base": {
                      "margin": {
                        "bottom": 4
                      }
                    }
                  }
                },
                {
                  "type": "text",
                  "text": "Growth"
                }
              ]
            }
          ]
        },
        {
          "type": "card",
          "modifier": {
            "base": {
              "width": 120,
              "margin": {
                "end": 8
              },
              "shadow": {
                "elevation": 2,
                "shape": {
                  "type": "roundedCorner",
                  "cornerRadius": 8
                }
              }
            }
          },
          "children": [
            {
              "type": "column",
              "modifier": {
                "base": {
                  "padding": {
                    "all": 12
                  }
                },
                "horizontalAlignment": "center"
              },
              "children": [
                {
                  "type": "box",
                  "modifier": {
                    "base": {
                      "size": 40,
                      "shape": {
                        "type": "circle"
                      },
                      "background": {
                        "color": "#9C27B0",
                        "alpha": 0.1
                      },
                      "margin": {
                        "bottom": 8
                      }
                    },
                    "contentAlignment": "center"
                  },
                  "children": [
                    {
                      "type": "text",
                      "text": "nah"
                    }
                  ]
                },
                {
                  "type": "text",
                  "text": "1.2M",
                  "modifier": {
                    "base": {
                      "margin": {
                        "bottom": 4
                      }
                    }
                  }
                },
                {
                  "type": "text",
                  "text": "Users"
                }
              ]
            }
          ]
        }
      ]
    },
    {
      "type": "box",
      "modifier": {
        "base": {
          "fillMaxWidth": true,
          "height": 200,
          "background": {
            "color": "#FFFFFF"
          },
          "border": {
            "width": 1,
            "color": "#E0E0E0",
            "shape": {
              "type": "roundedCorner",
              "cornerRadius": 12
            }
          },
          "padding": {
            "all": 16
          }
        },
        "contentAlignment": "center"
      },
      "children": [
        {
          "type": "text",
          "text": "Chart Area"
        }
      ]
    }
  ]
}
    """.trimIndent()
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        val layoutNode by remember { derivedStateOf { parseLayoutJson(textJson) } }
//
        DynamicLayout(layoutNode) { clickId ->
            println("clickId: $clickId")
        }

    }
}