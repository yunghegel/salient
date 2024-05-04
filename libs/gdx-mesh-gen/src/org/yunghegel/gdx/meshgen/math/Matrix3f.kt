package org.yunghegel.gdx.meshgen.math

import com.badlogic.gdx.math.Matrix3
import org.yunghegel.gdx.meshgen.math.Mathf.abs

class Matrix3f : Matrix3 {
    @JvmField
    var values: FloatArray = FloatArray(9)

    constructor()

    constructor(values: FloatArray) {
        set(values)
    }

    constructor(
        m00: Float,
        m01: Float,
        m02: Float,
        m10: Float,
        m11: Float,
        m12: Float,
        m20: Float,
        m21: Float,
        m22: Float
    ) {
        values[M00] = m00
        values[M01] = m01
        values[M02] = m02
        values[M10] = m10
        values[M11] = m11
        values[M12] = m12
        values[M20] = m20
        values[M21] = m21
        values[M22] = m22
    }

    constructor(m: Matrix3f) {
        set(m)
    }

    fun mult(m: Matrix3f): Matrix3f {
        val result = Matrix3f()
        val a = values
        val b = m.values
        val c = result.values
        c[M00] = a[M00] * b[M00] + a[M01] * b[M10] + a[M02] * b[M20]
        c[M01] = a[M00] * b[M01] + a[M01] * b[M11] + a[M02] * b[M21]
        c[M02] = a[M00] * b[M02] + a[M01] * b[M12] + a[M02] * b[M22]
        c[M10] = a[M10] * b[M00] + a[M11] * b[M10] + a[M12] * b[M20]
        c[M11] = a[M10] * b[M01] + a[M11] * b[M11] + a[M12] * b[M21]
        c[M12] = a[M10] * b[M02] + a[M11] * b[M12] + a[M12] * b[M22]
        c[M20] = a[M20] * b[M00] + a[M21] * b[M10] + a[M22] * b[M20]
        c[M21] = a[M20] * b[M01] + a[M21] * b[M11] + a[M22] * b[M21]
        c[M22] = a[M20] * b[M02] + a[M21] * b[M12] + a[M22] * b[M22]
        return result
    }

    fun multLocal(m: Matrix3f): Matrix3f {
        set(this.mult(m))
        return this
    }

    fun add(m: Matrix3f): Matrix3f {
        val result = Matrix3f(this)
        result.addLocal(m)
        return result
    }

    fun addLocal(m: Matrix3f): Matrix3f {
        values[M00] += m.values[M00]
        values[M01] += m.values[M01]
        values[M02] += m.values[M02]
        values[M10] += m.values[M10]
        values[M11] += m.values[M11]
        values[M12] += m.values[M12]
        values[M20] += m.values[M20]
        values[M21] += m.values[M21]
        values[M22] += m.values[M22]
        return this
    }

    fun add(
        m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float, m20: Float, m21: Float,
        m22: Float
    ): Matrix3f {
        val result = Matrix3f(this)
        result.addLocal(m00, m01, m02, m10, m11, m12, m20, m21, m22)
        return result
    }

    fun addLocal(
        m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float, m20: Float, m21: Float,
        m22: Float
    ): Matrix3f {
        values[M00] += m00
        values[M01] += m01
        values[M02] += m02
        values[M10] += m10
        values[M11] += m11
        values[M12] += m12
        values[M20] += m20
        values[M21] += m21
        values[M22] += m22
        return this
    }

    fun mult(scalar: Float): Matrix3f {
        val result = Matrix3f(this)
        result.multLocal(scalar)
        return result
    }

    fun multLocal(scalar: Float): Matrix3f {
        values[M00] *= scalar
        values[M01] *= scalar
        values[M02] *= scalar
        values[M10] *= scalar
        values[M11] *= scalar
        values[M12] *= scalar
        values[M20] *= scalar
        values[M21] *= scalar
        values[M22] *= scalar
        return this
    }

    override fun det(): Float {
        return values[M00] * values[M11] * values[M22] + values[M01] * values[M12] * values[M20] + values[M02] * values[M10] * values[M21] - values[M00] * values[M12] * values[M21] - values[M01] * values[M10] * values[M22] - values[M02] * values[M11] * values[M20]
    }

    fun abs(): Matrix3f {
        val result = Matrix3f(this)
        result.absLocal()
        return result
    }

    fun absLocal(): Matrix3f {
        values[M00] = abs(values[M00])
        values[M01] = abs(values[M01])
        values[M02] = abs(values[M02])
        values[M10] = abs(values[M10])
        values[M11] = abs(values[M11])
        values[M12] = abs(values[M12])
        values[M20] = abs(values[M20])
        values[M21] = abs(values[M21])
        values[M22] = abs(values[M22])
        return this
    }

    fun zero(): Matrix3f {
        values[M22] = 0f
        values[M21] = values[M22]
        values[M20] = values[M21]
        values[M12] = values[M20]
        values[M11] = values[M12]
        values[M10] = values[M11]
        values[M02] = values[M10]
        values[M01] = values[M02]
        values[M00] = values[M01]
        return this
    }

    fun set(
        m00: Float, m01: Float, m02: Float, m10: Float, m11: Float, m12: Float, m20: Float, m21: Float,
        m22: Float
    ): Matrix3f {
        values[M00] = m00
        values[M01] = m01
        values[M02] = m02
        values[M10] = m10
        values[M11] = m11
        values[M12] = m12
        values[M20] = m20
        values[M21] = m21
        values[M22] = m22
        return this
    }

    fun set(m: Matrix3f): Matrix3f {
        values[M00] = m.values[M00]
        values[M01] = m.values[M01]
        values[M02] = m.values[M02]
        values[M10] = m.values[M10]
        values[M11] = m.values[M11]
        values[M12] = m.values[M12]
        values[M20] = m.values[M20]
        values[M21] = m.values[M21]
        values[M22] = m.values[M22]
        return this
    }

    override fun set(values: FloatArray): Matrix3f {
        System.arraycopy(values, 0, this.values, 0, this.values.size)
        return this
    }

    fun setValueAt(value: Float, index: Int): Matrix3f {
        values[index] = value
        return this
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + values.contentHashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Matrix3f
        return values.contentEquals(other.values)
    }

    companion object {
        val ZERO: Matrix3f = Matrix3f(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val UNIT: Matrix3f = Matrix3f(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val ONE: Matrix3f = Matrix3f(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)

        const val M00: Int = 0
        const val M01: Int = 1
        const val M02: Int = 2
        const val M10: Int = 3
        const val M11: Int = 4
        const val M12: Int = 5
        const val M20: Int = 6
        const val M21: Int = 7
        const val M22: Int = 8
    }
}
