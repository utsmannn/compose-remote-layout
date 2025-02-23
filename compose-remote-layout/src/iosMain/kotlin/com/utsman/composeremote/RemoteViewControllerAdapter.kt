package com.utsman.composeremote

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.flow.MutableStateFlow
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import kotlin.experimental.ExperimentalObjCName

typealias SwiftUIViewBuilder = (String) -> UIView

@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "RemoteViewControllerAdapter")
class RemoteViewControllerAdapter {
    private val jsonStringState: MutableStateFlow<String> = MutableStateFlow("{}")

    fun setJsonString(jsonString: String) {
        jsonStringState.value = jsonString
    }

    fun registerUiView(viewBuilder: SwiftUIViewBuilder) {
//        val view = viewBuilder.invoke("haduh")
//        val uiView = UIHosting()
        CustomNodes.register("cuaks") { param ->
            UIKitView(
                factory = {
                    viewBuilder.invoke("haduh")
                },
                modifier = param.modifier,
            )
        }
    }

    fun viewController(): UIViewController = ComposeUIViewController(
        configure = {
            enforceStrictPlistSanityCheck = false
        },
    ) {
        val jsonString by jsonStringState.collectAsState()
        val component = createLayoutComponent(jsonString)
        DynamicLayout(
            component = component,
        )
    }
}
