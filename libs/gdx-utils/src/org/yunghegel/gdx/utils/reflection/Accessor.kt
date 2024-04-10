package org.yunghegel.gdx.utils.reflection

interface Accessor {

    fun get(): Any?

    fun set(value: Any?)

    fun getName(): String?

    fun getType(): Class<*>?

    fun <T> get(type: Class<T>?): T?

    fun <T : Annotation> config(annotation: Class<T>?): T?
}
