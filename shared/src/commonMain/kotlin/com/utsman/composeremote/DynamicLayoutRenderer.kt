package com.utsman.composeremote

object DynamicLayoutRenderer {
    private val componentCache = mutableMapOf<String, LayoutComponent>()

    fun saveComponent(
        path: String,
        component: LayoutComponent,
    ) {
        componentCache[path] = component
    }

    fun getLastValidComponent(path: String): LayoutComponent? = componentCache[path]

    fun clearCache() {
        componentCache.clear()
    }
}
