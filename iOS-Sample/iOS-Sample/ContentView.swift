//
//  ContentView.swift
//  iOS-Sample
//
//  Created by Utsman Muhammad on 01/03/25.
//

import SwiftUI
import ComposeRemoteLayoutSwift

struct ContentView: View {
    private let bindsValue = BindsValue()
    @State private var jsonLayout: String = """
    {
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
              "content": "Hello from Remote Layout"
            }
          },
          {
            "button": {
              "content": "Click Me",
              "clickId": "main_button"
            }
          }
        ]
      }
    }
    """
    
    init() {
        bindsValue.setClickHandler { clickId in
            print("clicked -> \(clickId)")
        }
    }

    var body: some View {
        DynamicLayoutView(jsonLayout: $jsonLayout, bindsValue: bindsValue)
    }
}

#Preview {
    ContentView()
}
