package org.yunghegel.salient.engine.graphics.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.salient.engine.graphics.shapes.BuilderUtils
import org.yunghegel.salient.engine.graphics.shapes.InstanceSupplier
import kotlin.math.cos
import kotlin.math.sin

class UVSphere : InstanceSupplier {
    private val rings: Int
    private val segments: Int
    private val radius: Float

    constructor(radius: Float, rings: Int, segments: Int, color: Color?) : super(color) {
        this.radius = radius
        this.rings = rings
        this.segments = segments
    }

    constructor(radius: Float, rings: Int, segments: Int) : super(BuilderUtils.getRandomColor()) {
        this.radius = radius
        this.rings = rings
        this.segments = segments
    }


    override fun createModel(): Model {
        modelBuilder.begin()
        b = modelBuilder.part("uv_sphere", primitiveType, attributes, mat)

        val stepTheta: Float = PI / rings.toFloat()
        val stepPhi: Float = TWO_PI / segments.toFloat()

        for (row in 1 until rings) {
            val theta = row * stepTheta
            for (col in 0 until segments) {
                val phi = col * stepPhi
                val x = (radius * cos(phi.toDouble()) * sin(theta.toDouble())).toFloat()
                val y = (radius * cos(theta.toDouble())).toFloat()
                val z = (radius * sin(phi.toDouble()) * sin(theta.toDouble())).toFloat()
                v(x, y, z)
            }
        }
        //top
        v(0f, radius, 0f)
        //bottom
        v(0f, -radius, 0f)
        for (row in 0 until rings - 2) {
            for (col in 0 until segments) {
                val index0 = getIndex(row, (col + 1) % segments).toShort()
                val index1 = getIndex(row + 1, (col + 1) % segments).toShort()
                val index2 = getIndex(row + 1, col).toShort()
                val index3 = getIndex(row, col).toShort()
                //                b.rect(index0, index1, index2, index3);
                b.triangle(index0, index1, index2)
                b.triangle(index0, index2, index3)
                if (row == 0) {
                    b.triangle(index3, (b.lastIndex() - 1).toShort(), index0)
                }
                if (row == rings - 3) {
                    b.triangle(index2, index1, (b.lastIndex() - 2).toShort())
                }
            }
        }
        return modelBuilder.end()
    }

    private fun getIndex(row: Int, col: Int): Int {
        val idx = segments * row + col
        return idx % b.lastIndex()
    }
}
