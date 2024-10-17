package org.yunghegel.gdx.utils.reflection

import kotlin.reflect.KClass

interface KAccessor {

    fun get(): Any

    fun set(value: Any)

    val name : String

    val type : KClass<*>

}