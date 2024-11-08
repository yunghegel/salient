package org.yunghegel.gdx.meshgen.builder

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.math.Vector3
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
import org.yunghegel.gdx.meshgen.data.VertexInfo
import org.yunghegel.gdx.meshgen.data.ifs.IFSMesh
import org.yunghegel.gdx.meshgen.math.Vector3f
import org.yunghegel.gdx.meshgen.math.clear
import java.util.*

typealias GdxFloatArray = com.badlogic.gdx.utils.FloatArray
typealias GdxShortArray = com.badlogic.gdx.utils.ShortArray

abstract class ModelCreator {
    var shapeType: Int = GL20.GL_TRIANGLES

    var v0: VertexInfo = VertexInfo()
    var v1: VertexInfo = VertexInfo()
    var v2 : VertexInfo = VertexInfo()
    var v3 : VertexInfo = VertexInfo()

    private val tmpIndices = GdxShortArray()
    private val tmpVertices = GdxFloatArray()

    val stack: Stack<() -> Unit> = Stack()

    lateinit var b: ShapeMeshBuilder
    var faceCount: Int = 0
    var vertexCount: Int = 0
    var mat: Material = Material()
    var col: Color = getRandomColor()
    val defaultAttributes = VertexAttributes(VertexAttribute.Position(),VertexAttribute.Normal(),VertexAttribute.ColorUnpacked(),VertexAttribute.TexCoords(0))

    var i1: Short = 0
    var i2: Short = 0
    var i3: Short = 0
    var i4: Short = 0

    val ifs = IFSMesh()


    abstract fun create(): Model

    fun instance() : ModelInstance {
        mat = buildMaterial()

        return ModelInstance(this.create())
    }

    fun mesh(): Mesh? {
        return b.mesh
    }

    fun setShapeType(shapeType: Int): ModelCreator {
        this.shapeType = shapeType
        return this
    }

    fun setMaterial(mat: Material): ModelCreator {
        this.mat = mat
        return this
    }

    fun v(x:Float,y:Float,z:Float) : Short{
        v1.clear()
        val pos = (Vector3(x,y,z))
        val nor = pos.cpy().nor()
        v1.setPos(pos)
        v1.setNor(nor)
        v1.setCol(col)
        val idx =  b.vertex(v1)
        ifs.createVertex(idx.toInt(),v1)
        return idx
    }

    fun f(x:Short,y:Short,z:Short){
        stack.add({b.triangle(x,y,z)})
        ifs.createFace(x.toInt(),y.toInt(),z.toInt())
    }

    fun f(x:Short,y:Short,z:Short,w:Short){
//        two triangles
        stack.add({b.triangle(x,y,z)})
        stack.add({b.triangle(x,z,w)})
        ifs.createFace(x.toInt(),y.toInt(),z.toInt())
        ifs.createFace(x.toInt(),z.toInt(),w.toInt())
    }
    fun processStack(){
        while(!stack.isEmpty()){
            stack.pop().invoke()
        }
    }

    fun f(x:Int,y:Int,z:Int){
        f(x.toShort(),y.toShort(),z.toShort())
    }

    fun f(x:Int,y:Int,z:Int,w:Int){
        f(x.toShort(),y.toShort(),z.toShort(),w.toShort())
    }

    protected fun calculateNormal(v1: Vector3f, v2: Vector3f, v3: Vector3f): Vector3f {
        val v1v2 = v2.sub(v1) as Vector3f
        val v1v3 = v3.sub(v1) as Vector3f
        val normal = v1v2.cross(v1v3)
        normal.normalize()
        return normal
    }

    //normal for quad
    protected fun calculateNormal(v1: Vector3f, v2: Vector3f, v3: Vector3f, v4: Vector3f): Vector3f {
        val v1v2 = v2.sub(v1) as Vector3f
        val v1v3 = v3.sub(v1) as Vector3f
        val v1v4 = v4.sub(v1) as Vector3f
        val normal = v1v2.cross(v1v3)
        normal.normalize()
        return normal
    }


