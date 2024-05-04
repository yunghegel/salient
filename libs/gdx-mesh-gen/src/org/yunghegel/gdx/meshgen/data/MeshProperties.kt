package org.yunghegel.gdx.meshgen.data

import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import org.yunghegel.gdx.meshgen.data.attribute.AttributeInfo

class MeshProperties(val mesh: Mesh) {

    init {
    }

    val size = MeshSize(mesh.numVertices, mesh.numIndices)

    val bufferProperties = MeshAttributes(mesh.vertexAttributes.map { AttributeInfo(it) }, mesh.vertexSize / 4)



}

data class MeshSize(val vertexCount: Int, val indexCount: Int)

data class MeshData(val vertices: FloatArray, val indices: ShortArray)

data class MeshAttributes(val attributes: List<AttributeInfo>, val vertexSize: Int) {

    constructor(vertexAttributes: VertexAttributes) : this(vertexAttributes.map { AttributeInfo(it) }, vertexAttributes.vertexSize / 4)

    fun offsetFor(alias: String) : Int {
        return attributes.first { it.alias == alias }.offset
    }

    fun offsetFor(attribute: VertexAttribute) : Int {
        return attributes.first { it.usage == attribute.usage }.offset
    }
}