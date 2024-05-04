package org.yunghegel.gdx.meshgen.builder.operators

import org.yunghegel.gdx.meshgen.builder.ShapeMeshBuilder

interface MeshOperator {

    fun operateOn(mesh: ShapeMeshBuilder)

}