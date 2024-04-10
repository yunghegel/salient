package org.yunghegel.gdx.utils.reflection

import com.badlogic.gdx.utils.Array
import org.yunghegel.gdx.utils.reflection.Accessor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

object AccessorScanner {

    private fun scanField(accessors: Array<Accessor>, entity: Any, field: Field, annotationBasedOnly: Boolean) {
        if (Modifier.isStatic(field.modifiers)) return

        if (annotationBasedOnly) {
            val editable = field.getAnnotation(Editable::class.java)
            if (editable == null) return
            if (editable.value.isEmpty()) {
                accessors.add(FieldAccessor(entity, field))
            } else {
                accessors.add(FieldAccessorWrapper(FieldAccessor(entity, field), editable.value))
            }
        } else {
            if (field.getAnnotation(NotEditable::class.java) != null) return
            accessors.add(FieldAccessor(entity, field))
        }
    }

    private fun scanMethod(accessors: Array<Accessor>, entity: Any, method: Method, annotationBasedOnly: Boolean) {
        if (Modifier.isStatic(method.modifiers)) return
        val editable = method.getAnnotation(Editable::class.java)
        if (editable == null && annotationBasedOnly) return
        if (method.getAnnotation(NotEditable::class.java) != null) return

        if (editable != null && method.returnType == Void::class.java && method.parameterTypes.isEmpty()) {
            if (editable.value.isEmpty()) {
                accessors.add(VoidAccessor(entity, method))
            } else {
                accessors.add(VoidAccessor(entity, method, editable.value))
            }
        }

        if (method.name.startsWith("set") && method.name.length > 3 && method.parameterTypes.size === 1) {
            var getterName = "g" + method.name.substring(1)
            var getter = ReflectionHelper.method(entity.javaClass, getterName)
            if (getter == null || getter.returnType != method.parameterTypes[0]) {
                // try boolean pattern setX/isX
                getterName = "is" + method.name.substring(3)
                getter = ReflectionHelper.method(entity.javaClass, getterName)
            }
            if (getter != null && getter.returnType == method.parameterTypes[0]) {
                val name = method.name.substring(3, 4).lowercase(Locale.getDefault()) + method.name.substring(4)
                accessors.add(MethodAccessor(entity, name, getter, method))
            }
        }
    }

    fun scan(entity: Any, annotationBasedOnly: Boolean): Array<Accessor> {
        return scan(entity, annotationBasedOnly, true)
    }

    fun scan(entity: Any, annotationBasedOnly: Boolean, includeTransient: Boolean): Array<Accessor> {
        val accessors = Array<Accessor>()

        // scan fields
        for (field in entity.javaClass.fields) {
            scanField(
                accessors,
                entity,
                field,
                annotationBasedOnly
            )
        }

        // scan getter/setter pattern
        for (method in entity.javaClass.methods) {
            scanMethod(accessors, entity, method!!, annotationBasedOnly)
        }
        return accessors
    }

    fun scan(entity: Any, vararg annotations: Class<out Annotation?>?): Array<Accessor> {
        val accessors = Array<Accessor>()

        // scan fields
        for (field in entity.javaClass.fields) {
            var match = true
            for (a in annotations) {
                if (field.getAnnotation(a) == null) {
                    match = false
                    break
                }
            }
            if (match) {
                scanField(accessors, entity, field, false)
            }
        }

        // scan getter/setter pattern
        for (method in entity.javaClass.methods) {
            var match = true
            for (a in annotations) {
                if (method.getAnnotation(a) == null) {
                    match = false
                    break
                }
            }
            if (match) {
                scanMethod(accessors, entity, method, false)
            }
        }
        return accessors
    }

}
