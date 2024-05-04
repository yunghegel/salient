package org.yunghegel.gdx.meshgen.data.base

import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.data.attribute.*
import org.yunghegel.gdx.meshgen.data.ifs.*
import org.yunghegel.gdx.meshgen.math.*

abstract class Vertex(index: Int, mesh: IFSMesh) : Element(index, mesh) {

    abstract val position: Vector3

    abstract val normal: Vector3

    abstract val color: Vector4

    abstract val uv: Vector2

    abstract val binormal: Vector3

    abstract val tangent: Vector3

    val attrFlags = EnumBitmask(VBOAttribute::class)

    init {
        attrFlags.set(VBOAttribute.POSITION,true)
    }

    fun has(element: Element): Boolean {
        val hash = element.hashCode()
        val thisHash = this.hashCode()
        return hash and thisHash == hash
    }

    fun contains(e: Element) : Boolean {
        val hash = e.hashCode()
        val thisHash = hashCode()
        return (hash and thisHash) == hash
    }

    fun getEdges() : List<IEdge> {
        val edges = mutableListOf<IEdge>()
        for (e in mesh.edges) {
            if (e has this) {
                edges.add(e)
            }
        }
        return edges
    }

    fun getFaces() : List<IFace> {
        val faces = mutableListOf<IFace>()
        for (f in mesh.faces) {
            if (f uses this) {
                faces.add(f)
            }
        }
        return faces
    }

    override fun update() {

    }

    infix fun usedby (e: Edge) : Boolean {
        return contains(e)
    }

    infix fun usedby (e: Face) : Boolean {
        return e uses this
    }

    override fun hashCode(): Int {
//      convert the index to a bitmask
        return 1 shl index
    }

    override fun equals(other: Any?): Boolean {
        if (other is IVertex) {
            return position == other.position
        }
        return false
    }


}
