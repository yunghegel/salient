package src

import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.math.*
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

import org.yunghegel.gdx.meshgen.data.attribute.*
import org.yunghegel.gdx.meshgen.data.ifs.*
import kotlin.test.Test

@RunWith(Enclosed::class)
class UnitTests {

    class VertexTests {


        val pos = floatArrayOf(0f,0f,0f)
        val norm = floatArrayOf(1f,1f,1f)
        val uv = floatArrayOf(0f,1f)
        val color = floatArrayOf(1f,0f,0f)

        val vert: IVertex

        val attributes = VertexAttributes(VertexAttribute.Position(),VertexAttribute.Normal(),VertexAttribute.TexCoords(2),VertexAttribute.ColorUnpacked())
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

            vert = IVertex(0,ifs)

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
    }



}
