package org.yunghegel.gdx.meshgen.builder.part

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.ShapeBuilder

class CubeCreator(private val radius: Float=1f) : ModelCreator() {



    override fun create(): Model {
        val modelBuilder = ShapeBuilder()
        modelBuilder.begin()
        b = modelBuilder.part("box", shapeType, defaultAttributes, mat)
        BoxShapeBuilder.build(b, radius, radius, radius)
        return modelBuilder.end()!!
    }
}