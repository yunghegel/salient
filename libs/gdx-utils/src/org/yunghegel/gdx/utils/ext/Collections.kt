package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.math.MathUtils
import ktx.collections.GdxArray

fun <T> GdxArray<T>.removeAt(index: Int): T {
    val removed = this[index]
    this.removeIndex(index)
    return removed
}

fun <T> Array<T>.toGdxArray(): GdxArray<T> {
    val gdxArray = GdxArray<T>(this.size)
    this.forEach { gdxArray.add(it) }
    return gdxArray
}

inline fun <T> Iterable<T>.each(action: (T) -> Unit): Unit {
    for (element in this) action(element)
}

inline fun <T> Iterable<T>.withEach(action: T.() -> Unit): Unit {
    for (element in this) element.action()
}

fun <T> Iterable<T>.eachApply(action: T.() -> Unit): Unit {
    for (element in this) element.apply {
        action()
    }
}

/**
 * Adds all elements of the given iterable to the given array.
 */
fun <T> addAll(array: GdxArray<T>, elements: Iterable<T>) {
    for (element in elements) array.add(element)
}

/**
 * Returns a new array containing the elements of the given iterable.
 */
fun <T> array(elements: Iterable<T>): GdxArray<T> {
    val array = GdxArray<T>()
    addAll(array, elements)
    return array
}

/**
 * Returns a new array containing the elements of the given array.
 */
fun <T> array(elements: Array<T>): GdxArray<T> {
    val array = GdxArray<T>()
    for (element in elements) array.add(element)
    return array
}

/**
 * Returns a random element from the array.
 */
fun <T> any(array: GdxArray<T>): T? {
    if (array.size > 0) {
        return array[(array.size * MathUtils.random()).toInt()]
    }
    return null
}

fun arraySwap(arr: Array<Any?>, i1: Int, i2: Int) {
    val tmp = arr[i1]
    arr[i1] = arr[i2]
    arr[i2] = tmp
}

fun reverseArray(arr: Array<Any?>) {
    var i = 0
    var j = arr.size - 1
    while (i < j) {
        val tmp = arr[i]
        arr[i] = arr[j]
        arr[j] = tmp
        i++
        j--
    }
}

fun <E> MutableCollection<E>.addIfNotNull(element: E?,ifNull: (() -> Unit)?=null){
    if (element != null) {
        add(element)
    } else {
        ifNull?.invoke()
    }
}

fun <E> MutableCollection<E>.addIfNotPresent(element: E,ifPresent: ((E) -> Unit)?=null) : Boolean {
    if (!contains(element)) {
        add(element)
        return true
    } else {
        ifPresent?.invoke(element)
        return false
    }
}

operator fun <T:Any> MutableList<T>.plus(element: T) {
    add(element)
}

operator fun <T> MutableCollection<T>.minusAssign(element: T) {
    remove(element)
}

fun <E> Collection<E>.singleOrNull(): E? {
    if (size == 1) {
        return first()
    }
    return null
}

class MapBuilder<K,V> {
    private val map = mutableMapOf<K,V>()

    fun put(key: K, value: V) {
        map[key] = value
    }

    fun build(): Map<K,V> {
        return map
    }
}

fun <K,V> buildMap(block: MapBuilder<K,V>.() -> Unit): Map<K,V> {
    val builder = MapBuilder<K,V>()
    builder.block()
    return builder.build()
}