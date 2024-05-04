package org.yunghegel.gdx.meshgen.data.base

import org.yunghegel.gdx.meshgen.data.ifs.IFSMesh
import org.yunghegel.gdx.meshgen.data.ifs.IVertex

abstract class Face(index:Int,mesh: IFSMesh) : Element(index,mesh) {


    lateinit var indices: IntArray

    val vertices = mutableListOf<IVertex>()

    abstract fun resolveVertices()

    fun set(vararg indices: Int) {
        this.indices = IntArray(indices.size)
        for (i in indices.indices) this.indices[i] = indices[i]
        resolveVertices()
    }

    override fun hashCode(): Int {
        var hash = 0
        for (v in vertices)
            hash = hash.xor(v.hashCode())
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Face) return false

        if (!indices.contentEquals(other.indices)) return false
        if (vertices != other.vertices) return false

        return true
    }

    override fun toString(): String {
        return "IFace(index=$index, indices=${indices.contentToString()})"
    }

    infix fun uses (vertex: Vertex): Boolean {
        return vertices.contains(vertex)
    }

    infix fun uses (edge: Edge): Boolean {
        val hash = edge.hashCode()
        val thisHash = this.hashCode()
        return (hash and thisHash) == hash
    }
}