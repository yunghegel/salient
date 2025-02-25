package org.yunghegel.gdx.meshgen.data

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.utils.*
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.*
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import org.yunghegel.gdx.meshgen.data.base.*
import org.yunghegel.gdx.meshgen.io.*
import org.yunghegel.gdx.meshgen.util.*
import java.util.*
import kotlin.system.*

val EMPTY_MESH: ()->Mesh = {Mesh(true, 0, 0, VertexAttribute.Position())}

const val MAX_VERTS = 32000
const val MAX_INDICES = 32000

class GeometryCache<V, F, E, M>(val data: M, meshReconstructor: MeshConstructor<V, F, E, M>):CachedReference<M, Mesh>,
    Disposable, RenderableProvider, DirtySyncronized by data, MeshConstructor<V, F, E, M>
        where V:Vertex, F:Face, E:Edge, M:StructuredMesh<V, F, E> {

    var b: MeshPartBuilder? = null

    val dirtyVerts = Stack<ElementChangeEvent<V, ElementData<V>>>()

    val dirtyFaces = Stack<ElementChange<F, ElementData<F>>>()

    val dirtyEdges = Stack<ElementChange<E, ElementData<E>>>()

    override val syncronizer: DirtySyncronizer<M, Mesh> = meshReconstructor

    override val ref: M = data

    override var cached: Mesh by lazyMutable { EMPTY_MESH() }

    override val listeners: MutableList<DirtyListener<Mesh>> = mutableListOf()

    private var building: Boolean = false

    private val id: String = "id"

    val maxVerices = MAX_VERTS

    val maxIndices = MAX_INDICES

    val primitiveType = GL20.GL_TRIANGLES

    val vertexAttributes = data.vertexAttributes

    val vertexSize = vertexAttributes.vertexSize / 4

    private val renderable: Renderable = Renderable()

    private val mesh: Mesh by lazy { Mesh(false, maxVerices, maxIndices, vertexAttributes) }

    private val builder: MeshBuilder = MeshBuilder()

    init {
        with(data) {
            data.observeVerts {
                dirtyVerts.push(it)
                if (!initialConstructionComplete or building) {
                    return@observeVerts
                }
                else {
//                    perf("REBUILD ${dirtyVerts.size} vertices ...") {
                        while (!dirtyVerts.isEmpty()) {
                            val change = dirtyVerts.pop()
//                            buildVertex(this@GeometryCache.data, change, cached)
//                        }
                    }
                }
            }

            data.observeFaces {
                dirtyFaces.push(it)
                if (!initialConstructionComplete or building) {
                    return@observeFaces
                }
                else {
//                    perf("REBUILT ${dirtyFaces.size} faces") {
                        while (!dirtyFaces.isEmpty()) {
                            val change = dirtyFaces.pop()
//                            buildFace(this@GeometryCache.data, change, cached)
//                        }
                    }

                }
            }

            data.observeEdges {
                dirtyEdges.push(it)
                if (!initialConstructionComplete or building) {
                    return@observeEdges
                }
                else {
                    perf("REBUILT ${dirtyEdges.size} edges") {
                        while (!dirtyEdges.isEmpty()) {
                            val change = dirtyEdges.pop()
//                            buildEdge(this@GeometryCache.data, change, cached)
                        }
                    }
                }
            }
        }

    }

    override fun beginConstruction() {
        begin()
    }

    override fun endConstruction() {
        end()
    }

    override fun convert(mesh: Mesh): M {
        val ifs = data
        val props = MeshProperties(mesh)
        val size = mesh.vertexSize
        val stride = size / 4
        val vertexCount = mesh.numVertices
        val indexCount = mesh.numIndices

        val vertexArraySize = vertexCount * stride
        val indexArraySize = indexCount

        val vertices = FloatArray(vertexArraySize)
        val indices = ShortArray(indexArraySize)

        mesh.getVertices(vertices)
        mesh.getIndices(indices)

//        traverse indices
        for (i in 0 until indexCount) {
            val index = indices[i].toInt()
            val info = VertexInfo()
            val slice = mesh slice index

            val subslices = mesh subslices slice

            subslices.forEach {
                val alias = it.attribute.alias
                val values = it.values
                when (alias) {
                    "a_position"  -> {
                        info.setPos(values[0], values[1], values[2])
                    }

                    "a_normal"    -> {
                        info.setNor(values[0], values[1], values[2])
                    }

                    "a_color"     -> {
                        info.setCol(values[0], values[1], values[2], values[3])
                    }

                    "a_texCoord0" -> {
                        info.setUV(values[0], values[1])
                    }

                    "a_tangent"   -> {
                    }

                    "a_binormal"  -> {
                    }

                    else          -> {
                        println("MeshConvert: unknown attribute ${alias}")
                    }

                }
            }
//            println()

            val vert = ifs.createVertex(i, info)

        }
        var i = 0
        while (i < indexCount) {
            val v0: V = ifs.vertices().getAll().get(indices[i].toInt())
            val v1: V = ifs.vertices().getAll().get(indices[i+1].toInt())
            val v2: V = ifs.vertices().getAll().get(indices[i+2].toInt())
            //degenerate triangles?
            if (v0 !== v1 && v0 !== v2 && v1 !== v2) {
                val face = ifs.createFace(v0.index, v1.index, v2.index)
            }
            i += 3
        }

        return ifs
    }

    override fun buildEdge(struct: M, change: ElementChange<E, ElementData<E>>, mesh: Mesh): Mesh {
        return mesh
    }

    override fun buildFace(struct: M, change: ElementChange<F, ElementData<F>>, mesh: Mesh): Mesh {
        return mesh
    }

    override fun buildVertex(struct: M, change: ElementChange<V, ElementData<V>>, mesh: Mesh): Mesh {
        return mesh
    }

    override fun reconstruct(struct: M, mesh: Mesh): Mesh {
        return mesh
    }

    override fun construct(meshData: M): Mesh {
        beginConstruction()

        var faceCnt = 0
        var vertCnt = 0

        val time = measureTimeMillis {

            val vertTime = measureTimeMillis {
                meshData.vertices().forEach {v ->
                    val info = VInfo()
                    meshData.vertexDataFlags.forEachTrue {
                        val attr = it

                        when (attr.alias) {
                            "a_position"  -> {
                                info.setPos(v.position.x, v.position.y, v.position.z)
                            }

                            "a_normal"    -> {
                                info.setNor(v.normal.x, v.normal.y, v.normal.z)
                            }

                            "a_color"     -> {
                                info.setCol(v.color.x, v.color.y, v.color.z, v.color.w)
                            }

                            "a_texCoord0" -> {
                                info.setUV(v.uv.x, v.uv.y)
                            }

                            "a_tangent"   -> {
                                info.setTangent(v.tangent.x, v.tangent.y, v.tangent.z)
                            }

                            "a_binormal"  -> {
                                info.setBinormal(v.binormal.x, v.binormal.y, v.binormal.z)
                            }

                            else          -> {
                                println("MeshConvert: unknown attribute ${attr.alias}")
                            }

                        }
                    }
                    b?.vertex(info)
                    vertCnt++
                }
            }

            val faceTime = measureTimeMillis {
                meshData.faces().forEach {f ->
                    val indx = f.indices
                    b?.triangle(indx[0].toShort(), indx[1].toShort(), indx[2].toShort())
                    faceCnt++
                }
            }

            println("MeshConvert: $vertTime ms to convert $vertCnt vertices")

            println("MeshConvert: $faceTime ms to convert ${faceCnt / 3} faces")


            println("MeshConvert: $vertCnt vertices, $faceCnt triangles")

        }

        println("MeshConvert: $time ms to convert mesh")

        endConstruction()



        return builder.end()
    }

    fun begin(): MeshPartBuilder {
        return begin(primitiveType)
    }

    fun begin(primitiveType: Int): MeshPartBuilder {
        if (building) throw GdxRuntimeException("Call end() after calling begin()")
        building = true
        builder.begin(mesh.vertexAttributes)
        builder.part(id, primitiveType, renderable.meshPart)
        return builder
    }

    fun end() {
        if (!building) throw GdxRuntimeException("Call begin() prior to calling end()")
        building = false

        builder.end(mesh)
    }

    override fun getRenderables(renderables: Array<Renderable>, pool: Pool<Renderable>) {
        renderables.add(renderable)
    }

    override fun dispose() {
        mesh.dispose()
    }

    override fun retrieve(): Mesh {
        if (checkDirty()) {
            store(resolveValue())
        }
        return cached
    }

    override fun resolveValue(): Mesh {
        return super.resolveValue()
    }

}
