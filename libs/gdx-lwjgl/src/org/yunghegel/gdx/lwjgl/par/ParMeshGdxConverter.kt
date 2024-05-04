package org.yunghegel.gdx.lwjgl.par

import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.util.nfd.NFDFilterItem
import org.lwjgl.util.nfd.NativeFileDialog.*
import org.lwjgl.util.par.ParShapes.npar_shapes_export
import org.lwjgl.util.par.ParShapesMesh
import java.nio.FloatBuffer
import java.nio.IntBuffer


class ParMeshGdxConverter(val parShapesMesh: ParShapesMesh) {


    private fun exportMesh(mesh:ParShapesMesh) {
        stackPush().use { stack ->
            val filter = NFDFilterItem.malloc(1, stack)
            filter[0]
                .name(stack.UTF8("Wavefront Object"))
                .spec(stack.UTF8("obj"))

            val pp = stack.mallocPointer(1)

            val result: Int = NFD_SaveDialog(pp, filter, null, "mesh.obj")
            when (result) {
                NFD_OKAY -> {
                    val path = pp[0]
                    npar_shapes_export(mesh.address(), path)
                    NFD_FreePath(path)
                }

                NFD_ERROR -> System.err.format("Error: %s\n", NFD_GetError())
                else -> {
                    println("User pressed cancel.")
                }
            }
        }
    }




    val numElements = parShapesMesh.ntriangles() * 3
    val positions: FloatBuffer = parShapesMesh.points(numElements * 3)
    val indices: IntBuffer = parShapesMesh.triangles(numElements)

    val normals = parShapesMesh.normals(numElements * 3)
    val tcoords = parShapesMesh.tcoords(numElements * 2)
    val hasNormals = normals != null
    val hasTexCoords = tcoords != null
//
    val vertexAttributes :VertexAttributes

    val vertexSize : Int

    val vertices : FloatArray


    init {
        vertexAttributes = buildVertexAttributes()
        vertexSize = vertexAttributes.vertexSize / 4
        val stride = vertexSize / 4
        vertices = FloatArray(numElements * vertexSize)

        val posOffset = vertexAttributes.findByUsage(VertexAttributes.Usage.Position).offset / 4
        val normalOffset = vertexAttributes.findByUsage(VertexAttributes.Usage.Normal)?.offset?.div(4) ?: -1
        val texCoordOffset = vertexAttributes.findByUsage(VertexAttributes.Usage.TextureCoordinates)?.offset!! / 4
        val texcoords =  ParShapesMesh.ntcoords(parShapesMesh.address(),numElements * 2)
        val normals = ParShapesMesh.nnormals(parShapesMesh.address(),numElements * 3)
        for (i in 0 until numElements*2) {
            println(texcoords!!.get(i))
        }
        println(texcoords?.get(0))
        insertAttribute(posOffset, positions)


    }


    fun insertAttribute(offset:Int, buffer: FloatBuffer){
        for (i in 0 until numElements){
            val index = i * vertexSize
            vertices[index + offset] = buffer.get(i * 3)
            vertices[index + offset + 1] = buffer.get(i * 3 + 1)
            vertices[index + offset + 2] = buffer.get(i * 3 + 2)
        }
    }

    fun buildVertexAttributes() : VertexAttributes {
        val vertexAttributes = mutableListOf<VertexAttribute>()
        vertexAttributes.add(VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"))
        if (hasNormals) {
            vertexAttributes.add(VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_normal"))
        }
        if (hasTexCoords) {
            vertexAttributes.add(VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"))
        }
        return VertexAttributes(*vertexAttributes.toTypedArray())
    }

    companion object {
        val vinfo0 = MeshPartBuilder.VertexInfo()
        val vinfo1 = MeshPartBuilder.VertexInfo()
    }

    fun floatBufferToArrayCopy(buffer: java.nio.FloatBuffer): FloatArray {
        val array = FloatArray(buffer.capacity())
        buffer.get(array)
        return array
    }





}