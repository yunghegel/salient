package org.yunghegel.gdx.meshgen.builder.part

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.ShapeBuilder
import org.yunghegel.gdx.meshgen.math.Mathf
import org.yunghegel.gdx.meshgen.math.Mathf.cos
import org.yunghegel.gdx.meshgen.math.Mathf.sin
import org.yunghegel.gdx.meshgen.math.Mathf.toOneDimensionalIndex

class CapsuleCreator(
    private val topRadius: Float = 1f,
    private val bottomRadius: Float=1f,
    private val cylinderHeight: Float=2f,
    private val topCapHeight: Float=1f,
    private val bottomCapHeight: Float=1f,
    private val topCapSegments: Int=16,
    private val bottomCapSegments: Int=16,
    private val cylinderSegments: Int=8,
    private val rotationSegments: Int=32
) : ModelCreator() {
    override fun create(): Model {
        val modelBuilder = ShapeBuilder()
        modelBuilder.begin()
        var i1: Short
        var i2: Short
        var i3: Short
        var i4: Short


        b = modelBuilder.part("capsule", shapeType, VertexAttributes.Usage.Position.toLong(), mat)


        createTopCapVertices(b)
        createCylinderVertices(b)
        createBottomCapVertices(b)
        createQuadFaces(b)
        createTriangleFaces()

        return modelBuilder.end()!!
    }

    private fun createTopCapVertices(b: MeshPartBuilder) {
        createCapVertices(-1, topRadius, topCapHeight, topCapSegments, b)
    }

    private fun createBottomCapVertices(b: MeshPartBuilder) {
        createCapVertices(1, bottomRadius, bottomCapHeight, bottomCapSegments, b)
    }

    private fun createCapVertices(a: Int, radius: Float, height: Float, segments: Int, b: MeshPartBuilder) {
        val yOffset = cylinderHeight / 2.0f * a
        val stepTheta = Mathf.HALF_PI / segments
        val stepPhi = Mathf.TWO_PI / rotationSegments
        val thetaA = segments * stepTheta
        for (i in 1 until segments) {
            val theta = if (a == 1) thetaA - (i * stepTheta) else i * stepTheta
            for (j in 0 until rotationSegments) {
                val phi = j * stepPhi
                val x = radius * cos(phi) * sin(theta)
                val y = height * a * cos(theta) + yOffset
                val z = radius * sin(phi) * sin(theta)
                v(x, y, z)
                val normal = Vector3(x, y, z).nor()
            }
        }
    }

    private fun createCylinderVertices(b: MeshPartBuilder) {
        val radiusStep = (topRadius - bottomRadius) / cylinderSegments
        val segmentHeight = cylinderHeight / cylinderSegments
        val angle = Mathf.TWO_PI / rotationSegments
        for (i in 0..cylinderSegments) {
            for (j in 0 until rotationSegments) {
                val x = (topRadius - (i * radiusStep)) * (cos(j * angle))
                val y = i * segmentHeight - (cylinderHeight / 2f)
                val z = (topRadius - (i * radiusStep)) * (sin(j * angle))
                v(x, y, z)
            }
        }
    }

    private fun createTopCapTriangleFan() {
        triangleFan(0, -cylinderHeight / 2 - topCapHeight, b)
    }

    private fun createBottomCapTriangleFan() {
        val offset = (segmentsCount - 2) * rotationSegments
        triangleFan(offset, cylinderHeight / 2f + bottomCapHeight, b)
    }

    private fun triangleFan(indexOffset: Int, centerY: Float, b: MeshPartBuilder) {
        v(0f, centerY, 0f)
        val centerIndex = b.lastIndex().toShort()
        for (i in 0 until rotationSegments) {
            val idx0 = (i + indexOffset).toShort()
            val idx1 = ((i + 1) % rotationSegments + indexOffset).toShort()
            if (indexOffset == 0) {
                b.triangle(idx0, idx1, centerIndex)
            } else {
                b.triangle(idx1, idx0, centerIndex)
            }
        }
    }

    private fun createQuadFaces(b: MeshPartBuilder) {
        for (i in 0 until segmentsCount - 2) for (j in 0 until rotationSegments) addFace(i.toInt(), j.toInt(), b)
    }

    private fun createTriangleFaces() {
        createTopCapTriangleFan()
        createBottomCapTriangleFan()
    }

    private fun addFace(i: Int, j: Int, b: MeshPartBuilder) {
        val idx0 = toOneDimensionalIndex(i, j)
        val idx1 = toOneDimensionalIndex(i + 1, j)
        val idx2 = toOneDimensionalIndex(i + 1, (j + 1) % rotationSegments)
        val idx3 = toOneDimensionalIndex(i, (j + 1) % rotationSegments)
        //        b.rect(idx0, idx1, idx2, idx3);
        //as two triangles
        b.triangle(idx0, idx1, idx2)
        b.triangle(idx0, idx2, idx3)
    }

    private fun toOneDimensionalIndex(i: Int, j: Int): Short {
        return toOneDimensionalIndex(i, j, rotationSegments).toShort()
    }

    private val segmentsCount: Int
        get() = topCapSegments + cylinderSegments + bottomCapSegments
}
