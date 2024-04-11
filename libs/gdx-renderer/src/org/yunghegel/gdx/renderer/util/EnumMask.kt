package org.yunghegel.gdx.renderer.util

class EnumMask<T:Enum<T>> {

    private var mask = 0

    fun add(vararg enums: T) {
        for (e in enums) {
            mask = mask or (1 shl e.ordinal)
        }
    }
    fun remove(vararg enums: T) {
        for (e in enums) {
            mask = mask and (1 shl e.ordinal).inv()
        }
    }

    fun contains(e: T) = mask and (1 shl e.ordinal) != 0

    fun clear() {
        mask = 0
    }

    fun whenContains(e: T, f: () -> Unit) {
        if (contains(e)) {
            f()
        }
    }



}

inline fun <reified T:Enum<T>> EnumMask<T>.forEach(f: (T) -> Unit) {
    T::class.java.enumConstants.forEach {
        if (contains(it)) {
            f(it)
        }
    }
}

fun <E:Enum<E>> maskOf(vararg enums: E) : EnumMask<E> {
    val mask = EnumMask<E>()
    mask.add(*enums)
    return mask
}