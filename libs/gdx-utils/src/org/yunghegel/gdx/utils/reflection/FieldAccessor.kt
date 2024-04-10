package org.yunghegel.salient.common.reflect

import java.lang.reflect.Field

class FieldAccessor : AccessorBase {

    private var `object`: Any? = null
    private var field: Field
    private var label: String? = null

    constructor(`object`: Any?, field: Field, labelName: String) : super() {
        this.`object` = `object`
        this.field = field
        this.label = labelName
    }

    constructor(`object`: Any?, field: Field) : super() {
        this.`object` = `object`
        this.field = field
        this.label = field.name
    }

    constructor(`object`: Any, fieldName: String) : super() {
        this.`object` = `object`
        this.field = ReflectionHelper.field(`object`.javaClass, fieldName)
        this.label = fieldName
    }

    constructor(field: Field) : super() {
        this.field = field
        this.label = field.name
    }

    override fun get(): Any {
        return ReflectionHelper.get(`object`, field)
    }

    override fun set(value: Any?) {
        ReflectionHelper.set(`object`, field, value)
    }

    override fun getType(): Class<*> {
        return field.type
    }

    override fun getName(): String {
        return field.name
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj is FieldAccessor) {
            obj.field.equals(field)
        } else super.equals(obj)
    }

    override fun hashCode(): Int {
        return field.hashCode()
    }

    override fun <T : Annotation> config(annotation: Class<T>?): T? {
        return field.getAnnotation(annotation)
    }
}
