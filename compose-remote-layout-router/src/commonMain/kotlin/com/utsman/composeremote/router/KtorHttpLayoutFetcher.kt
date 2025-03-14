package com.utsman.composeremote.router

import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow

val KtorClientDefault = HttpClient {
    install(Logging) {
        level = LogLevel.BODY
    }
    install(HttpCache)
}

class KtorHttpLayoutFetcher(
    private val client: HttpClient = KtorClientDefault,
) : LayoutFetcher {
    override suspend fun fetchLayout(url: String): Result<String> = try {
        val response: HttpResponse = client.get(url)
        val layoutText = response.bodyAsText()
        Result.success(layoutText)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun fetchLayoutAsFlow(url: String): Flow<ResultLayout<String>> = ResultLayout.flow {
        val response: HttpResponse = client.get(url)
        val content = response.bodyAsText()
        if (!content.startsWith("{")) {
            throw IllegalStateException(
                "Invalid layout content",
            )
        }
        content
    }

    fun close() {
        client.close()
    }
}
