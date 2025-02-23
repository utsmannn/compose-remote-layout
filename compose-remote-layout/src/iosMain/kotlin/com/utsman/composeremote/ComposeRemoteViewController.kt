package com.utsman.composeremote

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun ComposeRemoteViewController(jsonString: String): UIViewController = ComposeUIViewController {
    val component = createLayoutComponent(jsonString)
    DynamicLayout(
        component = component,
    )
}
