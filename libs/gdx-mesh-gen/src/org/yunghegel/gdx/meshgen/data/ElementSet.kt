package org.yunghegel.gdx.meshgen.data

import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.base.ElementData
import org.yunghegel.gdx.meshgen.data.base.Vertex
import org.yunghegel.gdx.meshgen.data.ifs.IFSMesh
import org.yunghegel.gdx.meshgen.data.ifs.IVertex

abstract class ElementSet<E:Element,Data:ElementData<E>>(val mesh:IFSMesh) {

    abstract operator fun getValue(thisRef: Any?, property: Any?): Set<E>

}

class VertexSet(mesh: IFSMesh) : ElementSet<IVertex,ElementData<IVertex>>(mesh) {

    override fun getValue(thisRef: Any?, property: Any?): Set<IVertex> {
        return mesh.vertices.toSet()
    }

}