package com.utsman.composeremote

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.MissingFieldException

@OptIn(ExperimentalSerializationApi::class)
object LayoutParser {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        serializersModule = layoutSerializersModule
    }

    fun parseLayoutJson(jsonString: String): LayoutNode? {
        return try {
            json.decodeFromString<LayoutNode>(jsonString)
        } catch (e: MissingFieldException) {
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}