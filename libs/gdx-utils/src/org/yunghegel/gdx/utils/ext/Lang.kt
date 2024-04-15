package org.yunghegel.gdx.utils.ext

inline infix fun (() -> Unit).then(crossinline other: () -> Unit) = { ->
    this()
    other()
}

/**
 * Composes two consumers sequentially.
 */
inline infix fun <T> ((T) -> Unit).then(crossinline other: (T) -> Unit) = { t: T ->
    this(t)
    other(t)
}

/**
 * Composes two consumers sequentially.
 */
inline infix fun <T, U> ((T, U) -> Unit).then(crossinline other: (T, U) -> Unit) = { t: T, u: U ->
    this(t, u)
    other(t, u)
}

//Builder style handler for operations which handle both cases of null or not null
class NullOrNotNull<T> {
    private var notNull: ((T) -> Unit)? = null
    private var isNull: (() -> Unit)? = null

    fun notNull(block: (T) -> Unit) {
        notNull = block
    }

    fun isNull(block: () -> Unit) {
        isNull = block
    }

    fun handle(value: T?) {
        if (value != null) {
            notNull?.invoke(value)
        } else {
            isNull?.invoke()
        }
    }
}

fun <T> T?.nullOrNotNull(block: NullOrNotNull<T>.() -> Unit) {
    val handler = NullOrNotNull<T>()
    handler.block()
    handler.handle(this)
}