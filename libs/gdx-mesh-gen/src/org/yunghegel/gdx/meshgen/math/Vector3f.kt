package org.yunghegel.gdx.meshgen.math

import com.badlogic.gdx.math.Vector3
import kotlin.math.pow
import kotlin.math.sqrt

class Vector3f : Vector3 {
    var x: Float
        get() = x
        private set(x) {
            super.x = x
        }
    var y: Float
        get() = y
        private set(y) {
            super.y = y
        }
    var z: Float
        get() = z
        set(z) {
            this.z = z
        }

    constructor()

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(v: Vector3f) {
        x = v.x
        y = v.y
        z = v.z
    }

    fun lengthSquared(): Float {
        return (x * x) + (y * y) + (z * z)
    }

    fun length(): Float {
        return sqrt(lengthSquared().toDouble()).toFloat()
    }

    fun distanceSquared(x: Float, y: Float, z: Float): Float {
        val dx = this.x - x
        val dy = this.y - y
        val dz = this.z - z
        return (dx * dx) + (dy * dy) + (dz * dz)
    }

    fun distanceSquared(v: Vector3f): Float {
        return distanceSquared(v.x, v.y, v.z)
    }

    fun distance(v: Vector3f): Float {
        return sqrt(distanceSquared(v).toDouble()).toFloat()
    }

    fun normalize(): Vector3f {
        var length = length()
        length = if (length == 0f) 1f else length
        return divide(length)
    }

    fun normalizeLocal(): Vector3f {
        var length = length()
        length = if (length == 0f) 1f else length
        return divideLocal(length)
    }

    fun dot(v: Vector3f): Float {
        return (x * v.x) + (y * v.y) + (z * v.z)
    }

    fun negate(): Vector3f {
        return Vector3f(-x, -y, -z)
    }

    override fun add(x: Float, y: Float, z: Float): Vector3f {
        return Vector3f(this.x + x, this.y + y, this.z + z)
    }

    fun addLocal(x: Float, y: Float, z: Float): Vector3f {
        this.x += x
        this.y += y
        this.z += z
        return this
    }

    fun add(v: Vector3f?): Vector3f {
        if (v == null) return Vector3f()
        return Vector3f(x + v.x, y + v.y, z + v.z)
    }

    fun addLocal(v: Vector3f): Vector3f {
        x += v.x
        y += v.y
        z += v.z
        return this
    }

    fun cross(v: Vector3f): Vector3f {
        val x = (y * v.z) - (z * v.y)
        val y = (z * v.x) - (x * v.z)
        val z = (x * v.y) - (y * v.x)
        return Vector3f(x, y, z)
    }

    fun subtractLocal(x: Float, y: Float, z: Float): Vector3f {
        this.x -= x
        this.y -= y
        this.z -= z
        return this
    }

    fun subtract(v: Vector3f): Vector3f {
        return Vector3f(x - v.x, y - v.y, z - v.z)
    }

    fun subtractLocal(v: Vector3f): Vector3f {
        x -= v.x
        y -= v.y
        z -= v.z
        return this
    }

    fun multLocal(v: Vector3f): Vector3f {
        x *= v.x
        y *= v.y
        z *= v.z
        return this
    }

    fun mult(scalar: Float): Vector3f {
        return Vector3f(x * scalar, y * scalar, z * scalar)
    }

    fun multLocal(scalar: Float): Vector3f {
        x *= scalar
        y *= scalar
        z *= scalar
        return this
    }

    fun mult(m: Matrix3f): Vector3f {
        val x0 = m.values[0] * x + m.values[1] * y + m.values[2] * z
        val y0 = m.values[3] * x + m.values[4] * y + m.values[5] * z
        val z0 = m.values[6] * x + m.values[7] * y + m.values[8] * z
        return Vector3f(x0, y0, z0)
    }

    fun multLocal(m: Matrix3f): Vector3f {
        val x0 = m.values[0] * x + m.values[1] * y + m.values[2] * z
        val y0 = m.values[3] * x + m.values[4] * y + m.values[5] * z
        val z0 = m.values[6] * x + m.values[7] * y + m.values[8] * z
        set(x0, y0, z0)
        return this
    }

    fun divide(scalar: Float): Vector3f {
        return Vector3f(x / scalar, y / scalar, z / scalar)
    }

    fun divideLocal(scalar: Float): Vector3f {
        x /= scalar
        y /= scalar
        z /= scalar
        return this
    }

    fun lerpLocal(finalVec: Vector3f, changeAmnt: Float): Vector3f {
        this.x = (1 - changeAmnt) * this.x + changeAmnt * finalVec.x
        this.y = (1 - changeAmnt) * this.y + changeAmnt * finalVec.y
        this.z = (1 - changeAmnt) * this.z + changeAmnt * finalVec.z
        return this
    }

    fun pow(a:Float,b:Float) : Float{
        return a.pow(b)
    }

    fun roundLocalDecimalPlaces(places: Int) {
        val a = Math.pow(10.0,places.toDouble()) as Float
        x = Math.round(x * a) / a
        y = Math.round(y * a) / a
        z = Math.round(z * a) / a
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + java.lang.Float.floatToIntBits(x)
        result = prime * result + java.lang.Float.floatToIntBits(y)
        result = prime * result + java.lang.Float.floatToIntBits(z)
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Vector3f
        if (java.lang.Float.floatToIntBits(x) != java.lang.Float.floatToIntBits(other.x)) return false
        if (java.lang.Float.floatToIntBits(y) != java.lang.Float.floatToIntBits(other.y)) return false
        return java.lang.Float.floatToIntBits(z) == java.lang.Float.floatToIntBits(other.z)
    }

    override fun set(x: Float, y: Float, z: Float): Vector3f {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(v: Vector3f): Vector3f {
        x = v.x
        y = v.y
        z = v.z
        return this
    }

    fun setX(x: Float): Vector3f {
        this.x = x
        return this
    }

    fun setY(y: Float): Vector3f {
        this.y = y
        return this
    }

    companion object {
        val ZERO: Vector3f = Vector3f(0f, 0f, 0f)
    }
}
