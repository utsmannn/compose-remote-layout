This is a simple implementation of a router.

This live example has the following URL:
[https://crl-marketplace.codeutsman.com/home](https://crl-marketplace.codeutsman.com/home)


<div style="width: 380px; height: 620px; overflow: scroll; border: 1px solid #ccc; border-radius: 2px;">
    <iframe src="http://localhost:8083/" width="100%" height="100%" frameborder="0" scrolling="yes" style="overflow: auto; width: 100%; height: 100%;"></iframe>
</div>

??? "Code"

    ```kotlin
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
    ```