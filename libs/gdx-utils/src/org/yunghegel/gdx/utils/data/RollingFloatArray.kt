package org.yunghegel.gdx.utils.data

import com.badlogic.gdx.math.FloatCounter
import com.badlogic.gdx.utils.Array

/** An Array that will automatically remove oldest items when beyond max size  */
class RollingFloatArray(var maxSize: Int) {
    var floatCounter: FloatCounter? = null


    var items: Array<Float> = Array()

    fun clear() {
        items.clear()
    }

    fun add(item: Float) {
        items.add(item)
        floatCounter!!.put(item)
        if (items.size > maxSize) {
            items.removeIndex(0)
        }
    }

    val average: Float
        get() {
            var total = 0f
            for (item in items) {
                total += item
            }
            return total / items.size
        }

    init {
        floatCounter = FloatCounter(maxSize)
    }
}
