package org.yunghegel.salient.engine.ui.widgets

interface InputResult : Iterable<Map.Entry<String, Any>> {

    fun value(key: String, value: Any)

    fun key(key: String): Any?

    operator fun set(key: String, value: Any) {
        value(key, value)
    }

    operator fun get(key: String): Any? {
        return key(key)
    }


}

