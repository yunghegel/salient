package org.yunghegel.gdx.meshgen.data.ifs

import org.yunghegel.gdx.meshgen.data.base.*
import kotlin.math.*

class IEdge(index: Int, mesh: IFSMesh) : Edge(index, mesh) {

    override var vertex0: IVertex
    override var vertex1: IVertex

    var midpoint by attribute(mesh.edgeMidpoint)
    var length by attribute(mesh.edgeLength)

    init {
        vertex0 = mesh.vertices().elements[toIdx] as IVertex
        vertex1 = mesh.vertices().elements[fromIdx] as IVertex

        mesh.position.listen { iVertex, vector3 ->
            if (iVertex == vertex0 || iVertex == vertex1) {
//                edge length
                val v0 = vertex0.position
                val v1 = vertex1.position
                val dx = v1.x - v0.x
                val dy = v1.y - v0.y
                val dz = v1.z - v0.z
                val len = sqrt((dx * dx + dy * dy + dz * dz))
                length = len

//                edge midpoint
                midpoint.set(v0).add(v1).scl(0.5f)

            }
        }
    }

    val elementData: ElementData<IEdge>
        get() = mesh.edges()

    override fun pair(index: Int): Edge {
        val edge = IEdge(index, mesh)
        edge.set(toIdx, fromIdx)
        return edge
    }

    fun resolveVertices() {
        vertex0 = mesh.vertices().elements[toIdx] as IVertex
        vertex1 = mesh.vertices().elements[fromIdx] as IVertex
    }

    override fun set(from: Int, to: Int) {
        super.set(from, to)
        resolveVertices()
    }

    override fun update() {
        super.update()
    }




}
