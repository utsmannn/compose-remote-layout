package router.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.utsman.composeremote.router.ComposeRemoteRouter
import com.utsman.composeremote.router.KtorHttpLayoutFetcher
import com.utsman.composeremote.router.NavigationEventContainer
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
            )
        }

        val navigationEventContainer =
            remember { NavigationEventContainer() }

        BackPress {
            navigationEventContainer.pop()
        }

        ComposeRemoteRouter(
            baseUrl = "https://crl-marketplace.codeutsman.com",
            initialPath = "home",
            router = router,
            navigationEventContainer = navigationEventContainer,
        )
    }
}
