package org.yunghegel.gdx.utils.data

class CircularList<T>(val size: Int = 100) {
    private val list = mutableListOf<T>()
    private var index = 0

    fun add(element: T) {
        if (list.size < size) {
            list.add(element)
        } else {
            list[index] = element
        }
        index = (index + 1) % size
    }

    fun get(index: Int): T {
        return list[(this.index + index) % size]
    }

    fun forEach(action: (T) -> Unit) {
        list.forEach(action)
    }

    fun clear() {
        list.clear()
    }
}