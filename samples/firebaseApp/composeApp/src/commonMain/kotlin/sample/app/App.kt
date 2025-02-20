package sample.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson
import shared.compose.Shared

@Suppress("ktlint:standard:function-naming")
@Composable
fun App(textJson: String = "{}") {
    LaunchedEffect(Unit) {
        Shared.registerCustomNode()
    }

    Scaffold(
        modifier = Modifier
            .padding(
                WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            ),
    ) {
        val layoutNode by remember(textJson) { mutableStateOf(parseLayoutJson(textJson)) }

        DynamicLayout(layoutNode) { clickId ->
            println("clickId: $clickId")
        }
    }
}
