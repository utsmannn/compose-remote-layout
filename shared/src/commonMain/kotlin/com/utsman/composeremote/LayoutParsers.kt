package com.utsman.composeremote

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

@OptIn(ExperimentalSerializationApi::class)
object LayoutParser {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            encodeDefaults = false
        }

    fun parseLayoutJson(jsonString: String): LayoutComponent? = try {
        val jsonElement = json.parseToJsonElement(jsonString)
        val modifierOrder = extractModifierOrder(jsonElement)

        // Store the order in a companion object for later use
        ModifierOrderTracker.setCurrentOrder(modifierOrder)

        val wrapper = json.decodeFromJsonElement<ComponentWrapper>(jsonElement)
        wrapper.component
    } catch (e: MissingFieldException) {
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    private fun extractModifierOrder(jsonElement: JsonElement): List<String> {
        val order = mutableListOf<String>()

        fun traverse(element: JsonElement) {
            when (element) {
                is JsonObject -> {
                    // Look for base modifier fields
                    element["modifier"]?.let { modifierElement ->
                        modifierElement.jsonObject["base"]?.let { baseElement ->
                            baseElement.jsonObject.keys.forEach { key ->
                                if (!order.contains(key)) {
                                    order.add(key)
                                }
                            }
                        }
                    }
                    // Continue traversing
                    element.jsonObject.values.forEach { traverse(it) }
                }
                is JsonArray -> element.forEach { traverse(it) }
                else -> {} // Do nothing for primitive values
            }
        }

        traverse(jsonElement)
        return order
    }
}

// Object to track modifier order across the parsing process
object ModifierOrderTracker {
    private var currentOrder: List<String> = emptyList()

    fun setCurrentOrder(order: List<String>) {
        currentOrder = order
    }

    fun getCurrentOrder(): List<String> = currentOrder

    fun clear() {
        currentOrder = emptyList()
    }
}
