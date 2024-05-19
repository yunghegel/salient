package org.yunghegel.salient.engine.graphics.shapes.utility

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Rectangle
import org.yunghegel.salient.engine.graphics.GridConfig
import org.yunghegel.salient.engine.graphics.GridShader
import org.yunghegel.salient.engine.system.singleton

val vInfo1 = MeshPartBuilder.VertexInfo()
val vInfo2 = MeshPartBuilder.VertexInfo()
val vInfo3 = MeshPartBuilder.VertexInfo()
val vInfo4 = MeshPartBuilder.VertexInfo()

class Grid {
    lateinit var texture: Texture
    lateinit var region: TextureRegion
    val spriteBatch = SpriteBatch()
    val config: GridConfig = GridConfig()
    var fbo: FrameBuffer = FrameBuffer(
        Pixmap.Format.RGBA8888,
        Gdx.graphics.width,
        Gdx.graphics.height,
        true
    )


    private val grid: ModelInstance
    private val batch = ModelBatch(GridShader.GridShaderProvider(config))

    init {
        grid = createGridModel()
    }

    fun supplyConfig() {
        if (config.injected) return
        singleton(config)
        config.injected = true
    }

    fun createGridModel(): ModelInstance {
        val b: ModelBuilder = ModelBuilder()
        b.begin()
        val attr = VertexAttribute.Position().usage.toLong() or VertexAttribute.TexCoords(0).usage.toLong()
        val mbp = b.part("grid", GL20.GL_TRIANGLES, attr, Material())
        vInfo1.setPos(-1f, 0f, -1f)
        vInfo1.setUV(0f, 0f)
        vInfo1.setCol(1f, 0f, 0f, 1f)
        val idx1 = mbp.vertex(vInfo1)
        vInfo2.setPos(1f, 0f, -1f)
        vInfo2.setUV(1f, 0f)
        vInfo2.setCol(0f, 1f, 0f, 1f)
        val idx2 = mbp.vertex(vInfo2)
        vInfo3.setPos(1f, 0f, 1f)
        vInfo3.setUV(1f, 1f)
        vInfo3.setCol(0f, 0f, 1f, 1f)
        val idx3 = mbp.vertex(vInfo3)
        vInfo4.setPos(-1f, 0f, 1f)
        vInfo4.setUV(0f, 1f)
        vInfo4.setCol(1f, 1f, 0f, 1f)
        val idx4 = mbp.vertex(vInfo4)

        mbp.triangle(idx1, idx2, idx3)
        mbp.triangle(idx3, idx4, idx1)

        return ModelInstance(b.end())
    }

    fun render(camera: Camera) {
        if(!config.enabled) return
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
        batch.begin(camera)
        batch.render(grid)
        batch.end()
    }

    val fullscreenBounds = Rectangle(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
}