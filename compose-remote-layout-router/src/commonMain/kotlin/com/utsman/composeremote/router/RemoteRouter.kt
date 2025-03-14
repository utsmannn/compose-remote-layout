package com.utsman.composeremote.router

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface RemoteRouter {
    val urlStack: MutableList<String>

    val currentUrl: String

    val previousUrl: String?

    val layoutContent: StateFlow<ResultLayout<String>>

    fun pushUrl(url: String)

    fun popUrl(): Boolean

    fun replaceUrl(url: String)

    fun clearHistory()

    fun reload()
}

class ResultRemoteRouterImpl(
    private val fetcher: LayoutFetcher,
    private val scope: CoroutineScope,
) : RemoteRouter {
    override val urlStack: MutableList<String> =
        mutableListOf()

    private val _layoutContent =
        MutableStateFlow<ResultLayout<String>>(ResultLayout.Loading)
    override val layoutContent =
        _layoutContent.asStateFlow()

    override val currentUrl: String
        get() = urlStack.lastOrNull() ?: ""

    override val previousUrl: String?
        get() = if (urlStack.size > 1) urlStack[urlStack.size - 2] else null

    override fun pushUrl(url: String) {
        if (url.isNotEmpty()) {
            urlStack.add(url)
            loadLayoutForCurrentUrl()
        }
    }

    override fun popUrl(): Boolean = if (urlStack.size > 1) {
        urlStack.removeAt(urlStack.size - 1)
        loadLayoutForCurrentUrl()
        true
    } else {
        false
    }

    override fun replaceUrl(url: String) {
        if (urlStack.isNotEmpty() && url.isNotEmpty()) {
            urlStack[urlStack.size - 1] = url
            loadLayoutForCurrentUrl()
        } else if (url.isNotEmpty()) {
            urlStack.add(url)
            loadLayoutForCurrentUrl()
        }
    }

    override fun clearHistory() {
        val currentUrl = this.currentUrl
        urlStack.clear()
        if (currentUrl.isNotEmpty()) {
            urlStack.add(currentUrl)
        }
    }

    override fun reload() {
        if (currentUrl.isNotEmpty()) {
            loadLayoutForCurrentUrl()
        }
    }

    private fun loadLayoutForCurrentUrl() {
        val url = currentUrl
        if (url.isNotEmpty()) {
            _layoutContent.value = ResultLayout.Loading

            scope.launch {
                fetcher.fetchLayoutAsFlow(url)
                    .collectLatest { result ->
                        _layoutContent.value = result
                    }
            }
        }
    }
}

class ResultRouterFactory {
    fun createRouter(
        scope: CoroutineScope,
        fetcher: LayoutFetcher = KtorHttpLayoutFetcher(),
    ): RemoteRouter = ResultRemoteRouterImpl(fetcher, scope)
}
