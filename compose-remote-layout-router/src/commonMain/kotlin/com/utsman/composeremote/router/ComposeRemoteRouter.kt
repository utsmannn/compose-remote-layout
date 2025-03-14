package com.utsman.composeremote.router

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utsman.composeremote.BindsValue
import com.utsman.composeremote.DynamicLayout
import com.utsman.composeremote.LayoutParser.parseLayoutJson

@Composable
fun ComposeRemoteRouter(
    baseUrl: String,
    initialPath: String,
    modifier: Modifier = Modifier,
    router: RemoteRouter,
    navigationEventContainer: NavigationEventContainer = remember { NavigationEventContainer() },
    bindsValue: BindsValue = remember { BindsValue() },
    onClickHandler: (String) -> Unit = {},
    onNavigateHandler: (NavigationEvent) -> Unit = {},
    errorContent: @Composable ((Throwable, String) -> Unit)? = null,
    loadingContent: @Composable (() -> Unit)? = null,
) {
    val layoutResult by router.layoutContent.collectAsState()

    LaunchedEffect(baseUrl) {
        navigationEventContainer.setBaseUrl(baseUrl)
    }

    val initialUrl = "$baseUrl/${initialPath.removePrefix("/")}"
    LaunchedEffect(initialPath) {
        if (initialUrl.isNotEmpty()) {
            router.pushUrl(initialUrl)
        }
    }

    val navigationEvent by navigationEventContainer.event.collectAsState()

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { event ->
            when (event) {
                is NavigationEvent.Push -> {
                    router.pushUrl(event.url)
                    onNavigateHandler(
                        NavigationEvent.Push(
                            event.url,
                        ),
                    )
                }

                is NavigationEvent.Replace -> {
                    router.replaceUrl(event.url)
                    onNavigateHandler(
                        NavigationEvent.Replace(
                            event.url,
                        ),
                    )
                }

                is NavigationEvent.Home -> {
                    router.clearHistory()
                    router.pushUrl(event.url)
                    onNavigateHandler(
                        NavigationEvent.Home(
                            event.url,
                        ),
                    )
                }

                is NavigationEvent.Pop -> {
                    if (router.popUrl()) {
                        onNavigateHandler(
                            NavigationEvent.Pop,
                        )
                    }
                }

                is NavigationEvent.Reload -> {
                    router.reload()
                    onNavigateHandler(
                        NavigationEvent.Reload,
                    )
                }
            }
        }
    }

    val enhancedClickHandler =
        remember(onClickHandler, onNavigateHandler) {
            { clickId: String ->
                when {
                    clickId.startsWith("navigate:") -> {
                        val url =
                            clickId.substringAfter("navigate:")
                        navigationEventContainer.push(url)
                    }

                    clickId.startsWith("replace:") -> {
                        val url =
                            clickId.substringAfter("replace:")
                        navigationEventContainer.replace(url)
                    }

                    clickId == "back" -> {
                        navigationEventContainer.pop()
                    }

                    clickId == "home" -> {
                        navigationEventContainer.home(
                            initialUrl,
                        )
                    }

                    clickId == "reload" -> {
                        navigationEventContainer.reload()
                    }

                    else -> onClickHandler(clickId)
                }
            }
        }

    val extendedBindsValue = remember(bindsValue, router) {
        val extended = BindsValue()

        extended.setValue("currentUrl", router.currentUrl)
        extended.setValue(
            "previousUrl",
            router.previousUrl ?: "",
        )
        extended.setValue(
            "hasHistory",
            (router.urlStack.size > 1).toString(),
        )

        extended + bindsValue
    }

    Box(modifier = modifier.fillMaxSize()) {
        layoutResult.foldCompose(
            onLoading = {
                if (loadingContent != null) {
                    loadingContent.invoke()
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            },
            onSuccess = { layoutJson ->
                val layoutComponent =
                    parseLayoutJson(layoutJson)

                DynamicLayout(
                    component = layoutComponent,
                    modifier = Modifier.fillMaxSize(),
                    bindValue = extendedBindsValue,
                    onClickHandler = {
                        enhancedClickHandler(it)
                    },
                )
            },
            onFailure = { error, message ->
                if (errorContent != null) {
                    errorContent(error, message)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Error loading layout: $message",
                            modifier = Modifier.align(
                                Alignment.Center,
                            ),
                        )
                    }
                }
            },
        )
    }
}
