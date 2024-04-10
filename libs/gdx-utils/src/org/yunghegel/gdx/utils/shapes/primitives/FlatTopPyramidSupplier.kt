package org.yunghegel.gdx.utils.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model

class FlatTopPyramidSupplier @JvmOverloads constructor(
    var size: Float,
    var topScale: Float,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()

        b = modelBuilder.part("disc", primitiveType, VertexAttributes.Usage.Position.toLong(), mat)
        //top face is scaled down
        v(size, -size, -size)
        v(size, -size, size)
        v(-size, -size, size)
        v(-size, -size, -size)
        v(size * topScale, size, -size * topScale)
        v(size * topScale, size, size * topScale)
        v(-size * topScale, size, size * topScale)
        v(-size * topScale, size, -size * topScale)

        b.triangle(0.toShort(), 2.toShort(), 1.toShort())
        b.triangle(0.toShort(), 3.toShort(), 2.toShort())
        b.triangle(4.toShort(), 6.toShort(), 5.toShort())
        b.triangle(4.toShort(), 7.toShort(), 6.toShort())
        b.triangle(0.toShort(), 5.toShort(), 1.toShort())
        b.triangle(0.toShort(), 4.toShort(), 5.toShort())
        b.triangle(1.toShort(), 6.toShort(), 2.toShort())
        b.triangle(1.toShort(), 6.toShort(), 5.toShort())
        b.triangle(2.toShort(), 3.toShort(), 7.toShort())
        b.triangle(2.toShort(), 7.toShort(), 6.toShort())
        b.triangle(3.toShort(), 0.toShort(), 4.toShort())
        b.triangle(3.toShort(), 4.toShort(), 7.toShort())


        return modelBuilder.end()
    }
}
