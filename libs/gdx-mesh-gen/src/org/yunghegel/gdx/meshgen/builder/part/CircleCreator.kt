package org.yunghegel.gdx.meshgen.builder.part

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.ShapeBuilder
import org.yunghegel.gdx.meshgen.math.Mathf
import org.yunghegel.gdx.meshgen.math.Mathf.cos
import org.yunghegel.gdx.meshgen.math.Mathf.sin

class CircleCreator(private val vertices: Int = 32, private val radius: Float =1f, private val centerY: Int=0, mat: Material = Material()) :
    ModelCreator() {
    init {
        this.mat = mat!!
    }

    override fun create(): Model {
        val modelBuilder = ShapeBuilder()
        modelBuilder.begin()
        setShapeType(GL20.GL_LINES)
        mat.set(IntAttribute.createCullFace(GL20.GL_NONE))

        b = modelBuilder.part("circle", shapeType, defaultAttributes, mat)


        var angle = 0f
        val step = Mathf.TWO_PI / vertices.toFloat()
        for (i in 0 until vertices) {
            val x = cos(angle) * radius
            val z = sin(angle) * radius
            v(x, centerY.toFloat(), z)

            angle += step
        }


        v(0f, centerY.toFloat(), 0f)
        for (i in 0 until vertices) {
            val i1 = (i % vertices).toShort()
            val i2 = ((i + 1).toShort() % vertices).toShort()
            b.triangle(i1, i2, vertices.toShort())
        }


        return modelBuilder.end()!!
    }
}
