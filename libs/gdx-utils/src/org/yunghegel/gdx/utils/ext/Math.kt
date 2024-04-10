package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.math.*
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.NumberUtils
import org.yunghegel.gdx.utils.Pools
import kotlin.math.atan2
import kotlin.math.pow

private val crsTmp = Vector3()
private val tmp = Vector3()
private val tmp2 = Vector3()
private val tmp3 = Vector3()

private val mtx = Matrix4()
private val q = Quaternion()

fun Int.pow(exp: Int): Int {
    var result = 1
    for (i in 0 until exp) {
        result *= this
    }
    return result
}

fun barryCentric(p1: Vector3, p2: Vector3, p3: Vector3, pos: Vector2): Float {
    val det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z)
    val l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det
    val l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det
    val l3 = 1.0f - l1 - l2
    return l1 * p1.y + l2 * p2.y + l3 * p3.y
}

fun getDownFromVector(vec: Vector3): Vector3 {
    crsTmp.set(vec).nor()
    crsTmp.crs(Vector3.Y) // cross by world up to get a right handle angle vector (facing forward)
    tmp.set(vec).nor()
    return tmp.crs(crsTmp).nor() // get the relative "down"
}

fun getUpFromVector(vec: Vector3): Vector3 {
    crsTmp.set(vec).nor()
    crsTmp.crs(Vector3.Y) // cross by world up to get a right handle angle vector (facing forward)
    tmp.set(vec).nor()
    return crsTmp.crs(tmp).nor() // get the relative "down"
}

fun getRightFromVector(vec: Vector3): Vector3 {
    crsTmp.set(vec).nor()
    crsTmp.crs(Vector3.Y) // cross by world up to get a right handle angle vector
    //Tools.print(crsTmp, "right");
    //Tools.print(vec, "original");
    return crsTmp.nor()
}

fun rotateAround(position: Vector3, axis: Vector3?, angle: Float) {
    q.setFromAxis(axis, angle)
    mtx.set(q)
    position.prj(mtx)
}

fun rotateAround(position: Vector3, axis: Vector3?, angle: Float, origin: Vector3?) {
    q.setFromAxis(axis, angle)
    mtx.set(q)
    position.sub(origin)
    position.prj(mtx)
    position.add(origin)
}

fun rotateAround(position: Vector3?, axis: Vector3?, angle: Float, origin: Vector3?, out: Vector3) {
    q.setFromAxis(axis, angle)
    mtx.set(q)
    out.set(position)
    out.sub(origin)
    out.prj(mtx)
    out.add(origin)
}

fun project(pVectorA: Vector3, pVectorB: Vector3): Vector3 {
    val dotProduct = pVectorA.dot(pVectorB)

    return Vector3(dotProduct * pVectorB.x, dotProduct * pVectorB.y, dotProduct * pVectorB.z)
}

fun getSurfaceNormal(v1: Vector3, v2: Vector3, v3: Vector3): Vector3 {
    return getSurfaceNormal(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z)
}

fun getSurfaceNormal(
    x1: Float,
    y1: Float,
    z1: Float,
    x2: Float,
    y2: Float,
    z2: Float,
    x3: Float,
    y3: Float,
    z3: Float
): Vector3 {
    val u = Vector3(x2 - x1, y2 - y1, z2 - z1)
    return u.crs(x3 - x1, y3 - y1, z3 - z1).nor()
}

fun getCentroid(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Vector2 {
    return Vector2(x1 + x2 + x3, y1 + y2 + y3).scl(0.333333333f)
}

fun getCentroid(v1: Vector2, v2: Vector2, v3: Vector2): Vector2 {
    return Vector2(v1.x + v2.x + v3.x, v1.y + v2.y + v3.y).scl(0.333333333f)
}

fun getCentroid(v1: Vector3, v2: Vector3, v3: Vector3): Vector3 {
    return Vector3(v1.x + v2.x + v3.x, v1.y + v2.y + v3.y, v1.z + v2.z + v3.z).scl(0.333333333f)
}

fun getCentroid(v1: Vector3, v2: Vector3, v3: Vector3, v4: Vector3): Vector3 {
    return Vector3(
        v1.x + v2.x + v3.x + v4.x,
        v1.y + v2.y + v3.y + v4.y,
        v1.z + v2.z + v3.z + v4.z
    ).scl(0.25f)
}

fun getMidpoint(v1: Vector3, v2: Vector3): Vector3 {
    return Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z).scl(0.5f)
}

fun getMidpoint(v1: Vector2, v2: Vector2): Vector2 {
    return Vector2(v1.x + v2.x, v1.y + v2.y).scl(0.5f)
}

