package org.yunghegel.gdx.meshgen.builder.part

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.ShapeBuilder

class BoxCreator(var width: Float =1f, var height: Float=1f, var depth: Float=1f) : ModelCreator() {
    override fun create(): Model {
        val modelBuilder = ShapeBuilder()
        modelBuilder.begin()

        b = modelBuilder.part("box", shapeType, defaultAttributes, mat)
        BoxShapeBuilder.build(b, width, height, depth)
        return modelBuilder.end()!!
    }
}