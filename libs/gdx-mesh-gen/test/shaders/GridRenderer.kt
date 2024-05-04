package shaders

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Rectangle

object GridRenderer {

    lateinit var texture : Texture
    lateinit var region : TextureRegion
    val spriteBatch = SpriteBatch()

    var fbo: FrameBuffer = FrameBuffer(
        Pixmap.Format.RGBA8888,
        Gdx.graphics.width,
        Gdx.graphics.height,
        true
    )

    fun ensureFBO(width: Int, height: Int) : FrameBuffer{
        if (MaskRenderer.fbo.width != width || MaskRenderer.fbo.height != height) {
            MaskRenderer.fbo.dispose()
            MaskRenderer.fbo = FrameBuffer(Pixmap.Format.RGBA8888, width, height, true)
        }
        return MaskRenderer.fbo
    }



    val config: GridShader.Config = GridShader.Config()
    private val grid: ModelInstance
    private val batch = ModelBatch(GridShader.GridShaderProvider(config))
    init {
        grid = createGridModel()
    }

    fun createGridModel():ModelInstance {
        val b : ModelBuilder = ModelBuilder()
        b.begin()
        val attr = VertexAttribute.Position().usage.toLong() or VertexAttribute.TexCoords(0).usage.toLong()
        val mbp = b.part("grid", GL20.GL_TRIANGLES,attr, Material())
        vInfo1.setPos(-1f,0f,-1f)
        vInfo1.setUV(0f,0f)
        vInfo1.setCol(1f,0f,0f,1f)
       val idx1= mbp.vertex(vInfo1)
        vInfo2.setPos(1f,0f,-1f)
        vInfo2.setUV(1f,0f)
        vInfo2.setCol(0f,1f,0f,1f)
       val idx2 = mbp.vertex(vInfo2)
        vInfo3.setPos(1f,0f,1f)
        vInfo3.setUV(1f,1f)
        vInfo3.setCol(0f,0f,1f,1f)
       val idx3 =  mbp.vertex(vInfo3)
        vInfo4.setPos(-1f,0f,1f)
        vInfo4.setUV(0f,1f)
        vInfo4.setCol(1f,1f,0f,1f)
       val idx4 = mbp.vertex(vInfo4)

        mbp.triangle(idx1,idx2,idx3)
        mbp.triangle(idx3,idx4,idx1)

        return ModelInstance(b.end())
    }

    fun render(camera: Camera,clearDepth: Boolean = true) {
//        fbo = ensureFBO(Gdx.graphics.width, Gdx.graphics.height)
//        fbo.begin()
//        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        if(clearDepth) Gdx.gl.glClear( GL20.GL_DEPTH_BUFFER_BIT)
        batch.begin(camera)
        batch.render(grid)
        batch.end()
//        fbo.end()

//        texture = fbo.colorBufferTexture
//        region = TextureRegion(texture)
//        region.flip(false,true)

//        spriteBatch.disableBlending()
//        spriteBatch.projectionMatrix.setToOrtho2D(0f,0f,Gdx.graphics.width.toFloat(),Gdx.graphics.height.toFloat() )
//        spriteBatch.begin()
//        spriteBatch.draw(
//           fbo.getColorBufferTexture(),
//            bounds.x,
//            Gdx.graphics.height - bounds.y,
//            bounds.width,
//            bounds.height,
//            0f,
//            0f,
//            1f,
//            1f
//        )
//
//        spriteBatch.end()

    }





}