fun Rectangle.topLeft(): Vector2 {
    return Vector2(x, y + height)
}

fun Rectangle.topRight(): Vector2 {
    return Vector2(x + width, y + height)
}

fun Rectangle.bottomLeft(): Vector2 {
    return Vector2(x, y)
}

fun Rectangle.bottomRight(): Vector2 {
    return Vector2(x + width, y)
}



object MathUtils {
    const val PI: Float = Math.PI.toFloat()
    val HALF_PI: Float = PI * 0.5f
    val TWO_PI: Float = PI + PI
    const val ONE_THIRD: Float = 1f / 3f
    val QUARTER_PI: Float = PI / 4f
    const val ZERO_TOLERANCE: Float = 0.00001f
    const val TRIBONACCI_CONSTANT: Float = 1.8392868f
    val GOLDEN_RATIO: Float = (1f + sqrt(5f)) / 2f

    private val tmpVec = Vector3()

    /**
     * Returns a vector which represents the direction a given matrix is facing.
     *
     * @param transform - in
     * @param out       - out
     */
    fun getDirection(transform: Matrix4?, out: Vector3) {
        tmpVec.set(Vector3.Z)
        out.set(tmpVec.rot(transform).nor())
    }

    /**
     * Gets the world position of modelInstance and sets it on the out vector
     *
     * @param transform modelInstance transform
     * @param out       out vector to be populated with position
     */
    fun getPosition(transform: Matrix4, out: Vector3?) {
        transform.getTranslation(out)
    }

    /**
     * Gets the world position of modelInstance and sets it on the out vector
     *
     * @param transform modelInstance transform
     * @param out       out vector to be populated with position
     */
    fun getWorldCoordinates(transform: Matrix4, out: Vector3?) {
        transform.getTranslation(out)
    }

    /**
     * Returns a random sign, either 1 or -1
     */
    fun randomSign(): Int {
        return if (Math.random() > 0.5) 1 else -1
    }

    /**
     * bitwise to convert short to unsigned representation
     * @param s -the short to convert
     * @return the unsigned representation of the short
     */
    fun unsignedShort(s: Short): Int {
        return s.toInt() and 0xFFFF
    }

    /**
     * Returns the absolute value of a float
     * @param a - the float to get the absolute value of
     * @return the absolute value of a float
     */
    fun abs(a: Float): Float {
        return kotlin.math.abs(a.toDouble()).toFloat()
    }

    /**
     * Clamp a value between a minimum float and maximum float value
     * @param value - the value to clamp
     * @param min - the minimum value
     * @param max - the maximum value
     * @return the clamped value
     */
    fun clamp(value: Float, min: Float, max: Float): Float {
        var clampedValue = value
        clampedValue = kotlin.math.max(clampedValue.toDouble(), min.toDouble()).toFloat()
        clampedValue = kotlin.math.min(clampedValue.toDouble(), max.toDouble()).toFloat()
        return clampedValue
    }

    /**
     * Returns the arctangent of the specified float value.
     * @param a - the value whose arc tangent is to be returned.
     * @return the arctangent of the specified float value.
     */
    fun atan(a: Float): Float {
        return kotlin.math.atan(a.toDouble()).toFloat()
    }

    /**
     * Returns the cosine of the specified angle in radians.
     * @param a - an angle, in radians.
     * @return the cosine of the specified angle in radians.
     */
    fun cos(a: Float): Float {
        return kotlin.math.cos(a.toDouble()).toFloat()
    }

    /**
     * Maps a value from one range to another
     * @param value - the value to map
     * @param from0 - the lower bound of the value's current range
     * @param to0 - the upper bound of the value's current range
     * @param from1 - the lower bound of the value's target range
     * @param to1 - the upper bound of the value's target range
     * @return the mapped value
     */
    fun map(value: Float, from0: Float, to0: Float, from1: Float, to1: Float): Float {
        return from1 + (to1 - from1) * ((value - from0) / (to0 - from0))
    }

    fun clampInt(a: Int, min: Int, max: Int): Int {
        var a = a
        a = if (a < min) min else (kotlin.math.min(a.toDouble(), max.toDouble()).toInt())
        return a
    }

    /**
     * Returns the value clamped between 0 and 1
     * @param a - the value to clamp
     * @return the value clamped between 0 and 1
     */
    fun clamp01(a: Float): Float {
        return clamp(a, 0f, 1f)
    }

    /**
     * Returns the maximum value of an array of floats
     * @param values - the array of floats
     * @return the maximum value of the array
     */
    fun max(vararg values: Float): Float {
        if (values.size == 0) return 0f
        var max = values[0]
        for (i in 1 until values.size) {
            max = kotlin.math.max(max.toDouble(), values[i].toDouble()).toFloat()
        }
        return max
    }

