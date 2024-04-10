package org.yunghegel.salient.common.reflect

import org.yunghegel.salient.common.reflect.ReflectionHelper.invoke
import java.lang.reflect.Method

class VoidAccessor(private val `object`: Any?, method: Method?, name: String) : AccessorBase() {

    private val method: Method?
    private val name: String

    constructor(`object`: Any?, method: Method?) : this(`object`, method, method!!.name)
    constructor(`object`: Any, method: String?) : this(`object`, ReflectionHelper.method(`object`.javaClass, method))

    init {
        this.method = method
        this.name = name
    }

    override fun get(): Any? {
        invoke(`object`, method!!)
        return null
    }

    override fun set(value: Any?) {}
    override fun getName(): String {
        return name
    }

    override fun getType(): Class<*> {
        return Void.TYPE
    }

    override fun <T : Annotation> config(annotation: Class<T>?): T? {
        return method!!.getAnnotation(annotation)
    }
}
