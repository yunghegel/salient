package org.yunghegel.gdx.utils.reflection

import java.lang.Exception
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ReflectionError(e: Throwable?) : Exception(e)

fun <T : Any> getFieldValue(`object`: Any?, field: Field, type: Class<T>?): T {
    return getFieldValue(`object`, field) as T
}

fun Any.setFieldValue(fieldName: String, value: Any?) {
    var clazz: Class<*> = this.javaClass
    var field: Field? = null

    while (field == null && clazz.superclass != null) {
        field = clazz.declaredFields.find { it.name == fieldName }
        clazz = clazz.superclass
    }

    if (field == null) throw NoSuchFieldException(fieldName)

    field.isAccessible = true
    field.set(this, value)
}

fun getFieldValue(`object`: Any?, field: Field): Any {
    field.trySetAccessible()
    return try {
        field.get(`object`)
    } catch (e: IllegalArgumentException) {
        throw ReflectionError(e)
    } catch (e: IllegalAccessException) {
        throw ReflectionError(e)
    }
}

fun <T : Any> getFieldValue(`object`: Any, field: String?, type: Class<T>?): T {
    return getFieldValue(`object`, field(`object`.javaClass, field), type)
}

fun getFieldValue(`object`: Any, field: String?): Any {
    return getFieldValue(`object`, field(`object`.javaClass, field))
}

fun <T> setFieldValue(`object`: Any?, field: Field, value: T) {
    try {
        field[`object`] = value
    } catch (e: java.lang.IllegalArgumentException) {
        throw ReflectionError(e)
    } catch (e: IllegalAccessException) {
        throw ReflectionError(e)
    }
}

fun field(type: Class<*>, field: String?): Field {
    return try {
        type.getField(field)
    } catch (e: NoSuchFieldException) {
        throw ReflectionError(e)
    } catch (e: SecurityException) {
        throw ReflectionError(e)
    }
}

fun <T> newInstance(type: Class<out T>): T {
    return try {
        type.getDeclaredConstructor().newInstance() as T
    } catch (e: InstantiationException) {
        throw ReflectionError(e)
    } catch (e: IllegalAccessException) {
        throw ReflectionError(e)
    }
}

fun <T : Any> newInstance(type: KClass<T>): T {
    return type.createInstance()
}

@Suppress("UNCHECKED_CAST")
fun <T> newInstance(className: String?): T {
    return try {
        Class.forName(className).getDeclaredConstructor().newInstance() as T
    } catch (e: InstantiationException) {
        throw ReflectionError(e)
    } catch (e: IllegalAccessException) {
        throw ReflectionError(e)
    } catch (e: ClassNotFoundException) {
        throw ReflectionError(e)
    }
}

fun Class<*>.createInstance(vararg args: Any?): Any {
    return try {
        getDeclaredConstructor(*args.map { it?.javaClass }.toTypedArray()).newInstance(*args)
    } catch (e: InstantiationException) {
        throw ReflectionError(e)
    } catch (e: IllegalAccessException) {
        throw ReflectionError(e)
    }
}