    /**
     * The minimum value of an array of floats
     * @param values - the array of floats
     * @return the minimum value of the array
     */
    fun min(vararg values: Float): Float {
        if (values.size == 0) return 0f
        var min = values[0]
        for (i in 1 until values.size) {
            min = kotlin.math.min(min.toDouble(), values[i].toDouble()).toFloat()
        }
        return min
    }


    /**
     * Returns the value of the first argument raised to the power of the second argument
     */
    fun pow(a: Float, b: Float): Float {
        return a.pow(b) as Float
    }

    /**
     * Returns a random integer between min and max
     * @param min - the minimum value
     * @param max - the maximum value
     * @return a random integer between min and max
     */
    fun random(min: Int, max: Int): Int {
        return (Math.random() * (max - min)).toInt() + min
    }

    /**
     * Returns a random float between 0 and 1
     * @return a random float between 0 and 1
     */
    fun random(min: Float, max: Float): Float {
        return (Math.random() * (max - min)).toFloat() + min
    }

    /**
     * Rounds a float to the nearest integer
     * @param a - the float to round
     * @return the rounded float
     */
    fun round(a: Float): Float {
        return Math.round(a).toFloat()
    }

    /**
     * Rounds a float to the nearest integer
     * @param a - the float to round
     * @return the rounded integer
     */
    fun roundToInt(a: Float): Int {
        return Math.round(a)
    }

    /**
     * Returns the sine of a float
     * @param a - the float to get the sine of
     * @return the sine of the float
     */
    fun sin(a: Float): Float {
        return kotlin.math.sin(a.toDouble()).toFloat()
    }

    /**
     * Returns the square root of a float
     * @param a - the float to square root
     * @return the square root of the float
     */
    fun sqrt(a: Float): Float {
        return kotlin.math.sqrt(a.toDouble()).toFloat()
    }

    /**
     * Converts a two dimensional index to a one dimensional index
     * @param rowIndex - the row index
     * @param colIndex - the column index
     *
     * @param numberOfColumns - the number of columns in the two dimensional array
     * @return the one dimensional index
     */
    fun toOneDimensionalIndex(rowIndex: Int, colIndex: Int, numberOfColumns: Int): Int {
        return rowIndex * numberOfColumns + colIndex
    }

    /**
     * Converts a two dimensional index to a one dimensional index
     * @param rowIndex - the row index
     * @param colIndex - the column index
     *
     * @param numberOfColumns - the number of columns in the two dimensional array
     * @return the one dimensional index
     */
    fun toOneDimensionalIndex(rowIndex: Short, colIndex: Short, numberOfColumns: Short): Short {
        return (rowIndex * numberOfColumns + colIndex).toShort()
    }

    /**
     * Converts degrees to radians
     * @param angDeg - the angle in degrees
     * @return the angle in radians
     */
    fun toRadians(angDeg: Float): Float {
        return Math.toRadians(angDeg.toDouble()).toFloat()
    }

    private val tmpQuat = Quaternion()

    fun project(v1: Vector3, v2: Vector3, v3: Vector3, direction: Vector3?) {
        val centroid = Vector3(v1.x + v2.x + v3.x, v1.y + v2.y + v3.y, v1.z + v2.z + v3.z).scl(0.333333333f)

        v1.sub(centroid)
        v2.sub(centroid)
        v3.sub(centroid)

        val surfaceNormal: Vector3 = getSurfaceNormal(v1, v2, v3)

        val quaternion: Quaternion = tmpQuat.setFromCross(surfaceNormal, direction)

        quaternion.transform(centroid)

        quaternion.transform(v1).add(centroid)
        quaternion.transform(v2).add(centroid)
        quaternion.transform(v3).add(centroid)
    }

    /**
     * Calculates the projection of vectorA into vectorB
     *
     * @param pVectorA the 3D vector to be projected
     * @param pVectorB the unit length 3D vector representing the axis
     * @return The 3D projection vector
     */
    fun project(pVectorA: Vector3, pVectorB: Vector3): Vector3 {
        val dotProduct = pVectorA.dot(pVectorB)

        return Vector3(dotProduct * pVectorB.x, dotProduct * pVectorB.y, dotProduct * pVectorB.z)
    }

    fun getSurfaceNormal(v1: Vector3, v2: Vector3, v3: Vector3): Vector3 {
        return getSurfaceNormal(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z)
    }

