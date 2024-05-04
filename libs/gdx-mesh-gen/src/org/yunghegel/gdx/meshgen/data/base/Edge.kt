package org.yunghegel.gdx.meshgen.data.base

import org.yunghegel.gdx.meshgen.data.ifs.*

abstract class Edge(index:Int,mesh: IFSMesh) : Element(index,mesh) {


    abstract val vertex0: Vertex
    abstract val vertex1: Vertex

    var fromIdx: Int = 0
    var toIdx: Int = 0

    open fun set(from: Int,to: Int) {
        fromIdx = from
        toIdx = to
    }



    abstract fun pair(index: Int): Edge

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Edge) return false

        if (fromIdx != other.fromIdx) return false
        if (toIdx != other.toIdx) return false

        return true
    }

    override fun hashCode(): Int {
        //bitwise encoding of the two vertices of the edge
        return vertex0.hashCode() and vertex1.hashCode()
    }

    infix fun has (vertex: Vertex) : Boolean {
        return vertex usedby this
    }

    infix fun usedby (face: Face) : Boolean {
        return face uses this
    }

    infix fun connects (edge: Edge) : Boolean {
        return has (edge.vertex0) || has (edge.vertex1)
    }

}
