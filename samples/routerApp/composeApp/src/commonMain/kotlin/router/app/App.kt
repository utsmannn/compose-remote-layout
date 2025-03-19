package router.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.router.ComposeRemoteRouter
import com.utsman.composeremote.router.KtorHttpLayoutFetcher
import com.utsman.composeremote.router.RenderEvent
import com.utsman.composeremote.router.ResultRouterFactory
import com.utsman.composeremote.router.cached

@Composable
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        val ktorFetcher =
            remember { KtorHttpLayoutFetcher() }
        val cachedFetcher =
            remember { ktorFetcher.cached() }
        val router = remember {
            ResultRouterFactory().createRouter(
                scope = scope,
                fetcher = cachedFetcher,
                baseUrl = "https://crl-marketplace.codeutsman.com",
            )
        }

        val isRoot by router.isRoot.collectAsState()

        val layoutContent by router.layoutContent.collectAsState()
        val isRendered by remember {
            derivedStateOf {
                layoutContent.isSuccess
            }
        }

        BackPress(!isRoot) {
            if (isRendered) {
                router.popPath()
            }
        }

        ComposeRemoteRouter(
            initialPath = "/home",
            router = router,
        ) { renderEvent ->

            val path by router.currentPath.collectAsState()

            Scaffold(
                topBar = {
                    AnimatedVisibility(path.startsWith("/product")) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Product Detail",
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        router.popPath()
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Localized description",
                                    )
                                }
                            },
                        )
                    }
                },
            ) {
                when (renderEvent) {
                    is RenderEvent.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "Loading on: ${renderEvent.path}",
                            )
                            CircularProgressIndicator()
                        }
                    }

                    is RenderEvent.Failure -> {
                        Text(
                            text = "Error on ${renderEvent.path}: ${renderEvent.error.message}",
                        )
                    }

                    is RenderEvent.RenderedLayout -> {
                        DynamicLayout(
                            component = renderEvent.component,
                            bindValue = renderEvent.bindsValue,
                            onClickHandler = renderEvent.clickEvent,
                        )
                    }
                }
            }
        }
    }
}
