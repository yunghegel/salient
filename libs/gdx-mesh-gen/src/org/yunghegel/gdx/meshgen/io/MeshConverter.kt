package org.yunghegel.gdx.meshgen.io

import com.badlogic.gdx.graphics.*
import org.yunghegel.gdx.meshgen.data.base.*

interface MeshConverter<V: Vertex,F: Face,E: Edge,M:StructuredMesh<V,F,E>> : MeshConstructor<V,F,E,M> {

    override fun convert(mesh: Mesh) : M



}
