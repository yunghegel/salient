package org.yunghegel.gdx.utils.data

import com.badlogic.gdx.math.FloatCounter
import com.badlogic.gdx.utils.Array

/** An Array that will automatically remove oldest items when beyond max size  */
class RollingFloatArray {
    var floatCounter: FloatCounter? = null

    constructor()

    constructor(maxSize: Int) {
        this.maxSize = maxSize
        floatCounter = FloatCounter(maxSize)
    }

    var items: Array<Float> = Array()
    var maxSize: Int = 60

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
}
