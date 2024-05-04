package org.yunghegel.gdx.meshgen.builder.part

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.ShapeBuilder
import org.yunghegel.gdx.meshgen.math.Mathf

class ArcCreator(
    private val startAngle: Float = 0f,
    private val endAngle: Float = Mathf.TWO_PI/3f,
    private val radius: Float = 1f,
    private val vertices: Int = 32
) : ModelCreator() {
    override fun create(): Model {
        val modelBuilder = ShapeBuilder()
        modelBuilder.begin()
        var i1: Short
        var i2: Short
        var i3: Short
        var i4: Short
        val material = mat.copy()
        material.set(IntAttribute.createCullFace(GL20.GL_NONE))

        b = modelBuilder.part("arc", shapeType, defaultAttributes, material)

        val angleBetweenPoints = calculateAngleBetweenPoints()
        for (i in 0 until vertices) {
            val currentAngle = angleBetweenPoints * i
            val x: Float = radius * Mathf.cos(currentAngle)
            val z: Float = radius * Mathf.sin(currentAngle)

            val x2: Float = radius * Mathf.cos(currentAngle + angleBetweenPoints)
            val z2: Float = radius * Mathf.sin(currentAngle + angleBetweenPoints)

            i1 = v(0f, 0f, 0f)
            i2 = v(x, 0f, z)
            i3 = v(x2, 0f, z2)

            b.triangle(i1, i2, i3)
        }


        return modelBuilder.end()!!
    }

    private fun calculateAngleBetweenPoints(): Float {
        return startAngle + ((endAngle - startAngle) / (vertices.toFloat() - 1))
    }
}
