package src

import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo
import com.badlogic.gdx.math.*


import org.yunghegel.gdx.meshgen.data.attribute.*
import org.yunghegel.gdx.meshgen.data.base.ChangeType
import org.yunghegel.gdx.meshgen.data.base.ElementChangeEvent
import org.yunghegel.gdx.meshgen.data.ifs.*
import types.headlessTest
import kotlin.test.Test
class VertexTests {


    val pos = floatArrayOf(0f,0f,0f)
    val norm = floatArrayOf(1f,1f,1f)
    val uv = floatArrayOf(0f,1f)
    val color = floatArrayOf(1f,0f,0f)

    val vert: IVertex
    val vert2: IVertex
    val vert3: IVertex

    val edge1: IEdge
    val edge2: IEdge
    val edge3: IEdge

    val face: IFace


    val attributes = VertexAttributes(VertexAttribute.Position(),VertexAttribute.Normal(),VertexAttribute.TexCoords(0),VertexAttribute.ColorUnpacked())
    val floatArray = FloatArray(attributes.vertexSize / 4)
    val shortArray = ShortArray(16)

    val ifs : IFSMesh

    init {
        val posOffset = attributes.findByUsage(VertexAttributes.Usage.Position).offset / 4
        val normOffset = attributes.findByUsage(VertexAttributes.Usage.Normal).offset / 4
        val uvOffset = attributes.findByUsage(VertexAttributes.Usage.TextureCoordinates).offset / 4
        val colorOffset = attributes.findByUsage(VertexAttributes.Usage.ColorUnpacked).offset / 4

        val posLength = attributes.findByUsage(VertexAttributes.Usage.Position).numComponents
        val normLength = attributes.findByUsage(VertexAttributes.Usage.Normal).numComponents
        val uvLength = attributes.findByUsage(VertexAttributes.Usage.TextureCoordinates).numComponents
        val colorLength = attributes.findByUsage(VertexAttributes.Usage.ColorUnpacked).numComponents


        ifs = IFSMesh(attributes)

        ifs.observeVerts({ v ->
            println("Vertex[${v.element.index}] EVENT: ${v.type}")
        })

        ifs.observeEdges({ e ->
            println("Edge[${e.element.index}] EVENT: ${e.type}")
        })

        ifs.observeFaces({ f ->
            println("Face[${f.element.index}] EVENT: ${f.type}")
        })

        vert = ifs.createVertex(0, org.yunghegel.gdx.meshgen.data.VertexInfo(1f, 1f, 1f))
        vert2 = ifs.createVertex(1, org.yunghegel.gdx.meshgen.data.VertexInfo(2f, 2f, 2f))
        vert3 = ifs.createVertex(2, org.yunghegel.gdx.meshgen.data.VertexInfo(3f, 3f, 3f))

        edge1 = ifs.createEdge(vert.index, vert2.index)
        edge2 = ifs.createEdge(vert2.index, vert3.index)
        edge3 = ifs.createEdge(vert3.index, vert.index)

        face = ifs.createFace(0, vert.index, vert2.index, vert3.index)

        ifs.initialConstructionComplete = true





    }

    @Test
    fun element_flag_bitmask_operations(){

        vert.setFlag(ElementFlags.CULLED,true)
        vert.setFlag(ElementFlags.DUPLICATED,true)

        assert(vert.getFlag(ElementFlags.CULLED))
        assert(vert.getFlag(ElementFlags.DUPLICATED))

        vert.setFlag(ElementFlags.CULLED,false)

        assert(!vert.getFlag(ElementFlags.CULLED))
    }

    @Test
    fun position_attribute_set(){
        vert.position = Vector3(1f,2f,3f)
        assert(vert.position.x == 1f)
        assert(vert.position.y == 2f)
        assert(vert.position.z == 3f)
    }

    @Test
    fun normal_attribute_set(){
        vert.normal = Vector3(1f,2f,3f)
        assert(vert.normal!!.x == 1f)
        assert(vert.normal!!.y == 2f)
        assert(vert.normal!!.z == 3f)
    }

    @Test
    fun texCoord_attribute_set(){
        vert.uv = Vector2(1f,2f)
        assert(vert.uv!!.x == 1f)
        assert(vert.uv!!.y == 2f)
    }

    @Test
    fun color_attribute_set(){
        vert.color = Vector4(1f,2f,3f,1f)
        assert(vert.color.x == 1f)
        assert(vert.color.y == 2f)
        assert(vert.color.z == 3f)
        assert(vert.color.w == 1f)
    }

    @Test
    fun `change vertex emit event`() {
        headlessTest {
            ifs.observeVerts { v ->
                println("Vertex[${v.element.index}] EVENT: ${v.type}")
            }
            ifs.createVertex(0, org.yunghegel.gdx.meshgen.data.VertexInfo(0f, 1f, 2f))
            ifs.initialConstructionComplete = true


            vert.position = Vector3(1f,2f,3f)
            ifs.emitVertxChanged(ElementChangeEvent( ChangeType.MODIFICATION,vert,ifs.vertices,vert.index))
            vert.normal = Vector3(1f,2f,3f)
            vert.uv = Vector2(1f,2f)

        }

    }
}
class UnitTests {





}
