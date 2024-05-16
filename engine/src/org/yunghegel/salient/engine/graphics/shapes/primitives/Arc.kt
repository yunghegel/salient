package org.yunghegel.salient.engine.graphics.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import org.yunghegel.salient.engine.graphics.shapes.BuilderUtils
import org.yunghegel.salient.engine.graphics.shapes.InstanceSupplier
import kotlin.math.cos
import kotlin.math.sin

class Arc @JvmOverloads constructor(
    private val startAngle: Float,
    private val endAngle: Float,
    private val radius: Float,
    private val vertices: Int,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part("arc", primitiveType, attributes, mat)
        mat!!.set(IntAttribute(IntAttribute.CullFace, 0))

        val angleBetweenPoints = calculateAngleBetweenPoints()
        for (i in 0 until vertices) {
            val currentAngle = angleBetweenPoints * i
            val x = (radius * cos(currentAngle.toDouble())).toFloat()
            val z = (radius * sin(currentAngle.toDouble())).toFloat()

            val x2 = (radius * cos((currentAngle + angleBetweenPoints).toDouble())).toFloat()
            val z2 = (radius * sin((currentAngle + angleBetweenPoints).toDouble())).toFloat()

            i1 = v(0f, 0f, 0f)
            i2 = v(x, 0f, z)
            i3 = v(x2, 0f, z2)

            b.triangle(i1, i3, i2)
        }


        return modelBuilder.end()
    }

    private fun calculateAngleBetweenPoints(): Float {
        return startAngle + ((endAngle - startAngle) / (vertices.toFloat() - 1))
    }
}
