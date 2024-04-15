package org.yunghegel.salient.engine.system.storage

import kotlinx.serialization.Serializable

@Serializable
data class Entry<T>(val values: MutableList<Value<T>> = mutableListOf())