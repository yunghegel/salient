package org.yunghegel.salient.engine.graphics.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.salient.engine.graphics.shapes.BuilderUtils
import org.yunghegel.salient.engine.graphics.shapes.InstanceSupplier
import kotlin.math.cos
import kotlin.math.sin

class Circle(color: Color?, var vertices: Int, var radius: Float, var centerY: Float) :
    InstanceSupplier(color) {
    constructor(vertices: Int, radius: Float, centerY: Float) : this(
        BuilderUtils.getRandomColor(),
        vertices,
        radius,
        centerY
    )

    override fun createModel(): Model {
        modelBuilder.begin()
        b = modelBuilder.part("circle", primitiveType, attributes, mat)
        var angle = 0f
        val step: Float = TWO_PI / vertices.toFloat()
        for (i in 0 until vertices) {
            val x = (cos(angle.toDouble()) * radius).toFloat()
            val z = (sin(angle.toDouble()) * radius).toFloat()
            v(x, centerY, z)
            angle += step
        }


        v(0f, centerY, 0f)
        for (i in 0 until vertices) {
            val i1 = (i % vertices).toShort()
            val i2 = ((i + 1).toShort() % vertices).toShort()
            b.triangle(i2, i1, vertices.toShort())
        }
        return modelBuilder.end()
    }
}
