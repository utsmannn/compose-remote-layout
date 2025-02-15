package com.utsman.composeremote

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalSerializationApi::class)
object LayoutParser {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = false
    }

    fun parseLayoutJson(jsonString: String): LayoutComponent? = try {
        val jsonElement = json.parseToJsonElement(jsonString)
        val modifierOrder = extractModifierOrder(jsonElement)
        ModifierOrderTracker.setCurrentOrder(modifierOrder)

        parseComponentWrapper(jsonElement).component
    } catch (e: MissingFieldException) {
        null
    } catch (e: Exception) {
        null
    }

    private fun parseComponentWrapper(element: JsonElement): ComponentWrapper {
        val jsonObject = element.jsonObject
        val (type, content) = jsonObject.entries.first()

        if (CustomNodes.exists(type)) {
            val contentObj = content.jsonObject
            val modifier = contentObj["modifier"]?.let {
                json.decodeFromJsonElement<LayoutModifier>(it)
            }

            val children = contentObj["children"]?.let { childrenArray ->
                json.decodeFromJsonElement<JsonArray>(childrenArray).map {
                    parseComponentWrapper(it)
                }
            }

            val data = contentObj.filterKeys { it != "modifier" && it != "children" }
                .mapValues { (_, value) ->
                    when (value) {
                        is JsonObject -> json.decodeFromJsonElement<String>(value)
                        else -> value.jsonPrimitive.content
                    }
                }

            return ComponentWrapper(
                custom = LayoutComponent.Custom(
                    modifier = modifier,
                    type = type,
                    data = data,
                    children = children,
                ),
            )
        }

        return when (type) {
            "column" -> {
                val children = content.jsonObject["children"]?.let { childrenArray ->
                    json.decodeFromJsonElement<JsonArray>(childrenArray).map {
                        parseComponentWrapper(it)
                    }
                }
                val modifier = content.jsonObject["modifier"]?.let {
                    json.decodeFromJsonElement<LayoutModifier>(it)
                }
                ComponentWrapper(
                    column = LayoutComponent.Column(
                        modifier = modifier,
                        children = children,
                    ),
                )
            }
            "row" -> ComponentWrapper(row = json.decodeFromJsonElement(content))
            "box" -> ComponentWrapper(box = json.decodeFromJsonElement(content))
            "text" -> ComponentWrapper(text = json.decodeFromJsonElement(content))
            "button" -> ComponentWrapper(button = json.decodeFromJsonElement(content))
            "card" -> ComponentWrapper(card = json.decodeFromJsonElement(content))
            else -> throw IllegalStateException("Unknown component type: $type")
        }
    }

    private fun extractModifierOrder(jsonElement: JsonElement): List<String> {
        val order = mutableListOf<String>()

        fun traverse(element: JsonElement) {
            when (element) {
                is JsonObject -> {
                    element["modifier"]?.let { modifierElement ->
                        modifierElement.jsonObject["base"]?.let { baseElement ->
                            baseElement.jsonObject.keys.forEach { key ->
                                if (!order.contains(key)) {
                                    order.add(key)
                                }
                            }
                        }
                    }
                    element.jsonObject.values.forEach { traverse(it) }
                }
                is JsonArray -> element.forEach { traverse(it) }
                else -> {}
            }
        }

        traverse(jsonElement)
        return order
    }
}

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
