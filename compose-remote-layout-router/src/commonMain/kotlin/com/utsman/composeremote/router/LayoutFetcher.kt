package com.utsman.composeremote.router

import kotlinx.coroutines.flow.Flow

interface LayoutFetcher {
    suspend fun fetchLayout(url: String): Result<String>

    fun fetchLayoutAsFlow(url: String): Flow<ResultLayout<String>>
}
