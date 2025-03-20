package com.utsman.composeremote.router

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface RemoteRouter {
    val baseUrl: String

    val urlStack: MutableList<String>

    val currentUrl: StateFlow<String>

    val previousUrl: StateFlow<String?>

    val currentPath: StateFlow<String>

    val previousPath: StateFlow<String?>

    val isRoot: StateFlow<Boolean>

    val layoutContent: StateFlow<ResultLayout<String>>

    fun pushPath(path: String)

    fun popPath(): Boolean

    fun replacePath(path: String)

    fun clearHistory()

    fun reload()

    enum class TransitionType {
        PUSH,
        POP,
        REPLACE,
        RELOAD,
        NONE,
    }
}

internal class ResultRemoteRouterImpl(
    private val fetcher: LayoutFetcher,
    private val scope: CoroutineScope,
    override val baseUrl: String,
) : RemoteRouter {
    override val urlStack: MutableList<String> =
        mutableListOf()

    private val _layoutContent =
        MutableStateFlow<ResultLayout<String>>(ResultLayout.Loading)

    override val layoutContent =
        _layoutContent.asStateFlow()

    private val _currentUrl: MutableStateFlow<String> =
        MutableStateFlow("")
    override val currentUrl: StateFlow<String>
        get() = _currentUrl

    private val _previousUrl: MutableStateFlow<String?> =
        MutableStateFlow(null)
    override val previousUrl: StateFlow<String?>
        get() = _previousUrl

    private val _currentPath: MutableStateFlow<String> = MutableStateFlow("/")
    override val currentPath: StateFlow<String>
        get() = _currentPath

    private val _previousPath: MutableStateFlow<String?> = MutableStateFlow(null)
    override val previousPath: StateFlow<String?>
        get() = _previousPath

    private val _isRoot: MutableStateFlow<Boolean> =
        MutableStateFlow(true)
    override val isRoot: StateFlow<Boolean>
        get() = _isRoot

    override fun pushPath(path: String) {
        if (path.isNotEmpty()) {
            urlStack.add(url(path))
            loadLayoutForCurrentUrl()
        }
    }

    override fun popPath(): Boolean = if (urlStack.size > 1) {
        urlStack.removeAt(urlStack.size - 1)
        loadLayoutForCurrentUrl()
        true
    } else {
        false
    }

    override fun replacePath(path: String) {
        if (urlStack.isNotEmpty() && path.isNotEmpty()) {
            urlStack[urlStack.size - 1] = url(path)
        } else if (path.isNotEmpty()) {
            urlStack.add(url(path))
        }

        loadLayoutForCurrentUrl()
    }

    override fun clearHistory() {
        val currentUrl = this.currentUrl.value
        urlStack.clear()
        if (currentUrl.isNotEmpty()) {
            urlStack.add(currentUrl)
        }

        calculateIsRoot()
    }

    override fun reload() {
        if (currentUrl.value.isNotEmpty()) {
            loadLayoutForCurrentUrl()
        }
    }

    private fun loadLayoutForCurrentUrl() {
        calculateIsRoot()
        val url = currentUrl.value
        if (url.isNotEmpty()) {
            _layoutContent.value = ResultLayout.Loading

            scope.launch {
                fetcher.fetchLayoutAsFlow(url)
                    .catch {
                        _layoutContent.value = ResultLayout.failure(it)
                    }
                    .collectLatest { result ->
                        _layoutContent.value = result
                        calculateIsRoot()
                    }
            }
        }
    }

    private fun calculateIsRoot() {
        _currentUrl.value = urlStack.lastOrNull().orEmpty()
        _previousUrl.value = if (urlStack.size > 1) urlStack[urlStack.size - 2] else null
        _currentPath.value = getPath(currentUrl.value).orEmpty()
        _previousPath.value = getPath(previousUrl.value)
        _isRoot.value = urlStack.size <= 1
    }

    private fun url(path: String): String = "$baseUrl/${path.removePrefix("/")}"

    private fun getPath(url: String?): String? = if (url != null) {
        "/" + url.removePrefix(baseUrl)
            .removePrefix("/")
    } else {
        null
    }
}

class ResultRouterFactory {
    fun createRouter(
        scope: CoroutineScope,
        fetcher: LayoutFetcher = KtorHttpLayoutFetcher(),
        baseUrl: String,
    ): RemoteRouter = ResultRemoteRouterImpl(fetcher, scope, baseUrl)
}
