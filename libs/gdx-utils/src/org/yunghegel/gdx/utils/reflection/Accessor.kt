package org.yunghegel.gdx.utils.reflection

interface Accessor {

    fun get(): Any?

    fun set(value: Any?)

    fun getName(): String?

    fun getType(): Class<*>?

    fun <T> get(type: Class<T>?): T? {
        return get() as T?
    }

    fun <T : Annotation> config(annotation: Class<T>?): T?

    fun <T : Annotation>  evalAnnotation(annotationClass: Class<T>?, evaluator: (T?)->Unit) {
        val annotation = config(annotationClass)
        evaluator(annotation)
    }
}
