package org.yunghegel.salient.engine.ui.widgets

class Result {
    val items = mutableMapOf<String,Any>()

    operator fun get(key: String) : Any? {
        return items[key] ?: items[key.lowercase()] ?: items[key.uppercase()]
    }

    operator fun set(key: String,value: Any) {
        items[key] = value
    }

    override fun toString(): String {
        val sb = StringBuilder()
        items.forEach { (key, value) ->
            sb.append("$key: $value\n")
        }
        return sb.toString()
    }

}