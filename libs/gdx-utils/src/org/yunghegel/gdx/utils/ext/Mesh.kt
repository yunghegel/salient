package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.model.Node
import org.lwjgl.opengl.GL20

fun xyGrid(size: Int, vararg attributes: VertexAttribute?): Mesh {
    val floatsPerVertex = 6
    val vertexCount = size * size
    val vertices = FloatArray(vertexCount * floatsPerVertex)

    for (i in 0 until size) {
        for (j in 0 until size) {
            val index = (i * size + j) * floatsPerVertex
            vertices[index] = j.toFloat()
            vertices[index + 1] = i.toFloat()
            vertices[index + 2] = 0f

            vertices[index + 3] = 0f
            vertices[index + 4] = 0f
            vertices[index + 5] = 1f
        }
    }

    val indicesCount = (size - 1) * (size - 1) * 6
    val indices = ShortArray(indicesCount)
    for (i in 0 until size - 1) {
        for (j in 0 until size - 1) {
            val index = (i * (size - 1) + j) * 6
            indices[index] = (i * size + j).toShort()
            indices[index + 1] = (i * size + j + 1).toShort()
            indices[index + 2] = ((i + 1) * size + j).toShort()

            indices[index + 3] = ((i + 1) * size + j).toShort()
            indices[index + 4] = (i * size + j + 1).toShort()
            indices[index + 5] = ((i + 1) * size + j + 1).toShort()
        }
    }

    val mesh = Mesh(true, vertices.size, vertices.size, *attributes)
    mesh.setVertices(vertices)
    mesh.setIndices(indices)

    return mesh
}

/**
 * Returns a grid mesh on the XZ plane with the given size.
 * @param size The size of the grid.
 * @return
 */
fun xzGrid(size: Int, vararg attributes: VertexAttribute?): Mesh {
    val floatsPerVertex = 6
    val vertexCount = size * size
    val vertices = FloatArray(vertexCount * floatsPerVertex)

    for (i in 0 until size) {
        for (j in 0 until size) {
            val index = (i * size + j) * floatsPerVertex
            vertices[index] = j.toFloat()
            vertices[index + 1] = 0f
            vertices[index + 2] = i.toFloat()

            vertices[index + 3] = 0f
            vertices[index + 4] = 1f
            vertices[index + 5] = 0f
        }
    }

    val indicesCount = (size - 1) * (size - 1) * 6
    val indices = ShortArray(indicesCount)
    for (i in 0 until size - 1) {
        for (j in 0 until size - 1) {
            val index = (i * (size - 1) + j) * 6
            indices[index] = (i * size + j).toShort()
            indices[index + 1] = (i * size + j + 1).toShort()
            indices[index + 2] = ((i + 1) * size + j).toShort()

            indices[index + 3] = ((i + 1) * size + j).toShort()
            indices[index + 4] = (i * size + j + 1).toShort()
            indices[index + 5] = ((i + 1) * size + j + 1).toShort()
        }
    }

    val mesh = Mesh(true, vertices.size, vertices.size, *attributes)
    mesh.setVertices(vertices)
    mesh.setIndices(indices)

    return mesh
}

/**
 * Returns a grid mesh on the YZ plane with the given size.
 * @param size The size of the grid.
 * @return
 */
fun yzGrid(size: Int, vararg attributes: VertexAttribute?): Mesh {
    val floatsPerVertex = 6
    val vertexCount = size * size
    val vertices = FloatArray(vertexCount * floatsPerVertex)

    for (i in 0 until size) {
        for (j in 0 until size) {
            val index = (i * size + j) * floatsPerVertex
            vertices[index] = 0f
            vertices[index + 1] = j.toFloat()
            vertices[index + 2] = i.toFloat()

            vertices[index + 3] = 1f
            vertices[index + 4] = 0f
            vertices[index + 5] = 0f
        }
    }

    val indicesCount = (size - 1) * (size - 1) * 6
    val indices = ShortArray(indicesCount)
    for (i in 0 until size - 1) {
        for (j in 0 until size - 1) {
            val index = (i * (size - 1) + j) * 6
            indices[index] = (i * size + j).toShort()
            indices[index + 1] = (i * size + j + 1).toShort()
            indices[index + 2] = ((i + 1) * size + j).toShort()

            indices[index + 3] = ((i + 1) * size + j).toShort()
            indices[index + 4] = (i * size + j + 1).toShort()
            indices[index + 5] = ((i + 1) * size + j + 1).toShort()
        }
    }

    val mesh = Mesh(true, vertices.size, vertices.size, *attributes)
    mesh.setVertices(vertices)
    mesh.setIndices(indices)

    return mesh
}

