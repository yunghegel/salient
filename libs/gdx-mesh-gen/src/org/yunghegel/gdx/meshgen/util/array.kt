package org.yunghegel.gdx.meshgen.util


inline fun <reified T> Array<T>.copy() : Array<T> {
    val copy = Array(this.size) { this[it] }
    return copy
}

inline fun <reified T> Array<T>.insertAt(index: Int, element: T) : Array<T> {
    val copy = Array(this.size + 1) { if (it < index) this[it] else if (it == index) element else this[it - 1] }
    return copy
}

inline fun <reified T> Array<T>.insertRangeAt(index: Int, range: Array<T>) : Array<T> {
    val copy = Array(this.size + range.size) { if (it < index) this[it] else if (it < index + range.size) range[it - index] else this[it - range.size] }
    return copy
}
