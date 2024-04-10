package org.yunghegel.gdx.utils.reflection

import org.yunghegel.gdx.utils.reflection.Accessor

abstract class AccessorBase : Accessor {

    override fun <T> get(type: Class<T>?): T? {
        val value = get()
        return if (value != null && type!!.isAssignableFrom(value.javaClass)) {
            get() as T
        } else null
    }

    override fun <T : Annotation> config(annotation: Class<T>?): T? {
        return annotation?.let { get()?.javaClass?.getAnnotation(it) }
    }

    fun config(): Editable {
        return config(Editable::class.java) ?: inferConfig(get())
    }

    fun <T> inferConfig(value:T): Editable {
        val name = value!!::class.java.simpleName
        val group = value!!::class.java.`package`.name.split(".").last()
        return Editable(name, group)

    }

}
