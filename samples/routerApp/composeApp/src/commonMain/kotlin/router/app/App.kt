package router.app

import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.utsman.composeremote.router.ComposeRemoteRouter
import com.utsman.composeremote.router.KtorHttpLayoutFetcher
import com.utsman.composeremote.router.ResultRouterFactory
import com.utsman.composeremote.router.cached

@Composable
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        val ktorFetcher = remember { KtorHttpLayoutFetcher() }
        val cachedFetcher = remember { ktorFetcher.cached() }
        val router = remember {
            ResultRouterFactory().createRouter(
                scope = scope,
                fetcher = cachedFetcher,
                baseUrl = "https://crl-marketplace.codeutsman.com",
            )
        }

        val isRoot by router.isRoot.collectAsState()

        BackPress(!isRoot) {
            router.popPath()
        }

        ComposeRemoteRouter(
            initialPath = "/home",
            router = router,
            loadingContent = { path ->
                Column {
                    Text(
                        text = "loading: $path",
                    )
                    CircularProgressIndicator()
                }
            },
        )
    }
}
