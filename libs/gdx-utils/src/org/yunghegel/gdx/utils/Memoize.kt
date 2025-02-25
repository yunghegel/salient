package org.yunghegel.gdx.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Minimum memoization implementation
 * no eviction strategy / no fixed size
 * unwise to call on functions with infinite domain or on every frame
 */



/**
 * Memoize with fixed size
 */
fun <T, R> ((T) -> R).memoize(size: Int = 100): (T) -> R {
    val values = HashMap<T, R>()
    return { x: T ->
        if (values.size == size) {
            values.clear()
        }
        values.getOrPut(x) {
            this(x)
        }
    }
}

/**
 * Memoize with eviction strategy
 */
fun <T, R> ((T) -> R).memoize(size: Int = 100, eviction: (Map<T, R>) -> Unit): (T) -> R {
    val values = LinkedHashMap<T, R>(size)
    return { x: T ->
        if (values.size == size) {
            eviction(values)
        }
        values.getOrPut(x) {
            this(x)
        }
    }
}

fun <T, R> ((T, T) -> R).memoize(size: Int = 100, eviction: ((Map<T, R>) -> Unit)? = null): (T, T) -> R {
    val values = LinkedHashMap<T, R>(size)
    return { x: T, y:T ->
        if (values.size == size) {
            if (eviction != null) {
                eviction(values)
            } else {
                values.clear()
            }
        }
        values.getOrPut(x) {
            this(x,y)
        }
    }
}

fun <T, R> ((T, T, T) -> R).memoize(size: Int = 100, eviction: (Map<T, R>) -> Unit): (T, T, T) -> R {
    val values = LinkedHashMap<T, R>(size)
    return { x: T, y:T, z:T ->
        if (values.size == size) {
            eviction(values)
        }
        values.getOrPut(x) {
            this(x,y,z)
        }
    }
}

class Quad<A,B,C,D>(private val a: A, private val b: B,private val c: C,private val d:D) {


    fun first(): A = a
    fun second(): B = b
    fun third(): C = c
    fun fourth(): D = d

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Quad<*, *, *, *>

        if (a != other.a) return false
        if (b != other.b) return false
        if (c != other.c) return false
        if (d != other.d) return false

        return true
    }

    override fun hashCode(): Int {
        var result = a?.hashCode() ?: 0
        result = 31 * result + (b?.hashCode() ?: 0)
        result = 31 * result + (c?.hashCode() ?: 0)
        result = 31 * result + (d?.hashCode() ?: 0)
        return result
    }

}

fun <T,R> ((T,T,T,T)->R).memoize(size: Int, eviction: (Map<Quad<T,T,T,T>, R>) -> Unit): (T,T,T,T) -> R {
    val values = LinkedHashMap<Quad<T,T,T,T>, R>(size)
    return { a: T, b:T, c:T, d:T ->
        if (values.size == size) {
            eviction(values)
        }
        values.getOrPut(Quad(a,b,c,d)) {
            this(a,b,c,d)
        }
    }
}

fun <A,B,R> ((A,B)->R).memoize(): (A,B)->R {
    val values = HashMap<Pair<A,B>,R>()
    return { a:A, b:B ->
        values.getOrPut(Pair(a,b)) {
            this(a,b)
        }
    }
}

fun <A,B,C,R> ((A,B,C)->R).memoize(): (A,B,C)->R {
    val values = HashMap<Triple<A,B,C>,R>()
    return { a:A, b:B, c:C ->
        values.getOrPut(Triple(a,b,c)) {
            this(a,b,c)
        }
    }
}

class MemoizedValue<T>(val value : T) : ReadWriteProperty<Any, T> {
    val map = HashMap<Any, T>(100)
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return map.getOrPut(thisRef) {
            value
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (value != this.value) {
            map[thisRef] = value
        }
    }
}

fun <T> memoize(value: T) = MemoizedValue(value)