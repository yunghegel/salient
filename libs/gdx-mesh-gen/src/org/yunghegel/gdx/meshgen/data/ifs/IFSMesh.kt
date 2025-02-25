package org.yunghegel.gdx.meshgen.data.ifs

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.*
import org.yunghegel.gdx.meshgen.data.*
import org.yunghegel.gdx.meshgen.data.VertexInfo
import org.yunghegel.gdx.meshgen.data.attribute.*
import org.yunghegel.gdx.meshgen.data.attribute.types.*
import org.yunghegel.gdx.meshgen.data.base.*
import org.yunghegel.gdx.meshgen.data.base.ElementData
import org.yunghegel.gdx.meshgen.io.*
import org.yunghegel.gdx.meshgen.math.*

object IFS : ElementAttributes<IVertex,IFace,IEdge,IFSMesh>()

open class IFSMesh(vertAttributes: VertexAttributes = VertexAttributes(VertexAttribute.Normal(), VertexAttribute.Position()))
    : StructuredMesh<IVertex,IFace,IEdge>  {

        override val vertexAttributes = vertAttributes

        override var stateChanged = false

    final override var constructing = false
        set (value) {
            if (!initialConstructionComplete && value) {
                initialConstructionComplete = true
            }
            field = value
        }

     final override var initialConstructionComplete = false
         set (value ) {
             if (field) {
                  throw IllegalStateException("Initial construction already complete")
             }
              field = value
         }


    internal val vertices = ElementData(this,object:ElementFactory<IVertex> {
        override fun create(index: Int): IVertex {
            return IVertex(index,this@IFSMesh)
        }
    })

    internal val faces = ElementData(this,object:ElementFactory<IFace> {
        override fun create(index: Int): IFace {
            return IFace(index,this@IFSMesh)
        }
    })

    internal val edges = ElementData(this,object:ElementFactory<IEdge> {
        override fun create(index: Int): IEdge {
            return IEdge(index,this@IFSMesh)
        }
    })

    override val edgeData: ElementData<IEdge>
        get() = super.edgeData

    override val verticesData: ElementData<IVertex>
        get() = super.verticesData

    override val facesData: ElementData<IFace>
        get() = super.facesData

    override val edgeObservers: MutableList<EdgeChangeObserver<IEdge>> = mutableListOf()
    override val vertexObservers: MutableList<VertexChangeObserver<IVertex>> = mutableListOf()
    override val faceObservers: MutableList<FaceChangeObserver<IFace>> = mutableListOf()

    override val geometryCache = GeometryCache(this,IFSMeshConstructor())





    val a = ElementAttributes<IVertex,IFace,IEdge,IFSMesh>()

    override val vertexDataFlags = IFS.BUFFER.mask
    val meshAttributes = MeshAttributes(vertAttributes)

    val position = Vector3ElementAttribute(IFS.BUFFER.POSITION)
    val normal = Vector3ElementAttribute(IFS.BUFFER.NORMAL)
    val color = Vector4ElementAttribute(IFS.BUFFER.COLOR)
    val uv = Vector2ElementAttribute(IFS.BUFFER.UV)
    val binormal = Vector3ElementAttribute(IFS.BUFFER.BINORMAL)
    val tangent = Vector3ElementAttribute(IFS.BUFFER.TANGENT)


    val faceNormal = Vector3ElementAttribute(IFS.FACE.NORMAL)

    val faceCenter = Vector3ElementAttribute(IFS.FACE.CENTER)
    val faceWinding = EnumElementAttribute(IFS.FACE.WINDING, WindingOrder::class.java)

    val edgeMidpoint = Vector3ElementAttribute(IFS.EDGE.MIDPOINT)
    val edgeLength = FloatElementAttribute(IFS.EDGE.LENGTH)

    val edgePickableMat = MaterialElementAttribute(IFS.EDGE.PICKABLE_MAT)
    val vertexPickableMat = MaterialElementAttribute(IFS.VERTEX.PICKABLE_MAT)
    val facePickableMat = MaterialElementAttribute(IFS.FACE.PICKABLE_MAT)

    val sizeInFloats : Int by lazy {
        var int = 0
        vertexDataFlags.forEachTrue {
            int += it.size()
        }
        int
    }



    init {
        vertAttributes.forEach {
        val attr = IFS.BUFFER.mapTo(it)
        if(attr != null) {
            vertexDataFlags.set(attr,true)
            }
        }

        faceCenter.dependencies = setOf(position)
        faceNormal.dependencies = setOf(position)

        edgeMidpoint.dependencies = setOf(position)

        println("IFS Mesh initalized:\n$vertexDataFlags")
        populateAttributes()

        observeVerts{ change ->
            println("Vertex[${change.element.index}] EVENT: ${change.type}")
        }
        observeFaces { change ->
            println("Face[${change.element.index}] EVENT: ${change.type}")
        }
        observeEdges { change ->
            println("Edge[${change.element.index}] -> EVENT: ${change.type}")
        }
    }

//    fun observeVerts (observer: VertexChangeObserver<IVertex>) {
//        vertexObservers.add(observer)
//    }
//
//    fun observeFaces(observer: FaceChangeObserver<IFace>) {
//        faceObservers.add(observer)
//    }
//
//    fun observeEdges (observer: EdgeChangeObserver<IEdge>) {
//        edgeObservers.add(observer)
//    }
//
//    fun emitVertxChanged(event: ElementChangeEvent<IVertex,ElementData<IVertex>>) {
//        vertexObservers.forEach { it.onElementChange(event) }
//    }
//
//    fun emitFaceChanged(event: ElementChangeEvent<IFace,ElementData<IFace>>) {
//        faceObservers.forEach { it.onElementChange(event) }
//    }
//
//    fun emitEdgeChanged(event: ElementChangeEvent<IEdge,ElementData<IEdge>>) {
//        edgeObservers.forEach { it.onElementChange(event) }
//    }


    final override fun populateAttributes() {

        vertices.clearAttributes()
        vertices.addAttribute(position)
        vertices.addAttribute(normal)
        vertices.addAttribute(binormal)
        vertices.addAttribute(tangent)
        vertices.addAttribute(color)
        vertices.addAttribute(uv)
        vertices.addAttribute(vertexPickableMat)

        faces.clearAttributes()
        faces.addAttribute(faceNormal)
        faces.addAttribute(faceCenter)
        faces.addAttribute(faceWinding)

        edges.clearAttributes()
        edges.addAttribute(edgeMidpoint)
        edges.addAttribute(edgeLength)
    }

    override fun getVertexSize(): Int {
        return sizeInFloats
    }

    private val attributes : List<BaseAttribute<*,*>>
        get() {
            val attr = mutableListOf<BaseAttribute<*,*>>()
            attr.addAll(vertices.attributes.values)
            attr.addAll(faces.attributes.values)
            attr.addAll(edges.attributes.values)
            return attr
        }

    override fun edges(): ElementData<IEdge> {
        return edges
    }

    override fun vertices(): ElementData<IVertex> {
        return vertices
    }

    override fun faces(): ElementData<IFace> {
        return faces
    }

    override fun createVertex(index:Int, vertexInfo: VertexInfo) : IVertex{
        val vertex = vertices.create(index)
        position.set(vertex,vertex.position)
        normal.set(vertex,vertex.normal)
        color.set(vertex,vertex.color)
        uv.set(vertex,vertex.uv)

        vertex.position = vertexInfo.position
        vertex.normal = vertexInfo.normal
        vertex.color = vertexInfo.color.toVector4()
        vertex.uv = vertexInfo.uv


        val changeEvent = ElementChangeEvent(ChangeType.INSERTION,vertex,vertices,index)

        emitVertxChanged(changeEvent)

        return vertex
    }

    override fun createFace(vararg vertices: Int): IFace {
        val face = faces.create()
        face.set(*vertices)
//        face.resolveVertices()

        val changeEvent = ElementChangeEvent(ChangeType.INSERTION,face,faces,face.index)

        emitFaceChanged(changeEvent)

        return face
    }

    override fun createEdge(from: Int, to: Int): IEdge {
        val edge = edges.create()
        edge.set(from,to)
        edge.resolveVertices()

        val changeEvent = ElementChangeEvent(ChangeType.INSERTION,edge,edges,edge.index)

        emitEdgeChanged(changeEvent)

        return edge
    }

    override fun isDirty(): Boolean {
        return stateChanged or(constructing)
    }

    override fun setClean() {
        stateChanged = false
    }

    override fun setDirty() {
        stateChanged = true
    }

    fun startBuilding() {
        constructing = true
    }

    fun finish() {
        constructing = false
    }


    fun isBuilding() : Boolean {
        return constructing
    }

    override fun update() {

    }

}
