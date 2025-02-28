package com.utsman.composeremote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.window.ComposeUIViewController
import com.utsman.composeremote.LayoutParser.parseLayoutJson
import kotlinx.coroutines.flow.MutableStateFlow
import platform.CoreGraphics.CGFloat
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import kotlin.experimental.ExperimentalObjCName

data class UIViewData(
    val uiView: UIView,
    val widthDp: CGFloat,
    val heightDp: CGFloat,
)

typealias Param = Map<String, String>

@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "RemoteViewControllerAdapter")
class RemoteViewControllerAdapter {
    private val jsonStringState: MutableStateFlow<String> =
        MutableStateFlow("{}")

    private val bindValue = BindsValue()

    fun setJsonString(jsonString: String) {
        jsonStringState.value = jsonString
    }

    fun setBindValue(
        key: String,
        value: Any,
    ) {
        bindValue.setValue(key, value)
    }

    fun registerUiView(
        type: String,
        viewDataBuilder: (Param) -> UIViewData,
    ) {
        CustomNodes.register(type) { param ->
            val viewData = remember(param.data) {
                viewDataBuilder.invoke(param.data)
            }

            UIKitView(
                factory = {
                    viewData.uiView
                },
                update = {
                    it.layoutIfNeeded()
                },
                modifier = Modifier
                    .size(width = viewData.widthDp.dp, height = viewData.heightDp.dp)
                    .then(param.modifier),
            )
        }
    }

    fun viewController(): UIViewController = ComposeUIViewController(
        configure = {
            enforceStrictPlistSanityCheck = false
        },
    ) {
        Column(
            modifier = Modifier.wrapContentHeight()
                .fillMaxWidth(),
        ) {
            val jsonString by jsonStringState.collectAsState()

            key(jsonString) {
                val component = parseLayoutJson(jsonString)
                DynamicLayout(
                    component = component,
                    modifier = Modifier.wrapContentSize(),
                    bindValue = bindValue,
                )
            }
        }
    }
}