fun getVerticesByAttribute(mesh: Mesh, attribute: VertexAttribute): FloatArray {
    val stride = mesh.vertexSize / 4
    val offset = attribute.offset / 4
    val size = attribute.numComponents

    val result = FloatArray(mesh.numVertices * size)
    val vertices = FloatArray(mesh.numVertices * stride)
    mesh.getVertices(vertices)

    for (i in result.indices) {
        val vertex = i / size
        val vindex = vertex * stride + offset
        result[i] = vertices[vindex]
    }


    return result
}

fun getIndexedVertexList(mesh: Mesh): Array<FloatArray?> {
    val stride = mesh.vertexSize / 4
    val vertices = FloatArray(mesh.numVertices * stride)
    mesh.getVertices(vertices)
    val indices = ShortArray(mesh.numIndices)
    mesh.getIndices(indices)
    val attribute = mesh.getVertexAttribute(VertexAttributes.Usage.Position)
    val posOffset = attribute.offset / 4
    val result = arrayOfNulls<FloatArray>(mesh.numIndices / 3)
    var i = 0
    while (i < indices.size) {
        val vertex = indices[i].toInt() and 0xFFFF
        val vindex = vertex * stride + posOffset
        val x = vertices[vindex]
        val y = vertices[vindex + 1]
        val z = vertices[vindex + 2]
        result[i / 3] = floatArrayOf(x, y, z)
        i += 3
    }
    return result
}

fun getVertexCountByAttribute(mesh: Mesh, attribute: VertexAttribute): Int {
    val stride = mesh.vertexSize / 4
    return mesh.numVertices * stride
}

fun getTotalVerticesCount(mesh: Mesh): Int {
    val stride = mesh.vertexSize / 4
    return mesh.numVertices * stride
}

fun getTotalIndicesCount(mesh: Mesh): Int {
    return mesh.numIndices
}

//get total in model
fun getTotalVerticesCount(modelInstance: ModelInstance): Int {
    var count = 0
    val nodes: com.badlogic.gdx.utils.Array<Node> = collectNodes(modelInstance)
    for (node in nodes) for (part in node.parts) count += getTotalVerticesCount(part.meshPart.mesh)
    return count
}

fun getTotalIndicesCount(modelInstance: ModelInstance): Int {
    var count = 0
    val nodes: com.badlogic.gdx.utils.Array<Node> = collectNodes(modelInstance)
    for (node in nodes) for (part in node.parts) count += getTotalIndicesCount(part.meshPart.mesh)
    return count
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

fun getPositionAtIndex(mesh: Mesh, index: Int): FloatArray {
    val attribute = mesh.getVertexAttribute(VertexAttributes.Usage.Position)
    return getAtrributeAtIndex(mesh, attribute, index)
}

fun getNormalAtIndex(mesh: Mesh, index: Int): FloatArray {
    val attribute = mesh.getVertexAttribute(VertexAttributes.Usage.Normal)
    return getAtrributeAtIndex(mesh, attribute, index)
}

fun getUVAtIndex(mesh: Mesh, index: Int): FloatArray {
    val attribute = mesh.getVertexAttribute(VertexAttributes.Usage.TextureCoordinates)
    return getAtrributeAtIndex(mesh, attribute, index)
}

fun getColorAtIndex(mesh: Mesh, index: Int): FloatArray {
    val attribute = mesh.getVertexAttribute(VertexAttributes.Usage.ColorUnpacked)
    return getAtrributeAtIndex(mesh, attribute, index)
}

fun getVerticesArray(mesh: Mesh): FloatArray {
    val stride = mesh.vertexSize / 4
    val vertices = FloatArray(mesh.numVertices * stride)
    mesh.getVertices(vertices)
    return vertices
}

fun Mesh.createRenderable() :Renderable  {
    val renderable = Renderable()
    renderable.meshPart.mesh = this
    renderable.meshPart.primitiveType = GL20.GL_TRIANGLES
    renderable.meshPart.offset = 0
    renderable.meshPart.size = this.numIndices
    renderable.meshPart.center.set(0f, 0f, 0f)
    renderable.meshPart.halfExtents.set(0.5f, 0.5f, 0.5f)
    return renderable
}