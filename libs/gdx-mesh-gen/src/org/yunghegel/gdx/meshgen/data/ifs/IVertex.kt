package org.yunghegel.gdx.meshgen.data.ifs

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.data.base.*
import java.util.*

class IVertex(index: Int,mesh:IFSMesh) : Vertex(index,mesh) {

    val elementData: ElementData<IVertex>
        get() = mesh.vertices()

    override var position  by attribute(mesh.position)
    override var normal by attribute(mesh.normal)
    override var color by attribute(mesh.color)
    override var uv by attribute(mesh.uv)
    override var binormal by attribute(mesh.binormal)
    override var tangent by attribute(mesh.tangent)



    init {

    }


    override fun toString(): String {
        return "IVertex(index=$index, position=$position, normal=$normal, color=$color, uv=$uv)"
    }

    inner class VertexData(val backingArray: FloatArray,val attributes:VertexAttributes) {
        val size = attributes.vertexSize / 4

        val floatData: FloatArray by lazy {
            getVertexData(backingArray,index,size)
        }

        var position: Vector3
            get() {
                val data = getAttributeData(VertexAttributes.Usage.Position,floatData)
                return Vector3(data[0],data[1],data[2])
            }
            set(value) {
                setAttributeData(VertexAttributes.Usage.Position,index,size, floatArrayOf(value.x,value.y,value.z),backingArray)
            }

        var normal: Vector3?
            get() {
                val data = getAttributeData(VertexAttributes.Usage.Normal,floatData)
                return Vector3(data[0],data[1],data[2])
            }
            set(value) = setAttributeData(VertexAttributes.Usage.Normal,index,size, floatArrayOf(value!!.x,value.y,value.z),backingArray)

        var uv: Vector2?
            get() {
                val data = getAttributeData(VertexAttributes.Usage.TextureCoordinates,floatData)
                return Vector2(data[0],data[1])
            }
            set(value) = setAttributeData(VertexAttributes.Usage.TextureCoordinates,index,size, floatArrayOf(value!!.x,value.y),backingArray)

        var color: Color?
            get() {
                val data = getAttributeData(VertexAttributes.Usage.ColorUnpacked,floatData)
                return Color(data[0],data[1],data[2],data[3])
            }
            set(value) = setAttributeData(VertexAttributes.Usage.ColorUnpacked,index,size, floatArrayOf(value!!.r,value.g,value.b,value.a),backingArray)

        fun printArray(floatArray: FloatArray) {
            println(Arrays.toString(floatArray))
        }

        fun getAttributeProperties(usage: Int): Pair<Int, Int> {
            val offset = attributes.findByUsage(usage).offset / 4
            val length = attributes.findByUsage(usage).numComponents
            return Pair(offset, length)
        }

        fun getVertexDataRange(vertexIndex:Int,vertexSize:Int): Pair<Int, Int> {
            val start = vertexIndex * vertexSize
            val end = start + vertexSize
            return Pair(start,end)
        }

        fun getVertexAttributeRange(vertexIndex: Int,vertexSize:Int, usage: Int): Pair<Int, Int> {
            val (offset, length) = getAttributeProperties(usage)
            val (start,end) = getVertexDataRange(vertexIndex,vertexSize)
            return Pair(start + offset,start + offset + length)
        }

        fun getVertexData(backingArray: FloatArray,vertexIndex:Int,vertexSize:Int): FloatArray {
            return backingArray.copyOfRange(vertexIndex * vertexSize,vertexIndex * vertexSize + vertexSize)
        }

        fun getAttributeData(usage: Int,vertexData:FloatArray): FloatArray {
            val (offset, length) = getAttributeProperties(usage)
            return vertexData.copyOfRange(offset,offset + length)
        }

        fun setVertexData(backingArray:FloatArray,vertexIndex: Int,vertexSize:Int,vertexData:FloatArray) {
            val (start,end) = getVertexDataRange(vertexIndex,vertexSize)
            System.arraycopy(vertexData,0,backingArray,start,vertexSize)
        }

        fun setAttributeData(usage: Int,vertexIndex:Int,vertexSize:Int,attributeData:FloatArray,backingArray: FloatArray) {
            val (start,end) = getVertexAttributeRange(vertexIndex,vertexSize,usage)
            System.arraycopy(attributeData,0,backingArray,start,attributeData.size)
    }

}
}
