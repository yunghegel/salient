package org.yunghegel.gdx.meshgen.builder.operators

import org.yunghegel.gdx.meshgen.builder.ShapeMeshBuilder

class PlanarVertexCenter  : MeshOperator{

    override fun operateOn(mesh: ShapeMeshBuilder) {
        val idx = mesh.lastIndex()
    }

}