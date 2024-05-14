package org.yunghegel.gdx.utils
/**
 * Minimum memoization implementation
 * no eviction strategy / no fixed size
 * unwise to call on functions with infinite domain or on every frame
 */

fun <T, R> ((T) -> R).memoize(): (T) -> R {
    val values = HashMap<T, R>()
    return { x: T ->
        values.getOrPut(x) {
            this(x)
        }
    }
}

/**
 * Memoize with fixed size
 */
fun <T, R> ((T) -> R).memoize(size: Int): (T) -> R {
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
fun <T, R> ((T) -> R).memoize(size: Int, eviction: (Map<T, R>) -> Unit): (T) -> R {
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