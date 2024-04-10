package org.yunghegel.gdx.utils.reflection

import org.yunghegel.gdx.utils.reflection.Accessor
import java.lang.reflect.Field

class FieldAccessorWrapper(original: Accessor?, field: Field?) : AccessorBase() {

    private var original: Accessor? = null
    private var field: Field? = null

    init {
        this.original = original
        this.field = field
    }

    constructor(original: Accessor, field: String?) : this(
        original, ReflectionHelper.field(
            original.getType()!!, field
        )
    )

    override fun get(): Any {
        return ReflectionHelper[original!!.get(), field]
    }

    override fun set(value: Any?) {
        ReflectionHelper.set(original!!.get(), field, value)
    }

    override fun getName(): String? {
        return field!!.name
    }

    override fun getType(): Class<*> {
        return field!!.type
    }

    override fun <T : Annotation> config(annotation: Class<T>?): T? {
        return field!!.getAnnotation(annotation)
    }
}
