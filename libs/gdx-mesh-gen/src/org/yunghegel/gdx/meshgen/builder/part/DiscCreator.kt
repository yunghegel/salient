package org.yunghegel.gdx.meshgen.builder.part

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.ShapeBuilder
import org.yunghegel.gdx.meshgen.math.Mathf

class DiscCreator(
    private val outerRadius: Float=1.0f,
    private val innerRadius: Float=0.5f,
    private val rotationSegments: Int=32,
    private val discSegments: Int=1
) : ModelCreator() {
    override fun create(): Model {
        val modelBuilder = ShapeBuilder()
        modelBuilder.begin()

        mat.set(IntAttribute.createCullFace(GL20.GL_NONE))

        b = modelBuilder.part("disc", shapeType, defaultAttributes, mat)
        if (innerRadius > 0) {
            createDisc(discSegments, innerRadius)
        } else {
            createDisc(discSegments - 1, outerRadius / discSegments)
            createTriangleFan()
        }

        processStack()

        val model =  modelBuilder.end()!!

        ifs.vertices().forEach {
            val pos = ifs.position.get(it)
            println("$it")
        }
        ifs.faces().forEach {
            println("$it")
        }

        return model
    }

    private fun addFace(i: Int, j: Int, segments: Int) {
        if (i >= segments) return
        val idx0 = toOneDimensionalIndex(i, j)
        val idx1 = toOneDimensionalIndex(i + 1, j)
        val idx2 = toOneDimensionalIndex(i + 1, j + 1)
        val idx3 = toOneDimensionalIndex(i, j + 1)
        f(idx0.toShort(), idx1.toShort(), idx2.toShort(), idx3.toShort())
    }

    private fun toOneDimensionalIndex(i: Int, j: Int): Int {
        return Mathf.toOneDimensionalIndex(i, j % rotationSegments, rotationSegments)
    }

    private fun createDisc(segments: Int, startRadius: Float) {
        var angle = 0f
        val radius = (outerRadius - innerRadius) / discSegments.toFloat()


        for (i in 0..segments) {
            for (j in 0 until rotationSegments) {
                val x: Float = (startRadius + (i * radius)) * Mathf.cos(angle)
                val y = 0f
                val z: Float = (startRadius + (i * radius)) * Mathf.sin(angle)
                v(x, y, z)
                addFace(i, j, segments)
                angle += Mathf.TWO_PI / rotationSegments.toFloat()
            }
            angle = 0f
        }
    }

    private fun createTriangleFan() {
        val idx: Int = b.lastIndex()
        v(0f, 0f, 0f)
        for (i in 0 until rotationSegments) {
            f(idx.toShort(), i.toShort(), ((i + 1) % rotationSegments).toShort())
        }
    }
}
