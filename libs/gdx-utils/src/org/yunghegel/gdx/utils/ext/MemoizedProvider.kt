package org.yunghegel.gdx.utils.ext


import org.checkerframework.checker.units.qual.A
import org.yunghegel.gdx.utils.data.EnumBitmask.Companion.getValue
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty


// Time-based cache entry wrapper
data class CacheEntry<out T>(
    val value: T,
    val timestamp: Long = System.currentTimeMillis()
)

// Main memoization class with configurable cache strategy
class Memoized<P, R>(
    private val computation: (P) -> R,
    private val maxCacheSize: Int = 100,
    private val timeToLive: Long = 1000 // 1 second default TTL
) {
    private val cache = object : LinkedHashMap<P, CacheEntry<R>>(maxCacheSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<P, CacheEntry<@UnsafeVariance R>>): Boolean {
            return size > maxCacheSize
        }
    }

    @Synchronized
    fun invoke(param: P): R {
        val currentTime = System.currentTimeMillis()
        val cached = cache[param]

        // Check if we have a valid cached value
        if (cached != null && (currentTime - cached.timestamp) < timeToLive) {
            return cached.value
        }

        // Compute new value and cache it
        return computation(param).also { result ->
            cache[param] = CacheEntry(result, currentTime)
        }
    }

    fun clearCache() {
        cache.clear()
    }

    fun getCacheSize(): Int = cache.size
}

class Memoized2<P, R, T>(
    private val computation: (P, R) -> T,
    private val maxCacheSize: Int = 100,
    private val timeToLive: Long = 1000 // 1 second default TTL
) : ReadWriteProperty<Any, ((@UnsafeVariance P, @UnsafeVariance R) -> @UnsafeVariance T)> {
    private val cache = object : LinkedHashMap<Pair<P, R>, CacheEntry<T>>(maxCacheSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<Pair<P, R>, CacheEntry<@UnsafeVariance T>>): Boolean {
            return size > maxCacheSize
        }
    }


    @Synchronized
    fun invoke(param1: P, param2: R): T {
        val currentTime = System.currentTimeMillis()
        val cached = cache[param1 to param2]

        // Check if we have a valid cached value
        if (cached != null && (currentTime - cached.timestamp) < timeToLive) {
            println("Cache hit")
            return cached.value
        }

        // Compute new value and cache it
        return computation(param1, param2).also { result ->
            cache[param1 to param2] = CacheEntry(result, currentTime)
        }

    }

    fun clearCache() {
        cache.clear()
    }

    fun getCacheSize(): Int = cache.size


    override fun getValue(thisRef: Any, property: KProperty<*>): ((P, R) -> T) {
        return { p, r -> invoke(p, r) }
    }

    override fun setValue(
        thisRef: Any,
        property: KProperty<*>,
        value: ((@UnsafeVariance P, @UnsafeVariance R) -> @UnsafeVariance T)
    ) {
        throw UnsupportedOperationException("Setting the value of a memoized function is not supported")
    }

}

// Extension function to create memoized functions easily
fun <P, R> ((P) -> R).memoized(
    maxCacheSize: Int = 100,
    timeToLive: Long = 1000
): (P) -> R {
    val memoized = Memoized(this, maxCacheSize, timeToLive)
    return { param -> memoized.invoke(param) }
}

// Extension function for nullary functions
fun <R> (() -> R).memoized(
    timeToLive: Long = 1000
): () -> R {
    val memoized = Memoized<Unit, R>({ this() }, 1, timeToLive)
    return { memoized.invoke(Unit) }
}

fun <P, R, T> ((P, R) -> T).memoized(
    maxCacheSize: Int = 100,
    timeToLive: Long = 1000
): (P, R) -> T {
    val memoized = Memoized2(this, maxCacheSize, timeToLive)
    return { p, r -> memoized.invoke(p, r) }
}


// Usage example
fun main() {
    var value = { x: Int, y: Int -> x + y }.memoized()

    println(value(1, 2)) // 3
    println(value(1, 2)) // 3


}

