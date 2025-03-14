package com.utsman.composeremote.router

import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow

class KtorHttpLayoutFetcher : LayoutFetcher {
    private val client = HttpClient {
        install(Logging) {
            level = LogLevel.BODY
        }
        install(HttpCache)
    }

    override suspend fun fetchLayout(url: String): Result<String> = try {
        println("Fetching layout from: $url")
        val response: HttpResponse = client.get(url)
        val layoutText = response.bodyAsText()
        Result.success(layoutText)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun fetchLayoutAsFlow(url: String): Flow<ResultLayout<String>> = ResultLayout.flow {
        println("Fetching layout from: $url")
        val response: HttpResponse = client.get(url)
        response.bodyAsText()
    }

    fun close() {
        client.close()
    }
}
