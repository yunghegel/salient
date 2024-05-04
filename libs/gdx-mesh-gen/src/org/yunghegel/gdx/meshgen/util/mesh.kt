package org.yunghegel.gdx.meshgen.util

import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.math.Octree
import org.yunghegel.gdx.meshgen.data.attribute.AttributeInfo


fun getValuesForAttribute(attribute: AttributeInfo, vertexSize:Int, vertices:FloatArray, index:Int) : FloatArray  {

    val values = FloatArray(attribute.size)

    for (i in 0 until attribute.size) {
        values[i] = vertices[index * vertexSize + attribute.offset + i]
    }

    return values

}

fun getAtrributeAtIndex(mesh: Mesh, attribute: VertexAttribute, index: Int): FloatArray {
    //check if mesh has attribute
    val size = attribute.numComponents
    val stride = mesh.vertexSize / 4
    val vals = FloatArray(size)
    val vertices = FloatArray(mesh.numVertices * stride)
    mesh.getVertices(vertices)
    val offset = attribute.offset / 4
    for (i in 0 until size) {
        vals[i] = vertices[index * stride + offset + i]
    }
    return vals
}

fun getVertexSlice(vertexBuffer: FloatArray, vertexSize:Int, index: Int): VertexSlice {
    val stride = vertexSize / 4
    return VertexSlice(index,vertexBuffer.sliceArray(index * stride until index * stride + stride))
}

fun getSubslices(vertexBuffer: FloatArray, vertexSize:Int, index: Int, vertexAttributes: VertexAttributes) : MutableList<VertexSubslice> {
    val vertex = getVertexSlice(vertexBuffer, vertexSize, index)
    val subslices = mutableListOf<VertexSubslice>()
    vertexAttributes.forEach {
        val offset = it.offset / 4
        val size = it.numComponents
        val values = vertex.values.sliceArray(offset until offset + size)
        subslices.add(VertexSubslice(index,it, values))
    }
    return subslices
}

infix fun Mesh.slice(index: Int) : VertexSlice {
    val size = vertexSize
    val stride = size / 4
    val vertexCount = numVertices
    val indexCount = numIndices

    val vertexArraySize = vertexCount * stride
    val indexArraySize = indexCount

    val vertices = FloatArray(vertexArraySize)
    val indices = ShortArray(indexCount)

    getVertices(vertices)
    getIndices(indices)
    return getVertexSlice(vertices, size, index)
}

infix fun Mesh.attributes(pair: Pair<Int,VertexAttributes>) : List<VertexSubslice> {
    val size = vertexSize
    val stride = size / 4
    val vertexCount = numVertices
    val indexCount = numIndices

    val vertexArraySize = vertexCount * stride
    val indexArraySize = indexCount

    val vertices = FloatArray(vertexArraySize)
    val indices = ShortArray(indexCount)

    getVertices(vertices)
    getIndices(indices)
    return getSubslices(vertices, vertexSize, pair.first, pair.second)
}

infix fun Mesh.subslices(slice:VertexSlice) : List<VertexSubslice> {
    val vertexAttributes = slice.values
    val vertexSize = vertexAttributes.size
    val index = slice.index
    val subslices = mutableListOf<VertexSubslice>()
    getVertexAttributes().forEach {
        subslices.add(VertexSlice.getSubslice(slice,it))
    }
    return subslices
}




@JvmInline
value class VertexSlice(val slice : Pair<Int,FloatArray>) {

    val index : Int
        get() = slice.first

    val values : FloatArray
        get() = slice.second

    constructor(index: Int, values: FloatArray) : this(Pair(index,values))

    fun getSubslice(attribute: VertexAttribute) : VertexSubslice {
        val offset = attribute.offset / 4
        val size = attribute.numComponents
        val values = slice.second.sliceArray(offset until offset + size)
        return VertexSubslice(slice.first,attribute, values)
    }

    infix fun attribute(attribute: VertexAttribute) : VertexSubslice {
        return getSubslice(attribute)
    }



    companion object {

        fun getVertexSlice(vertexBuffer: FloatArray, vertexSize:Int, index: Int): VertexSlice {
            val stride = vertexSize / 4
            return VertexSlice(index,vertexBuffer.sliceArray(index * stride until index * stride + stride))
        }

        fun getSubslice(slice:VertexSlice, attribute: VertexAttribute) : VertexSubslice {
            val offset = attribute.offset / 4
            val size = attribute.numComponents
            val values = slice.values.sliceArray(offset until offset + size)
            return VertexSubslice(slice.index,attribute, values)
        }
    }
}



@JvmInline
value class VertexSubslice(val slice : Triple<Int,VertexAttribute,FloatArray>) {

    val index : Int
        get() = slice.first

    val attribute : VertexAttribute
        get() = slice.second

    val values : FloatArray
        get() = slice.third

    constructor(index: Int, attribute: VertexAttribute, values: FloatArray) : this(Triple(index,attribute,values))

}

