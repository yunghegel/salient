package org.yunghegel.salient.engine.system.storage

import kotlinx.serialization.Serializable

@Serializable
class Value<T>(val name: String, val value: T) {
    override fun toString(): String {
        return "$name: $value"
    }
}