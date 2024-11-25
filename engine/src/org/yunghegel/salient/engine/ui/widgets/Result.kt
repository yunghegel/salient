package org.yunghegel.salient.engine.ui.widgets

import imgui.ImGuiListClipper.forEach

class Result : InputResult {

    private val map = mutableMapOf<String, Any>()

    override fun value(key: String, value: Any) {
        map[key] = value
    }

    override fun key(key: String): Any? {
        return map[key]
    }

    override fun iterator(): Iterator<Map.Entry<String, Any>> {
        return map.iterator()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        forEach { (key, value) ->
            sb.append("$key: $value\n")
        }
        return sb.toString()
    }
}