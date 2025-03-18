package com.utsman.composeremote.router

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.utsman.composeremote.BindsValue
import com.utsman.composeremote.LayoutParser.parseLayoutJson

typealias TransitionSpec = (type: RemoteRouter.TransitionType, durationMillis: Int) -> ContentTransform

private fun defaultTransitionSpec(
    type: RemoteRouter.TransitionType,
    duration: Int,
): ContentTransform = when (type) {
    RemoteRouter.TransitionType.PUSH, RemoteRouter.TransitionType.POP, RemoteRouter.TransitionType.REPLACE -> {
        fadeIn(
            animationSpec = tween(
                duration,
                easing = EaseInOut,
            ),
        ) togetherWith fadeOut(
            animationSpec = tween(
                duration,
                easing = EaseInOut,
            ),
        )
    }

    RemoteRouter.TransitionType.RELOAD -> {
        fadeIn(
            animationSpec = tween(duration / 2),
        ) togetherWith fadeOut(
            animationSpec = tween(duration / 2),
        )
    }

    RemoteRouter.TransitionType.NONE -> {
        EnterTransition.None togetherWith ExitTransition.None
    }
}

@Composable
fun ComposeRemoteRouter(
    initialPath: String,
    modifier: Modifier = Modifier,
    router: RemoteRouter,
    navigationEventContainer: NavigationEventContainer = remember { NavigationEventContainer() },
    bindsValue: BindsValue = remember { BindsValue() },
    onClickHandler: (String) -> Unit = {},
    onNavigateHandler: (NavigationEvent) -> Unit = {},
    animationDuration: Int = 300,
    transitionSpec: TransitionSpec = ::defaultTransitionSpec,
    onRenderEvent: @Composable (RenderEvent) -> Unit,
) {
    val layoutResult by router.layoutContent.collectAsState()
    val baseUrl = remember { router.baseUrl }

    var transitionType by remember {
        mutableStateOf(
            RemoteRouter.TransitionType.NONE,
        )
    }

    LaunchedEffect(baseUrl) {
        navigationEventContainer.setBaseUrl(baseUrl)
    }

    val initialUrl =
        "$baseUrl/${initialPath.removePrefix("/")}"
    LaunchedEffect(initialPath) {
        if (initialUrl.isNotEmpty()) {
            router.pushPath(initialPath)
        }
    }

    val navigationEvent by navigationEventContainer.event.collectAsState()

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { event ->

            transitionType = when (event) {
                is NavigationEvent.Push -> RemoteRouter.TransitionType.PUSH
                is NavigationEvent.Replace -> RemoteRouter.TransitionType.REPLACE
                is NavigationEvent.Home -> RemoteRouter.TransitionType.REPLACE
                is NavigationEvent.Pop -> RemoteRouter.TransitionType.POP
                is NavigationEvent.Reload -> RemoteRouter.TransitionType.RELOAD
            }

            when (event) {
                is NavigationEvent.Push -> {
                    router.pushPath(event.path)
                    onNavigateHandler(
                        NavigationEvent.Push(
                            event.path,
                        ),
                    )
                }

                is NavigationEvent.Replace -> {
                    router.replacePath(event.path)
                    onNavigateHandler(
                        NavigationEvent.Replace(
                            event.path,
                        ),
                    )
                }

                is NavigationEvent.Home -> {
                    router.clearHistory()
                    router.pushPath(event.path)
                    onNavigateHandler(
                        NavigationEvent.Home(
                            event.path,
                        ),
                    )
                }

                is NavigationEvent.Pop -> {
                    if (router.popPath()) {
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

            navigationEventContainer.clear()
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

    AnimatedContent(
        targetState = layoutResult,
        transitionSpec = {
            transitionSpec(
                transitionType,
                animationDuration,
            )
        },
        modifier = modifier,
        label = "Screen Transition",
    ) { currentLayoutResult ->
        val currentPath by router.currentPath.collectAsState()

        currentLayoutResult.foldCompose(
            onLoading = {
                onRenderEvent.invoke(
                    RenderEvent.Loading(
                        currentPath,
                    ),
                )
            },
            onSuccess = { layoutJson ->
                val layoutComponent =
                    parseLayoutJson(layoutJson)

                if (layoutComponent != null) {
                    onRenderEvent.invoke(
                        RenderEvent.RenderedLayout(
                            layoutComponent,
                            currentPath,
                            bindsValue,
                            enhancedClickHandler,
                        ),
                    )
                }
            },
            onFailure = { error, message ->
                onRenderEvent.invoke(
                    RenderEvent.Failure(
                        error,
                        message,
                    ),
                )
            },
        )
    }
}
