package org.yunghegel.salient.engine.helpers.reflect

interface Accessor {

    fun get(obj: Any): Any?

    fun set(value: Any)

    val name: String

    val type: Class<*>

}