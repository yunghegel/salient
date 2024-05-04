package org.yunghegel.gdx.meshgen.builder.part

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder
import com.badlogic.gdx.math.Matrix4
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.ShapeBuilder

class DoubleConeCreator(
    private val radius: Float = 1f,
    private val height: Float =2f,
    private val vertices: Int = 32,
    private val divisions: Int=8,
) : ModelCreator() {
    override fun create(): Model {
        val modelBuilder = ShapeBuilder()
        modelBuilder.begin()

        b = modelBuilder.part("top_cone", shapeType, defaultAttributes, mat)
        ConeShapeBuilder.build(b, radius, height / 2, radius, divisions, 0f, 360f, false)
        val top = modelBuilder.end()!!

        modelBuilder.begin()
        b = modelBuilder.part("bottom_cone", shapeType, defaultAttributes, mat)
        ConeShapeBuilder.build(b, radius, height / 2, radius, divisions, 0f, -360f, true)


        //flip so the cone is facing down
        val bottom = modelBuilder.end()!!

        modelBuilder.begin()
        b = modelBuilder.part("double_cone", shapeType, defaultAttributes, mat)
        val topMesh = top.meshes[0]
        topMesh.transform(Matrix4().setToTranslation(0f, height / 2, 0f))


        b.addMesh(top.meshes[0])
        val bottomMesh = bottom.meshes[0]
        //rotate the bottom cone 180 degrees so it's facing up
        bottomMesh.transform(Matrix4().setToRotation(1f, 0f, 0f, -180f))
        b.addMesh(bottomMesh)

        val doubleCone = modelBuilder.end()!!
        doubleCone.materials.first().set(mat)

        return doubleCone
    }


    private fun createFaces() {
        for (i in 0 until vertices) {
            createTopFaceAt(i)
            createBottomFaceAt(i)
        }
    }

    private fun createVertices() {
        createVerticesAroundOrigin()
        createTopCenterVertex()
        createBottomCenterVertex()
    }


    private fun createVerticesAroundOrigin() {
        val cir = CircleCreator(vertices, radius, 0, mat).create()
    }

    private fun createBottomCenterVertex() {
        addVertex(0f, height / 2f, 0f)
        for (i in 0 until vertices) addFace(vertices + 1, i, (i + 1) % vertices)
    }

    private fun createTopCenterVertex() {
        addVertex(0f, -height / 2f, 0f)
    }

    private fun createTopFaceAt(i: Int) {
        addFace(vertices, i, (i + 1) % vertices)
    }

    private fun createBottomFaceAt(i: Int) {
        addFace(vertices + 1, (i + 1) % vertices, i)
    }

    private fun addVertex(x: Float, y: Float, z: Float) {
        b.vertex(x, y, z)
    }

    private fun addFace(vararg indices: Int) {
        if (indices.size == 3) b.triangle(indices[0].toShort(), indices[1].toShort(), indices[2].toShort())
        else if (indices.size == 4) b.rect(
            indices[0].toShort(),
            indices[1].toShort(),
            indices[2].toShort(),
            indices[3].toShort()
        )
    }
}
