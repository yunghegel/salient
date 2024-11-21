package org.yunghegel.salient.engine.graphics.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import org.yunghegel.salient.engine.graphics.shapes.BuilderUtils
import org.yunghegel.salient.engine.graphics.shapes.InstanceSupplier
import kotlin.math.cos
import kotlin.math.sin

class Disc @JvmOverloads constructor(
     var outerRadius: Float,
     var innerRadius: Float,
     var rotationSegments: Int,
     var discSegments: Int,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model {
        modelBuilder.begin()
        addMaterialAttribute(IntAttribute(IntAttribute.CullFace, 0))
        b = modelBuilder.part("disc", primitiveType, VertexAttributes.Usage.Position.toLong(), mat)
        if (innerRadius > 0) {
            createDisc(discSegments, innerRadius)
        } else {
            createDisc(discSegments - 1, outerRadius / discSegments)
            createTriangleFan()
        }
        return modelBuilder.end()
    }

    private fun addFace(i: Int, j: Int, segments: Int) {
        if (i >= segments) return
        val idx0 = toOneDimensionalIndex(i, j)
        val idx1 = toOneDimensionalIndex(i + 1, j)
        val idx2 = toOneDimensionalIndex(i + 1, j + 1)
        val idx3 = toOneDimensionalIndex(i, j + 1)
        b.triangle(idx0.toShort(), idx2.toShort(), idx1.toShort())
        b.triangle(idx0.toShort(), idx3.toShort(), idx2.toShort())
    }

    private fun toOneDimensionalIndex(i: Int, j: Int): Int {
        return toOneDimensionalIndex(i, j % rotationSegments, rotationSegments)
    }

    private fun createDisc(segments: Int, startRadius: Float) {
        var angle = 0f
        val radius = (outerRadius - innerRadius) / discSegments.toFloat()


        for (i in 0..segments) {
            for (j in 0 until rotationSegments) {
                val x = ((startRadius + (i * radius)) * cos(angle.toDouble())).toFloat()
                val y = 0f
                val z = ((startRadius + (i * radius)) * sin(angle.toDouble())).toFloat()
                v(x, y, z)
                addFace(i, j, segments)
                angle += TWO_PI / rotationSegments.toFloat()
            }
            angle = 0f
        }
    }

    private fun createTriangleFan() {
        val idx = b.lastIndex()
        v(0f, 0f, 0f)
        for (i in 0 until rotationSegments) {
            b.triangle(idx.toShort(), i.toShort(), ((i + 1) % rotationSegments).toShort())
        }
    }
}
