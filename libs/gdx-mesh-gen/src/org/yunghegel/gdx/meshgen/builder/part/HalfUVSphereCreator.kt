package org.yunghegel.gdx.meshgen.builder.part

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.ShapeBuilder
import org.yunghegel.gdx.meshgen.math.Mathf

class HalfUVSphereCreator(private val radius: Float, private val rings: Int, private val segments: Int) :
    ModelCreator() {

    var numVertices: Int = 0
    var numFaces: Int = 0




    override fun create(): Model {
        val modelBuilder = ShapeBuilder()
        modelBuilder.begin()
        b = modelBuilder.part("half_uv_sphere", shapeType, VertexAttributes.Usage.Position.toLong(), mat)
        createVertices()
        println("created vertices")
        createFaces()
        println("created faces")
        createCap()
        println("created cap")


        val mdl: Model = modelBuilder.end()!!
        mdl.meshes[0].transform(Matrix4().translate(0f, -radius / 2f, 0f))
        return mdl
    }

    private fun createVertices() {
        val stepTheta: Float = Mathf.PI / rings.toFloat()
        val stepPhi: Float = Mathf.TWO_PI / segments.toFloat()
        for (row in 1 until rings / 2 + 1) {
            val theta = row * stepTheta
            for (col in 0 until segments) {
                val phi = col * stepPhi
                val x: Float = radius * Mathf.cos(phi) * Mathf.sin(theta)
                val y: Float = radius * Mathf.cos(theta)
                val z: Float = radius * Mathf.sin(phi) * Mathf.sin(theta)
                v(x, y, z)
//                mesh.addVertex(x, y, z)
                numVertices++
            }
        }
        v(0f, radius, 0f)
//        mesh.addVertex(0, radius, 0)
        numVertices++
    }

    private fun getIndex(row: Int, col: Int): Int {
        val idx = segments * row + (col % segments)
        return idx % numVertices
    }

    private fun createFaces() {
        for (row in 0 until (rings - 2) / 2) {
            for (col in 0 until segments) {
                val a = getIndex(row, col + 1)
                val b = getIndex(row + 1, col + 1)
                val c = getIndex(row + 1, col)
                val d = getIndex(row, col)
                this.b.rect(a.toShort(), b.toShort(), c.toShort(), d.toShort())
                numFaces++
                numFaces++
                f(a.toShort(), b.toShort(), c.toShort(), d.toShort())
                if (row == 0) {
                    this.b.triangle(d.toShort(), (numVertices - 1).toShort(), a.toShort())
                    numFaces++
                    f(d, numVertices - 1, a)
                }
                if (row == rings - 3) {
                    this.b.triangle(c.toShort(), b.toShort(), (numVertices - 2).toShort())
                    numFaces++
                    f(c, b, numVertices - 2)
                }
            }
        }
    }

    private fun createCap() {
        capNGon()

//        splitCapIntoTriangleFan()
    }
    

//    private fun splitCapIntoTriangleFan() {
//        val faceToSplit: Face3D = mesh.getFaceAt(mesh.faces.size() - 1)
//        PlanarVertexCenterModifier().modify(mesh, faceToSplit)
//    }

    private fun capNGon() {
        var center = numVertices - 1
        for (i in 0 until segments) {
            val a = getIndex(0, i)
            val b = getIndex(0, i + 1)
            f(a.toShort(), b.toShort(), center.toShort())
            numFaces++
//            mesh.addFace(a, b, center)
        }

        v(0f, 0f, 0f)
//        mesh.addVertex(0, -radius, 0)
        numVertices++
        center = numVertices - 1
        for (i in 0 until segments) {
            val a = getIndex(rings / 2 - 1, i)
            val b = getIndex(rings / 2 - 1, i + 1)
            f(a.toShort(), center.toShort(), b.toShort())
            numFaces++
//            mesh.addFace(a, center, b)
        }
    }

}
