package org.yunghegel.gdx.meshgen.builder.part

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.ShapeBuilder

class FlatTopPyramidCreator(private val size: Float=1f, private val topScale: Float=0.5f) : ModelCreator() {
    override fun create(): Model {
        val modelBuilder = ShapeBuilder()
        modelBuilder.begin()

        b = modelBuilder.part("disc", shapeType, defaultAttributes, mat)
        //top face is scaled down
        v(size, -size, -size)
        v(size, -size, size)
        v(-size, -size, size)
        v(-size, -size, -size)
        v(size * topScale, size, -size * topScale)
        v(size * topScale, size, size * topScale)
        v(-size * topScale, size, size * topScale)
        v(-size * topScale, size, -size * topScale)

        f(0.toShort(), 1.toShort(), 2.toShort())
        f(0.toShort(), 2.toShort(), 3.toShort())
        f(4.toShort(), 5.toShort(), 6.toShort())
        f(4.toShort(), 6.toShort(), 7.toShort())
        f(0.toShort(), 1.toShort(), 5.toShort())
        f(0.toShort(), 5.toShort(), 4.toShort())
        f(1.toShort(), 2.toShort(), 6.toShort())
        f(1.toShort(), 6.toShort(), 5.toShort())
        f(2.toShort(), 3.toShort(), 7.toShort())
        f(2.toShort(), 7.toShort(), 6.toShort())
        f(3.toShort(), 0.toShort(), 4.toShort())
        f(3.toShort(), 4.toShort(), 7.toShort())



        return modelBuilder.end()!!
    }
}
