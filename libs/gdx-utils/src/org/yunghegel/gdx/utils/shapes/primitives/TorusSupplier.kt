package org.yunghegel.gdx.utils.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import kotlin.math.cos
import kotlin.math.sin

class TorusSupplier //   defaults .5f,0.1f,47,12
@JvmOverloads constructor(
    private val majorRadius: Float,
    private val minorRadius: Float,
    private val majorSegments: Int,
    private val minorSegments: Int,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part("torus", primitiveType, attributes, mat)

        val stepU: Float = InstanceSupplier.Companion.TWO_PI / minorSegments
        val stepV: Float = InstanceSupplier.Companion.TWO_PI / majorSegments

        for (i in 0 until majorSegments) {
            val v = i * stepV
            for (j in 0 until minorSegments) {
                val u = j * stepU
                val x = ((majorRadius + minorRadius * cos(u.toDouble())) * cos(v.toDouble())).toFloat()
                val y = (minorRadius * sin(u.toDouble())).toFloat()
                val z = ((majorRadius + minorRadius * cos(u.toDouble())) * sin(v.toDouble())).toFloat()
                v(x, y, z)
            }
        }

        for (i in 0 until minorSegments) {
            for (j in 0 until majorSegments) {
                var i1 = toOneDimensionalIndex(i, j)
                var i2 = toOneDimensionalIndex(i + 1, j)
                var i3 = toOneDimensionalIndex(i + 1, j + 1)
                var i4 = toOneDimensionalIndex(i, j + 1)
                b.triangle(i1, i2, i3)
                b.triangle(i1, i3, i4)
            }
        }

        return modelBuilder.end()
    }

    private fun toOneDimensionalIndex(i: Int, j: Int): Short {
        return ((j % majorSegments).toShort() * minorSegments + (i % minorSegments)).toShort()
    }
}
