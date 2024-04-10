package org.yunghegel.gdx.utils.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.gdx.utils.shapes.BuilderUtils
import org.yunghegel.gdx.utils.shapes.InstanceSupplier

class PyramidSupplier @JvmOverloads constructor(
    var size: Float,
    var height: Float,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part(
            "square_pyramid",
            primitiveType,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorUnpacked).toLong(),
            mat
        )
        v(-size, 0f, -size)
        v(-size, 0f, size)
        v(size, 0f, size)
        v(size, 0f, -size)
        v(0f, height, 0f)


        b.triangle(0.toShort(), 1.toShort(), 2.toShort())
        b.triangle(0.toShort(), 2.toShort(), 3.toShort())
        b.triangle(0.toShort(), 4.toShort(), 1.toShort())
        b.triangle(1.toShort(), 4.toShort(), 2.toShort())
        b.triangle(2.toShort(), 4.toShort(), 3.toShort())
        b.triangle(3.toShort(), 4.toShort(), 0.toShort())




        return modelBuilder.end()
    }
}
