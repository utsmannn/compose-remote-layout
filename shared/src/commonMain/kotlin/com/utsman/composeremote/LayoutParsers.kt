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
    }

    fun parseLayoutJson(jsonString: String): LayoutComponent? {
        return try {
            val wrapper = json.decodeFromString<ComponentWrapper>(jsonString)
            wrapper.component
        } catch (e: MissingFieldException) {
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}