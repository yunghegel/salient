package org.yunghegel.gdx.meshgen.data.base

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.*
import org.yunghegel.gdx.meshgen.data.*
import org.yunghegel.gdx.meshgen.data.attribute.*
import org.yunghegel.gdx.meshgen.math.*

interface StructuredMesh<V:Vertex,F:Face,E:Edge> : DirtySyncronized, ElementChangeNotifier<V,F,E> {

    var stateChanged : Boolean

    var constructing : Boolean

    var initialConstructionComplete : Boolean

    val vertexDataFlags : SetBitmask<ElementAttributeReference<V>>

    val all: List<Element>
        get() = allElements()

    val vertexAttributes: VertexAttributes

    val edgeData: ElementData<E>
        get() = edges()

    val verticesData: ElementData<V>
        get() = vertices()

    val facesData: ElementData<F>
        get() = faces()

    val geometryCache: GeometryCache<V,F,E,out StructuredMesh<V,F,E>>

    fun populateAttributes()

    fun edges() : ElementData<E>

    fun vertices() : ElementData<V>

    fun faces() : ElementData<F>

    fun createVertex(index: Int, vertexInfo: VertexInfo) : V

    fun createEdge(from: Int, to: Int) : E

    fun createFace(vararg vertices: Int) : F

    fun getVertexSize() : Int

    fun allElements() : List<Element> {
        val all = mutableListOf<Element>()
        all.addAll(vertices().elements)
        all.addAll(edges().elements)
        all.addAll(faces().elements)
        return all
    }

    fun observeVerts(observer: VertexChangeObserver<V>) {
        vertexObservers.add(observer)
    }

    fun observeFaces(observer: FaceChangeObserver<F>) {
        faceObservers.add(observer)
    }

    fun observeEdges(observer: EdgeChangeObserver<E>) {
        edgeObservers.add(observer)
    }

    fun emitVertxChanged(event: ElementChangeEvent<V,ElementData<V>>) {
        vertexObservers.forEach { it.onElementChange(event) }
    }

    fun emitFaceChanged(event: ElementChangeEvent<F,ElementData<F>>) {
        faceObservers.forEach { it.onElementChange(event) }
    }

    fun emitEdgeChanged(event: ElementChangeEvent<E,ElementData<E>>) {
        edgeObservers.forEach { it.onElementChange(event) }
    }



}
