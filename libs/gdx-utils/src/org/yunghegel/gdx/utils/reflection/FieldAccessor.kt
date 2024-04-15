package org.yunghegel.gdx.utils.reflection

import com.badlogic.gdx.scenes.scene2d.Actor
import org.yunghegel.gdx.utils.data.Searchable
import java.lang.reflect.Field

class FieldAccessor : AccessorBase,Searchable {

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

    var actor : Actor? = null
    private var `object`: Any? = null
    private var field: Field
    var label: String? = null
    var group = "null"
    private val typeName : String
        get() = this.field.type.name

    var config : Editable? = null

    init {

    }



    override fun get(): Any {
        return ReflectionHelper.get(`object`, field)
    }

    override fun <T> get(type: Class<T>?): T? {
        return ReflectionHelper.get(`object`, field, type)
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

    override val searchTerms: List<String>
        get() = listOf(label ?: "", getName(), getType().typeName,if(group!="null") group else "").filter { it.isNotEmpty() }

    override fun equals(obj: Any?): Boolean {
        return if (obj is FieldAccessor) {
            obj.field.equals(field)
        } else super.equals(obj)
    }

    override fun hashCode(): Int {
        return field.hashCode()
    }

    override fun <T : Annotation> config(annotation: Class<T>?): T? {
        return annotation?.let { field.getAnnotation(it) }
    }

    override fun toString(): String {
        return "$typeName[object=$`object`, field=$field, label='$label']"
    }
}
