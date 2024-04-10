package org.yunghegel.salient.common.reflect

import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object ReflectionHelper {

    operator fun <T> get(`object`: Any?, field: Field?, type: Class<T>?): T {
        return ReflectionHelper[`object`, field] as T
    }

    operator fun get(`object`: Any?, field: Field?): Any {
        return try {
            field!!.get(`object`)
        } catch (e: IllegalArgumentException) {
            throw ReflectionError(e)
        } catch (e: IllegalAccessException) {
            throw ReflectionError(e)
        }
    }

    operator fun <T> get(`object`: Any, field: String?, type: Class<T>?): T {
        return get(`object`, field(`object`.javaClass, field), type)
    }

    operator fun get(`object`: Any, field: String?): Any {
        return ReflectionHelper[`object`, field(`object`.javaClass, field)]
    }

    operator fun <T> set(`object`: Any?, field: Field?, value: T) {
        try {
            field!!.set(`object`, value)
        } catch (e: IllegalArgumentException) {
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
            type.newInstance() as T
        } catch (e: InstantiationException) {
            throw ReflectionError(e)
        } catch (e: IllegalAccessException) {
            throw ReflectionError(e)
        }
    }

    fun <T> newInstance(className: String?): T {
        return try {
            Class.forName(className).newInstance() as T
        } catch (e: InstantiationException) {
            throw ReflectionError(e)
        } catch (e: IllegalAccessException) {
            throw ReflectionError(e)
        } catch (e: ClassNotFoundException) {
            throw ReflectionError(e)
        }
    }

    /**
     * Type check version
     * @param className
     * @param type
     * @return null if instance doesn't match type
     */
    fun <T> newInstance(className: String?, type: Class<T>): T? {
        return try {
            val `object` = Class.forName(className).newInstance()
            if (type.isInstance(`object`)) {
                `object` as T
            } else null
        } catch (e: InstantiationException) {
            throw ReflectionError(e)
        } catch (e: IllegalAccessException) {
            throw ReflectionError(e)
        } catch (e: ClassNotFoundException) {
            throw ReflectionError(e)
        }
    }

    fun forName(className: String?): Class<*> {
        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw ReflectionError(e)
        }
    }

    fun hasName(className: String?): Boolean {
        try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            return false
        }
        return true
    }

    fun method(type: Class<*>, name: String?, vararg parameterTypes: Class<*>?): Method? {
        return try {
            type.getMethod(name, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            null // not found
        } catch (e: SecurityException) {
            throw ReflectionError(e)
        }
    }

    /** note : return null if return type is void  */
    operator fun invoke(obj: Any?, method: Method, vararg args: Any?): Any {
        return try {
            method.invoke(obj, args)
        } catch (e: IllegalAccessException) {
            throw ReflectionError(e)
        } catch (e: IllegalArgumentException) {
            throw ReflectionError(e)
        } catch (e: InvocationTargetException) {
            throw ReflectionError(e)
        }
    }

    fun <T> copy(out: T, `in`: T): T {
        for (field in out!!::class.java.fields) {
            if (Modifier.isPublic(field.modifiers) && !Modifier.isStatic(field.modifiers) && !Modifier.isTransient(
                    field.modifiers
                )
            ) {
                set(out, field, ReflectionHelper[`in`, field])
            }
        }
        return out
    }

    /**
     * Check if left type is same type or subtype of right type using same conventions
     * as java instanceof expression but apply to type.
     *
     * @param left
     * @param right
     * @return
     */
    fun instanceOf(left: Class<*>, right: Class<*>?): Boolean {
        return left.isAssignableFrom(right)
    }

    class ReflectionError(e: Throwable?) : Error(e)
}
