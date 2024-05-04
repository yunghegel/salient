package org.yunghegel.gdx.meshgen.math

import kotlin.math.pow


object Mathf {
    const val PI: Float = Math.PI.toFloat()
    const val HALF_PI: Float = PI * 0.5f
    const val TWO_PI: Float = PI + PI
    const val ONE_THIRD: Float = 1f / 3f
    const val QUARTER_PI: Float = PI / 4f
    const val ZERO_TOLERANCE: Float = 0.00001f
    const val TRIBONACCI_CONSTANT: Float = 1.8392868f
    val GOLDEN_RATIO: Float = (1f + sqrt(5f)) / 2f

    @JvmStatic
    fun abs(a: Float): Float {
        return kotlin.math.abs(a.toDouble()).toFloat()
    }

    fun clamp(value: Float, min: Float, max: Float): Float {
        var clampedValue = value
        clampedValue = if (clampedValue < min) min else clampedValue
        clampedValue = if (clampedValue > max) max else clampedValue
        return clampedValue
    }

    fun atan(a: Float): Float {
        return kotlin.math.atan(a.toDouble()).toFloat()
    }

    fun cos(a: Float): Float {
        return kotlin.math.cos(a.toDouble()).toFloat()
    }

    fun map(value: Float, from0: Float, to0: Float, from1: Float, to1: Float): Float {
        return from1 + (to1 - from1) * ((value - from0) / (to0 - from0))
    }

    fun clampInt(a: Int, min: Int, max: Int): Int {
        var a = a
        a = if (a < min) min else (if (a > max) max else a)
        return a
    }

    fun clamp01(a: Float): Float {
        return clamp(a, 0f, 1f)
    }

    fun max(vararg values: Float): Float {
        if (values.size == 0) return 0f
        var max = values[0]
        for (i in 1 until values.size) {
            max = kotlin.math.max(max.toDouble(), values[i].toDouble()).toFloat()
        }
        return max
    }

    fun min(vararg values: Float): Float {
        if (values.size == 0) return 0f
        var min = values[0]
        for (i in 1 until values.size) {
            min = kotlin.math.min(min.toDouble(), values[i].toDouble()).toFloat()
        }
        return min
    }

    fun pow(a: Float, b: Float): Float {
        return a.pow(b)
    }

    fun random(min: Int, max: Int): Int {
        return (Math.random() * (max - min)).toInt() + min
    }

    fun random(min: Float, max: Float): Float {
        return (Math.random() * (max - min)).toFloat() + min
    }

    fun round(a: Float): Float {
        return Math.round(a).toFloat()
    }

    fun roundToInt(a: Float): Int {
        return Math.round(a)
    }

    fun sin(a: Float): Float {
        return kotlin.math.sin(a.toDouble()).toFloat()
    }

    fun sqrt(a: Float): Float {
        return kotlin.math.sqrt(a.toDouble()).toFloat()
    }

    fun toOneDimensionalIndex(rowIndex: Int, colIndex: Int, numberOfColumns: Int): Int {
        return rowIndex * numberOfColumns + colIndex
    }

    fun toOneDimensionalIndex(rowIndex: Short, colIndex: Short, numberOfColumns: Short): Short {
        return (rowIndex * numberOfColumns + colIndex).toShort()
    }

    fun toRadians(angDeg: Float): Float {
        return Math.toRadians(angDeg.toDouble()).toFloat()
    }

    fun toShort(value: Int): Short {
        return (value and 0xffff).toShort()
    }
}
