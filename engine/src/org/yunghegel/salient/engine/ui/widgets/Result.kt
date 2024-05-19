package org.yunghegel.salient.engine.ui.widgets

class Result: MutableMap<String,Any> by mutableMapOf() {


    override fun toString(): String {
        val sb = StringBuilder()
        forEach { (key, value) ->
            sb.append("$key: $value\n")
        }
        return sb.toString()
    }

}