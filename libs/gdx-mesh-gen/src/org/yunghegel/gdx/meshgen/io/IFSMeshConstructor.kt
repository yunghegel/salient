package org.yunghegel.gdx.meshgen.io

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.utils.*
import org.yunghegel.gdx.meshgen.data.*
import org.yunghegel.gdx.meshgen.data.base.*
import org.yunghegel.gdx.meshgen.data.ifs.*
import org.yunghegel.gdx.meshgen.util.*
import kotlin.system.*

class IFSMeshConstructor : MeshConverter<IVertex,IFace,IEdge,IFSMesh> {

    val builder = MeshBuilder()

    override fun beginConstruction() {

    }

    override fun endConstruction() {

    }

    override fun convert(mesh: Mesh) : IFSMesh {
        val ifs = IFSMesh(mesh.vertexAttributes)
        val props = MeshProperties(mesh)
        val size = mesh.vertexSize
        val stride = size / 4
        val vertexCount = mesh.numVertices
        val indexCount = mesh.numIndices

        val vertexArraySize = vertexCount * stride
        val indexArraySize = indexCount

        val vertices = FloatArray(vertexArraySize)
        val indices = ShortArray(indexArraySize)

        mesh.getVertices(vertices)
        mesh.getIndices(indices)

        ifs.startBuilding()
//        traverse indices
        for (i in 0 until indexCount) {
           val index = indices[i].toInt()
            val info = VertexInfo()
            val slice = mesh slice index


            val subslices = mesh subslices slice

            subslices.forEach {
                val alias = it.attribute.alias
                val values = it.values
                when (alias) {
                    "a_position" -> {
                        info.setPos(values[0], values[1], values[2])
                    }
                    "a_normal" -> {
                        info.setNor(values[0],values[1], values[2])
                    }
                    "a_color" -> {
                        info.setCol(values[0], values[1], values[2], values[3])
                    }
                    "a_texCoord0" -> {
                        info.setUV(values[0], values[1])
                    }
                    "a_tangent" -> {
                    }
                    "a_binormal" -> {
                    }
                    else -> {
                        println("MeshConvert: unknown attribute ${alias}")
                    }

                }
            }
//            println()


            val vert = ifs.createVertex(i,info)

        }
            var i = 0
            while (i < indexCount) {
                val v0: IVertex = ifs.vertices().elements[indices[i].toInt()]
                val v1: IVertex = ifs.vertices().elements[indices[i+1].toInt()]
                val v2: IVertex = ifs.vertices().elements[indices[i+2].toInt()]
                //degenerate triangles?
                if (v0 !== v1 && v0 !== v2 && v1 !== v2) {
                 val face = ifs.createFace(v0.index, v1.index, v2.index)
                }
                i += 3
            }

        ifs.finish()

        return ifs
    }

    override fun buildEdge(struct: IFSMesh, change: ElementChange<IEdge, ElementData<IEdge>>, mesh: Mesh): Mesh {
        return mesh
    }

    override fun buildFace(struct: IFSMesh, change: ElementChange<IFace, ElementData<IFace>>, mesh: Mesh): Mesh {
        return mesh 
    }

    override fun buildVertex(struct: IFSMesh, change: ElementChange<IVertex, ElementData<IVertex>>, mesh: Mesh): Mesh {
        return mesh 
    }

    override fun reconstruct(struct: IFSMesh, mesh: Mesh): Mesh {
       return mesh
    }

    override fun construct(struct: IFSMesh): Mesh {
        struct.startBuilding()
        builder.begin(struct.vertexAttributes, GL20.GL_TRIANGLES)
        builder.part("part1", GL20.GL_TRIANGLES)
        builder.setVertexTransform(null)


        var faceCnt = 0
        var vertCnt = 0

        val time = measureTimeMillis {

        val vertTime = measureTimeMillis {
            struct.vertices.forEach {v ->
                val info = VInfo()
                struct.vertexDataFlags.forEachTrue {
                    val attr = it

                    when (attr.alias) {
                        "a_position" -> {
                            info.setPos(v.position.x, v.position.y, v.position.z)
                        }
                        "a_normal" -> {
                            info.setNor(v.normal.x,v.normal.y, v.normal.z)
                        }
                        "a_color" -> {
                            info.setCol(v.color.x, v.color.y, v.color.z, v.color.w)
                        }
                        "a_texCoord0" -> {
                            info.setUV(v.uv.x, v.uv.y)
                        }
                        "a_tangent" -> {
                            info.setTangent(v.tangent.x, v.tangent.y, v.tangent.z)
                        }
                        "a_binormal" -> {
                            info.setBinormal(v.binormal.x, v.binormal.y, v.binormal.z)
                        }
                        else -> {
                            println("MeshConvert: unknown attribute ${attr.alias}")
                        }

                    }
                }
                builder.vertex(info)
                vertCnt++
            }
         }

            val faceTime = measureTimeMillis {
                struct.faces.forEach {f ->
                    val indx = f.indices
                    builder.triangle(indx[0].toShort(), indx[1].toShort(), indx[2].toShort())
                    faceCnt++
                }
            }

            println("MeshConvert: ${vertTime} ms to convert ${vertCnt} vertices")

            println("MeshConvert: ${faceTime} ms to convert ${faceCnt / 3} faces")


            val totalVerts = builder.numVertices
            val totalTris = (builder.lastIndex()+1) / 3
            println("MeshConvert: ${totalVerts} vertices, ${totalTris} triangles")

            if (totalVerts != vertCnt) {
                println("MeshConvert: vertex count mismatch: ${totalVerts} != ${vertCnt}")
            }
            if (totalTris != faceCnt) {
                println("MeshConvert: triangle count mismatch: ${totalTris} != ${faceCnt}")
            }



        }

        println("MeshConvert: ${time} ms to convert mesh")

        struct.finish()
        return builder.end()
    }


}