    fun getSurfaceNormal(
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
        x3: Float,
        y3: Float,
        z3: Float
    ): Vector3 {
        val u = Vector3(x2 - x1, y2 - y1, z2 - z1)
        return u.crs(x3 - x1, y3 - y1, z3 - z1).nor()
    }

    fun getCentroid(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Vector2 {
        return Vector2(x1 + x2 + x3, y1 + y2 + y3).scl(0.333333333f)
    }

    fun readFloatFromBytes(bytes: ByteArray, offset: Int): Float {
        val i = 0
        var value = bytes[offset].toInt() and 0xFF
        value = value or (bytes[1 + offset].toInt() shl (8) and 0xFFFF)
        value = value or (bytes[2 + offset].toInt() shl (16) and 0xFFFFFF)
        value = value or (bytes[3 + offset].toInt() shl (24) and -0x1)
        return NumberUtils.intBitsToFloat(value)
    }

    fun writeFloatToBytes(f: Float, bytes: ByteArray, offset: Int): Int {
        val value = NumberUtils.floatToIntBits(f)
        bytes[offset] = (value and 0xFF).toByte()
        bytes[1 + offset] = (value shr 8 and 0xFF).toByte()
        bytes[2 + offset] = (value shr 16 and 0xFF).toByte()
        bytes[3 + offset] = (value shr 24 and 0xFF).toByte()
        // return next idx
        return offset + 4
    }

    private val mtx = Matrix4()
    private val q = Quaternion()

    fun rotateAround(position: Vector3, axis: Vector3?, angle: Float) {
        q.setFromAxis(axis, angle)
        mtx.set(q)
        position.prj(mtx)
    }

    fun rotateAround(position: Vector3, axis: Vector3?, angle: Float, origin: Vector3?) {
        q.setFromAxis(axis, angle)
        mtx.set(q)
        position.sub(origin)
        position.prj(mtx)
        position.add(origin)
    }

    fun rotateAround(position: Vector3?, axis: Vector3?, angle: Float, origin: Vector3?, out: Vector3) {
        q.setFromAxis(axis, angle)
        mtx.set(q)
        out.set(position)
        out.sub(origin)
        out.prj(mtx)
        out.add(origin)
    }

    private val crsTmp = Vector3()
    private val tmp = Vector3()
    private val tmp2 = Vector3()
    private val tmp3 = Vector3()

    fun getDownFromVector(vec: Vector3?): Vector3 {
        crsTmp.set(vec).nor()
        crsTmp.crs(Vector3.Y) // cross by world up to get a right handle angle vector (facing forward)
        tmp.set(vec).nor()
        return tmp.crs(crsTmp)
            .nor() // get the relative "down"
    }

    fun getUpFromVector(vec: Vector3?): Vector3 {
        crsTmp.set(vec).nor()
        crsTmp.crs(Vector3.Y) // cross by world up to get a right handle angle vector (facing forward)
        tmp.set(vec).nor()
        return crsTmp.crs(tmp)
            .nor() // get the relative "down"
    }

    fun getRightFromVector(vec: Vector3?): Vector3 {
        crsTmp.set(vec).nor()
        crsTmp.crs(Vector3.Y) // cross by world up to get a right handle angle vector
        //Tools.print(crsTmp, "right");
        //Tools.print(vec, "original");
        return crsTmp.nor()
    }

    fun getLeftFromVector(vec: Vector3?): Vector3 {
        crsTmp.set(vec).nor()
        tmp.set(Vector3.Y).crs(crsTmp)
        return tmp.nor()
    }

    fun getAngleFromAtoB(a: Vector2, b: Vector2): Float {
        val rawAngle = Math.toDegrees(atan2((b.y - a.y).toDouble(), (b.x - a.x).toDouble())).toFloat()
        return (rawAngle - 90) % 360
    }

    fun getAngleFromAtoB(ax: Float, ay: Float, bx: Float, by: Float): Float {
        return Math.toDegrees(atan2((by - ay).toDouble(), (bx - ax).toDouble())).toFloat()
    }

    private val va = Vector2()
    private val vb = Vector2()

    fun getAngleFromAtoB(a: Vector3, b: Vector3, axis: Vector3): Float {
        if (axis == Vector3.Y) {
            va.set(a.x, a.z)
            vb.set(b.x, b.z)
            return getAngleFromAtoB(
                va,
                vb
            )
        } else {
            throw IllegalArgumentException("bad argument for vector angle")
        }
    }

    fun faceDirectionZ(q: Quaternion, direction: Vector3?) {
        val axisZ: Vector3 = tmp.set(direction).nor()
        val axisY: Vector3 =
            tmp2.set(tmp).crs(Vector3.Y).nor()
                .crs(tmp).nor()
        val axisX: Vector3 = tmp3.set(axisY).crs(axisZ).nor()
        q.setFromAxes(
            false, axisX.x, axisY.x, axisZ.x,
            axisX.y, axisY.y, axisZ.y,
            axisX.z, axisY.z, axisZ.z
        )
    }

    fun faceDirectionY(q: Quaternion?, direction: Vector3?) {
        throw GdxRuntimeException("THIS IS NOT WORKING! DONT USE IT!")
        /*Vector3 axisZ = tmp.set(direction).nor();
		Vector3	axisY = tmp2.set(tmp).crs(Vector3.X).nor().crs(tmp).nor();
		Vector3	axisX = tmp3.set(axisY).crs(axisZ).nor();
		q.setFromAxes(false, axisX.x, axisY.x, axisZ.x,
				axisX.y, axisY.y, axisZ.y,
				axisX.z, axisY.z, axisZ.z);*/
    }

    fun constrainAngle180(angle: Float): Float {
        var angle = angle
        while (angle > 180) {
            angle = angle - 360
        }
        while (angle < -180) {
            angle = angle + 360
        }
        return angle
    }

    fun randomUnitVector(): Vector3 {
        val x: Float = random(0f, 1f)
        val y: Float = random(0f, 1f)
        val z: Float = random(0f, 1f)
        return tmp.set(x, y, z).nor()
    }

    fun barryCentric(p1: Vector3, p2: Vector3, p3: Vector3, pos: Vector2): Float {
        val det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z)
        val l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det
        val l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det
        val l3 = 1.0f - l1 - l2
        return l1 * p1.y + l2 * p2.y + l3 * p3.y
    }

