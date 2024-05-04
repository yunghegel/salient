package org.yunghegel.salient.engine.graphics.shapes.utility

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import ktx.math.component1
import ktx.math.component2
import ktx.math.component3


class AxisArrows(val length:Float=1f,val capSize:Float=0.1f,val thickness:Float=0.25f,val divisions:Int=12) {

    private val modelBuilder = ModelBuilder()

    private val attributes = VertexAttributes(
        VertexAttribute.Position(),
        VertexAttribute.Normal(),
        VertexAttribute.ColorUnpacked()
    )

    fun buildModel() : Model {
        modelBuilder.begin()
        val x = buildAxisArrow(Axis.X,length,capSize,thickness,divisions)
        val y = buildAxisArrow(Axis.Y,length,capSize,thickness,divisions)
        val z = buildAxisArrow(Axis.Z,length,capSize,thickness,divisions)
        val nodeX = modelBuilder.node("x",x)
        val nodeY = modelBuilder.node("y",y)
        val nodeZ = modelBuilder.node("z",z)
        return modelBuilder.end()
    }

    fun attach(sourceTransform : Matrix4) : ModelInstance {
        val model = buildModel()
        return ModelInstance(model,sourceTransform)
    }

    init {
        modelBuilder.begin()
        val x = buildAxisArrow(Axis.X,length,capSize,thickness,divisions)
        val y = buildAxisArrow(Axis.Y,length,capSize,thickness,divisions)
        val z = buildAxisArrow(Axis.Z,length,capSize,thickness,divisions)
        val nodeX = modelBuilder.node("x",x)
        val nodeY = modelBuilder.node("y",y)
        val nodeZ = modelBuilder.node("z",z)
        val model = modelBuilder.end()
    }


    private fun buildAxisArrow(axis: Axis, length: Float, capSize:Float, thickness:Float, divisions:Int): Model {
        val builder = ModelBuilder()
        val from = Vector3.Zero
        val to = when(axis) {
            Axis.X -> Vector3(length, 0f, 0f)
            Axis.Y -> Vector3(0f, length, 0f)
            Axis.Z -> Vector3(0f, 0f, length)
        }
        val material = Material()
        material.set(DepthTestAttribute(GL20.GL_ALWAYS,true))
        material.set(IntAttribute.createCullFace(GL20.GL_NONE))
        when (axis) {
            Axis.X -> material.set(ColorAttribute.createDiffuse(Color.RED))
            Axis.Y -> material.set(ColorAttribute.createDiffuse(Color.GREEN))
            Axis.Z -> material.set(ColorAttribute.createDiffuse(Color.BLUE))
        }
        val (x,y,z) = from
        val (x1,y1,z1) = to

        val model = builder.createArrow(
            x,y,z,x1,y1,z1,
            capSize, thickness, divisions, GL20.GL_TRIANGLES,
            material,
            attributes.mask
        )
        return model
    }

}