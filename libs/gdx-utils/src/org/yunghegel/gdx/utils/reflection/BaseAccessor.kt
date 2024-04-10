package org.yunghegel.salient.common.reflect

abstract class AccessorBase : Accessor {

    override fun <T> get(type: Class<T>?): T? {
        val value = get()
        return if (value != null && type!!.isAssignableFrom(value.javaClass)) {
            get() as T
        } else null
    }

    override fun <T : Annotation> config(annotation: Class<T>?): T? {
        return null
    }

    fun config(): Editable? {
        return config(Editable::class.java)
    }

}