    /**
     * Angle between 2 points.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    fun angle(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return Math.toDegrees(atan2((x2 - x1).toDouble(), (y2 - y1).toDouble())).toFloat()
    }

    /**
     * Get an angle between two Vector3s
     *
     * @param from the vector to compare
     * @param to the vector to compare with
     * @return angle in degrees
     */
    fun getAngleBetween(from: Vector3, to: Vector3): Float {
        val absolute = kotlin.math.sqrt((from.len() * to.len()).toDouble()).toFloat()
        if (MathUtils.isZero(absolute)) return 0f // It is close enough to just return 0


        val angleDot = from.dot(to)
        val dot = MathUtils.clamp(angleDot / absolute, -1f, 1f)
        return MathUtils.acos(dot) * MathUtils.radiansToDegrees
    }

    /**
     * Rotate a directional vector up/down by given angle.
     *
     * @param vectorToRotate the vector to rotate
     * @param angleDegrees the angle in degrees to rotate by
     */
    fun rotateUpDown(vectorToRotate: Vector3, angleDegrees: Float) {
        tmp.set(vectorToRotate)

        // Determine the axis to use
        var axis: Vector3 = tmp.crs(Vector3.Y)

        // If collinear, set to right
        if (axis === Vector3.Zero) axis = Vector3.X

        vectorToRotate.rotate(axis, angleDegrees)
    }

    fun isPowerOfTwo(number: Int): Boolean {
        return (number and (number - 1)) == 0
    }

    /**
     * Find the nearest point on a line to a given point.
     * @param lineStart start of the line
     * @param lineEnd end of the line
     * @param point the point
     * @param out populated with the nearest point on the line
     */
    fun findNearestPointOnLine(lineStart: Vector2?, lineEnd: Vector2?, point: Vector2?, out: Vector2) {
        val lineDirection: Vector2 = Pools.vector2Pool.obtain().set(lineEnd).sub(lineStart)

        // Calculate the length of the line.
        val lineLength = lineDirection.len()
        lineDirection.nor()

        // lineStart to point
        val toPoint: Vector2 = Pools.vector2Pool.obtain().set(point).sub(lineStart)
        val projectedLength = lineDirection.dot(toPoint)

        // Calculate the coordinates of the projected point.
        val projectedPoint = Vector2(lineDirection).scl(toPoint.dot(lineDirection))

        Pools.vector2Pool.free(lineDirection)
        Pools.vector2Pool.free(toPoint)

        if (projectedLength < 0) {
            out.set(lineStart)
        } else if (projectedLength > lineLength) {
            out.set(lineEnd)
        } else {
            // If the projected point lies on the line segment, return the projected point.
            out.set(lineStart).add(projectedPoint)
        }
    }

    /**
     * Calculates the midpoint between two Vector3s
     */
    fun midpoint(a: Vector3?, b: Vector3?): Vector3 {
        return Pools.vector3Pool.obtain().set(a).add(b).scl(0.5f)
    }
}