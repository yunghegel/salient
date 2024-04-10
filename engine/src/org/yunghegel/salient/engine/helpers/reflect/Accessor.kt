package org.yunghegel.salient.engine.reflect

interface Accessor {

    fun get(obj: Any): Any?

    fun set(value: Any);

    val name: String

    val type: Class<*>

}