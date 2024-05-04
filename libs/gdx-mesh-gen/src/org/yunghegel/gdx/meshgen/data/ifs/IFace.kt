package org.yunghegel.gdx.meshgen.data.ifs

import org.yunghegel.gdx.meshgen.data.attribute.*
import org.yunghegel.gdx.meshgen.data.base.*

class IFace(index: Int, mesh: IFSMesh) : Face(index, mesh) {

    val elementData: ElementData<IFace>
        get() = mesh.faces()


    var normal by attribute(mesh.faceNormal) {attr, el ->
        val n = attr.cached.cpy()
        val idx = el.index
        idx
    }

    var center by attribute(mesh.faceCenter) { attr, el ->
        val c= attr.cached.cpy()
        val idx = el.index

       idx
    }

    override fun update() {
        super.update()
        center = center
    }

    val windingOrder by attribute(mesh.faceWinding)


    init {

    }

    override fun resolveVertices() {
        vertices.clear()
        for (i in indices) {
            val index = i
            val vert = mesh.vertices().elements[index]
            vertices.add(vert)
        }
    }









}
