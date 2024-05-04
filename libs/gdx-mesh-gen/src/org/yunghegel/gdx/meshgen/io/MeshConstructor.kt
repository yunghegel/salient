package org.yunghegel.gdx.meshgen.io

import com.badlogic.gdx.graphics.*
import org.yunghegel.gdx.meshgen.data.*
import org.yunghegel.gdx.meshgen.data.base.*

interface MeshConstructor<V:Vertex,F:Face,E:Edge,T:StructuredMesh<V,F,E>> : DirtySyncronizer<T, Mesh> {

    fun beginConstruction()

    fun construct(struct: T) : Mesh

    fun reconstruct(struct: T,mesh:Mesh) : Mesh

    fun buildVertex(struct :T, change: ElementChange<V,ElementData<V>>, mesh:Mesh) : Mesh

    fun buildFace(struct :T, change: ElementChange<F,ElementData<F>>, mesh:Mesh) : Mesh

    fun buildEdge(struct :T, change: ElementChange<E,ElementData<E>>, mesh:Mesh) : Mesh

    fun endConstruction()

    fun convert(mesh: Mesh) : T

    override fun getCurrent(ref: T): Mesh {
        return ref.geometryCache.cached
    }

}
