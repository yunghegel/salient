package org.yunghegel.salient.common.reflect

import org.yunghegel.salient.common.reflect.ReflectionHelper.invoke
import org.yunghegel.salient.common.reflect.ReflectionHelper.method
import java.lang.reflect.Method

class MethodAccessor : AccessorBase {

    private var `object`: Any
    private var name: String
    private var getter: Method?
    private var setter: Method?

    constructor(`object`: Any, name: String, getter: String?, setter: String?) : super() {
        this.`object` = `object`
        this.name = name
        this.getter = method(`object`.javaClass, getter)
        this.setter = method(`object`.javaClass, setter, this.getter!!.returnType)
    }

    constructor(`object`: Any, name: String, getter: Method?, setter: Method?) : super() {
        this.`object` = `object`
        this.name = name
        this.getter = getter
        this.setter = setter
    }

    override fun get(): Any {
        return invoke(`object`, getter!!)
    }

    override fun set(value: Any?) {
        invoke(`object`, setter!!, value)
    }

    override fun getName(): String {
        return name
    }

    override fun getType(): Class<*> {
        return getter!!.returnType
    }

    override fun <T : Annotation> config(annotation: Class<T>?): T {
        val a: T = setter!!.getAnnotation(annotation)
        return a
    }
}
