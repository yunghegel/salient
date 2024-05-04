package org.yunghegel.salient.engine.graphics.shapes.utility

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import org.yunghegel.gdx.utils.ext.boundingRadius
import kotlin.math.cos
import kotlin.math.sin


class RotationGizmoShape(var radius: Float =0.5f, val height: Float=0.008f, val divisionsU: Int=50, val divisionsV: Int=50) {

    private val modelBuilder = ModelBuilder()

    private val attributes = VertexAttributes(
        VertexAttribute.Position(),
        VertexAttribute.Normal(),
        VertexAttribute.ColorUnpacked()
    )

    val v0: VertexInfo = VertexInfo()
    val v1: VertexInfo = VertexInfo()

    fun buildModel() : Model {
        modelBuilder.begin()
        val x = torus(Axis.X,radius,height,divisionsU,divisionsV)
        val y = torus(Axis.Y,radius,height,divisionsU,divisionsV)
        val z = torus(Axis.Z,radius,height,divisionsU,divisionsV)
        val nodeX = modelBuilder.node("x",x)
        val nodeY = modelBuilder.node("y",y)
        val nodeZ = modelBuilder.node("z",z)
        return modelBuilder.end()
    }

    fun attach(target : Model,transform:Matrix4) : ModelInstance {
        radius = target.boundingRadius
        val model = buildModel()
        return ModelInstance(model,transform)
    }

    private fun torus(axis: Axis, width: Float, height: Float, divisionsU: Int, divisionsV: Int): Model {
        val mat = Material()
        val colorAttr = when(axis) {
            Axis.X -> Material(ColorAttribute.createDiffuse(1f, 0f, 0f, 1f))
            Axis.Y -> Material(ColorAttribute.createDiffuse(0f, 1f, 0f, 1f))
            Axis.Z -> Material(ColorAttribute.createDiffuse(0f, 0f, 1f, 1f))
        }
        mat.set(colorAttr)
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        val builder: MeshPartBuilder = modelBuilder.part("torus", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position.toLong(), mat)
        when(axis) {
            Axis.X -> {
                builder.setColor(1f, 0f, 0f, 1f)
                builder.setVertexTransform(Matrix4().rotate(0f, 0f, 1f, 90f))
            }
            Axis.Y -> {
                builder.setColor(0f, 1f, 0f, 1f)
                builder.setVertexTransform(Matrix4().rotate(0f, 1f, 0f, 90f))
            }
            Axis.Z -> {
                builder.setColor(0f, 0f, 1f, 1f)
                builder.setVertexTransform(Matrix4().rotate(1f, 0f, 0f, 90f))            }
        }

        val curr1: VertexInfo = v0.set(null, null, null, null)
        curr1.hasNormal = false
        curr1.hasUV = curr1.hasNormal
        curr1.hasPosition = true

        val curr2: VertexInfo = v1.set(null, null, null, null)
        curr2.hasNormal = false
        curr2.hasUV = curr2.hasNormal
        curr2.hasPosition = true
        var i1: Short
        var i2: Short
        var i3: Short = 0
        var i4: Short = 0
        var j: Int
        var k: Int
        var s: Double
        var t: Double
        val twopi = 2 * Math.PI

        var i = 0
        while (i < divisionsV) {
            j = 0
            while (j <= divisionsU) {
                k = 1
                while (k >= 0) {
                    s = (i + k) % divisionsV + 0.5
                    t = (j % divisionsU).toDouble()

                    curr1.position[((width + height * cos(s * twopi / divisionsV))
                            * cos(t * twopi / divisionsU)).toFloat(), ((width + height * cos(s * twopi / divisionsV))
                            * sin(t * twopi / divisionsU)).toFloat()] = (height * sin(s * twopi / divisionsV)).toFloat()
                    k--
                    s = (i + k) % divisionsV + 0.5
                    curr2.position[((width + height * cos(s * twopi / divisionsV))
                            * cos(t * twopi / divisionsU)).toFloat(), ((width + height * cos(s * twopi / divisionsV))
                            * sin(t * twopi / divisionsU)).toFloat()] = (height * sin(s * twopi / divisionsV)).toFloat()
                    // curr2.uv.set((float) s, 0);
                    i1 = builder.vertex(curr1)
                    i2 = builder.vertex(curr2)
                    builder.rect(i4, i2, i1, i3)
                    i4 = i2
                    i3 = i1
                    k--
                }
                j++
            }
            i++
        }

        return modelBuilder.end()
    }

}