    protected fun computeNormals(
        vertices: FloatArray,
        indices: ShortArray?,
        attributesGroup: VertexAttributes
    ): FloatArray {
        val posOffset = attributesGroup.getOffset(VertexAttributes.Usage.Position)
        val normalOffset = attributesGroup.getOffset(VertexAttributes.Usage.Normal)
        val stride = attributesGroup.vertexSize / 4

        val vab = Vector3()
        val vac = Vector3()
        if (indices != null) {
            var index = 0
            val count = indices.size
            while (index < count) {
                val vIndexA = indices[index++].toInt() and 0xFFFF
                val ax = vertices[vIndexA * stride + posOffset]
                val ay = vertices[vIndexA * stride + posOffset + 1]
                val az = vertices[vIndexA * stride + posOffset + 2]

                val vIndexB = indices[index++].toInt() and 0xFFFF
                val bx = vertices[vIndexB * stride + posOffset]
                val by = vertices[vIndexB * stride + posOffset + 1]
                val bz = vertices[vIndexB * stride + posOffset + 2]

                val vIndexC = indices[index++].toInt() and 0xFFFF
                val cx = vertices[vIndexC * stride + posOffset]
                val cy = vertices[vIndexC * stride + posOffset + 1]
                val cz = vertices[vIndexC * stride + posOffset + 2]

                vab.set(bx, by, bz).sub(ax, ay, az)
                vac.set(cx, cy, cz).sub(ax, ay, az)
                val n = vab.crs(vac).nor()

                vertices[vIndexA * stride + normalOffset] = n.x
                vertices[vIndexA * stride + normalOffset + 1] = n.y
                vertices[vIndexA * stride + normalOffset + 2] = n.z

                vertices[vIndexB * stride + normalOffset] = n.x
                vertices[vIndexB * stride + normalOffset + 1] = n.y
                vertices[vIndexB * stride + normalOffset + 2] = n.z

                vertices[vIndexC * stride + normalOffset] = n.x
                vertices[vIndexC * stride + normalOffset + 1] = n.y
                vertices[vIndexC * stride + normalOffset + 2] = n.z
            }
        } else {
            var index = 0
            val count = vertices.size / stride
            while (index < count) {
                val vIndexA = index++
                val ax = vertices[vIndexA * stride + posOffset]
                val ay = vertices[vIndexA * stride + posOffset + 1]
                val az = vertices[vIndexA * stride + posOffset + 2]

                val vIndexB = index++
                val bx = vertices[vIndexB * stride + posOffset]
                val by = vertices[vIndexB * stride + posOffset + 1]
                val bz = vertices[vIndexB * stride + posOffset + 2]

                val vIndexC = index++
                val cx = vertices[vIndexC * stride + posOffset]
                val cy = vertices[vIndexC * stride + posOffset + 1]
                val cz = vertices[vIndexC * stride + posOffset + 2]

                vab.set(bx, by, bz).sub(ax, ay, az)
                vac.set(cx, cy, cz).sub(ax, ay, az)
                val n = vab.crs(vac).nor()

                vertices[vIndexA * stride + normalOffset] = n.x
                vertices[vIndexA * stride + normalOffset + 1] = n.y
                vertices[vIndexA * stride + normalOffset + 2] = n.z

                vertices[vIndexB * stride + normalOffset] = n.x
                vertices[vIndexB * stride + normalOffset + 1] = n.y
                vertices[vIndexB * stride + normalOffset + 2] = n.z

                vertices[vIndexC * stride + normalOffset] = n.x
                vertices[vIndexC * stride + normalOffset + 1] = n.y
                vertices[vIndexC * stride + normalOffset + 2] = n.z
            }
        }
        return vertices
    }

    companion object {
        fun buildMaterial(color: Color = getRandomColor(),pbr:Boolean = false): Material {
            val mat = Material()
            if(pbr) {
                mat.set(PBRColorAttribute.createDiffuse(color))
                mat.set(PBRColorAttribute.createSpecular(color))
                mat.set(PBRColorAttribute.createAmbient(color))
                mat.set(PBRColorAttribute.createEmissive(color))
                mat.set(PBRColorAttribute.createBaseColorFactor(color))
            } else {
                mat.set(ColorAttribute.createDiffuse(color))
                mat.set(ColorAttribute.createSpecular(color))
                mat.set(ColorAttribute.createAmbient(color))
                mat.set(ColorAttribute.createEmissive(color))
            }
            return mat
        }

        fun buildAttributes(normal: Boolean = true, color: Boolean=true, texCoords: Boolean=false): Long {
            var attributes: Long = 0
            if (normal) attributes = attributes or VertexAttributes.Usage.Normal.toLong()
            if (color) attributes = attributes or VertexAttributes.Usage.ColorUnpacked.toLong()
            if (texCoords) attributes = attributes or VertexAttributes.Usage.TextureCoordinates.toLong()
            return attributes
        }

        fun buildAttributes(
            normal: Boolean,
            color: Boolean,
            texCoords: Boolean,
            tangent: Boolean,
            binormal: Boolean
        ): Long {
            var attributes: Long = 0
            if (normal) attributes = attributes or VertexAttributes.Usage.Normal.toLong()
            if (color) attributes = attributes or VertexAttributes.Usage.ColorUnpacked.toLong()
            if (texCoords) attributes = attributes or VertexAttributes.Usage.TextureCoordinates.toLong()
            if (tangent) attributes = attributes or VertexAttributes.Usage.Tangent.toLong()
            if (binormal) attributes = attributes or VertexAttributes.Usage.BiNormal.toLong()
            return attributes
        }

        fun getRandomColor(): Color {
            val color = Color()
            color.fromHsv(Math.random().toFloat() * 360, 1f, 1f)
            return color
        }
    }
}
