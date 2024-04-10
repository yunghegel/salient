package org.yunghegel.gdx.utils.data

interface Mask {

    val mask : Int

    fun set(value: Int): Int {
        return value or mask
    }

    fun clear(value: Int): Int {
        return value and mask.inv()
    }

    fun toggle(value: Int): Int {
        return value xor mask
    }

    fun has(value: Int): Boolean {
        return value and mask != 0
    }

    fun allOf(value: Int): Boolean {
        return value and mask == mask
    }

    fun noneOf(value: Int): Boolean {
        return value and mask == 0
    }

    fun maskOf(vararg values: Int): Int {
        var mask = 0
        for (value in values) {
            mask = mask or value
        }
        return mask
    }

}