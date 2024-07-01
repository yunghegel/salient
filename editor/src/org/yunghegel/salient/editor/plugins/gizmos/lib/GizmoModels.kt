package org.yunghegel.salient.editor.plugins.gizmos.lib

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import com.badlogic.gdx.math.Vector3
import kotlin.math.cos
import kotlin.math.sin


internal object GizmoModels {

    val v0: VertexInfo = VertexInfo()

    val v1: VertexInfo = VertexInfo()

    fun torus(mat: Material, width: Float, height: Float, divisionsU: Int, divisionsV: Int): Model {
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        val builder: MeshPartBuilder =
            modelBuilder.part("torus", GL20.GL_TRIANGLES, VertexAttributes( VertexAttribute.Position() , VertexAttribute.ColorUnpacked()), mat)

        // builder.setColor(Ansi.LIGHT_GRAY);
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

    fun createArrowStub(mat: Material?, from: Vector3, to: Vector3): Model {
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        // line
        var meshBuilder = modelBuilder.part(
            "line",
            GL20.GL_LINES,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.TextureCoordinates or VertexAttributes.Usage.ColorUnpacked).toLong(),
            mat
        )
        meshBuilder.line(from.x, from.y, from.z, to.x, to.y, to.z)
        //rectangular prism
        val node1: Node = modelBuilder.node()
        //        node1.translation.set(to.cpy().sub(from).scl(0.5f).add(from));
        node1.translation.set(from.cpy().add(to).scl(0.5f))
        meshBuilder = modelBuilder.part(
            "rectangularPrism",
            GL20.GL_TRIANGLES,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.TextureCoordinates or VertexAttributes.Usage.ColorUnpacked).toLong(),
            mat
        )
        BoxShapeBuilder.build(meshBuilder, 0.08f / 7 + to.x, 0.08f / 7 + to.y, 0.08f / 7 + to.z)


        // stub
        val node2: Node = modelBuilder.node()
        node2.translation.set(to.x, to.y, to.z)
        meshBuilder = modelBuilder.part(
            "stub",
            GL20.GL_TRIANGLES,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong(),
            mat
        )
        BoxShapeBuilder.build(meshBuilder, 0.025f, 0.0251f, 0.025f)
        return modelBuilder.end()
    }

    fun createArrow(builder:ModelBuilder,axis: Vector3,length: Float,capSize: Float,thickness:Float,divisons:Int, mat:Material) : Model {
        val dir = axis.cpy().scl(length)
        return builder.createArrow(0f,0f,0f,dir.x,dir.y,dir.z,capSize,thickness,divisons,GL20.GL_TRIANGLES,mat, (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong())
    }

    fun createSphere(builder:ModelBuilder, radius:Float, divisionsU:Int, divisionsV:Int, divisionsW:Int, mat:Material): Model {
        return builder.createSphere(radius, radius, radius, divisionsU, divisionsV, divisionsW, mat, (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
    }

}