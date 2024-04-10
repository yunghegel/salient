package org.yunghegel.gdx.utils.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder
import com.badlogic.gdx.math.Matrix4

class DoubleConeSupplier : InstanceSupplier {
    private val vertices: Int
    private val radius: Float
    private val height: Float
    private val divisions: Int

    constructor(radius: Float, height: Float, vertices: Int, divisions: Int, color: Color?) : super(color) {
        this.radius = radius
        this.height = height
        this.vertices = vertices
        this.divisions = divisions
    }

    constructor(radius: Float, height: Float, vertices: Int, divisions: Int) : super(BuilderUtils.getRandomColor()) {
        this.radius = radius
        this.height = height
        this.vertices = vertices
        this.divisions = divisions
    }


    override fun createModel(): Model? {
        modelBuilder.begin()

        b = modelBuilder.part("top_cone", primitiveType, attributes, mat)
        ConeShapeBuilder.build(b, radius, height / 2, radius, divisions, 0f, 360f, true)
        val top = modelBuilder.end()

        modelBuilder.begin()
        b = modelBuilder.part("bottom_cone", primitiveType, attributes, mat)
        ConeShapeBuilder.build(b, radius, height / 2, radius, divisions, 0f, 360f, true)


        //flip so the cone is facing down
        val bottom = modelBuilder.end()

        modelBuilder.begin()
        b = modelBuilder.part("double_cone", primitiveType, attributes, mat)
        val topMesh = top.meshes[0]
        topMesh.transform(Matrix4().setToTranslation(0f, height / 2, 0f))


        b.addMesh(top.meshes[0])
        val bottomMesh = bottom.meshes[0]
        //rotate the bottom cone 180 degrees so it's facing up
        bottomMesh.transform(Matrix4().setToRotation(1f, 0f, 0f, 180f))
        b.addMesh(bottomMesh)

        val doubleCone = modelBuilder.end()
        doubleCone.materials.first().set(mat)

        return doubleCone
    }
}
