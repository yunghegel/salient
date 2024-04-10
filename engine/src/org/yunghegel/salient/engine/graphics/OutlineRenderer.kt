package org.yunghegel.salient.core.graphics.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider
import org.yunghegel.salient.engine.graphics.GenFrameBuffer
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext


class OutlineRenderer {
    private val depthBuffer: FrameBuffer
    private val depthBatch: ModelBatch
    var batch: SpriteBatch = SpriteBatch()
    var buf = GenFrameBuffer(true)
    var modelInstance: RenderableProvider? = null
    var outlineShaderProgram: ShaderProgram
    var modelBatch: ModelBatch = ModelBatch()
    var renderables: Array<RenderableProvider> = Array()
    var texture: Texture? = null
    var textureRegion: TextureRegion? = null

    private var depthTexture: Texture? = null


    init {
        outlineShaderProgram = ShaderProgram(Gdx.files.internal("shaders/object_outline.vert").readString(), Gdx.files.internal("shaders/object_outline.frag").readString())
        if (!outlineShaderProgram.isCompiled) {
            println(outlineShaderProgram.log)
        }
        outlineShaderProgram.bind()
        batch.shader = outlineShaderProgram

        depthBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
        depthBatch = ModelBatch(PBRShaderProvider.createDefaultDepth(144))

    }

    fun add(renderableProvider: RenderableProvider) {
        renderables.add(renderableProvider)
    }

    fun clear() {
        renderables.clear()
    }


    fun captureFBO(context:SceneContext) {
        buf.ensureFboSize(Gdx.graphics.width, Gdx.graphics.height)
        buf.begin(context.viewport)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        context.modelBatch.begin(context.perspectiveCamera)
        context.modelBatch.render(renderables)
        context.modelBatch.end()
        buf.end()
    }

    fun renderFBO() {
        batch.begin()
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.draw(buf.fboTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.end()
    }

    fun render(context: SceneContext) {
        if (renderables.isEmpty) return
        captureFBO(context)
        renderFBO()
    }

    fun captureDepth(context: SceneContext) {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        depthBuffer.begin()
        ScreenUtils.clear(.2f, .2f, .2f, 0f, true)

        context.depthBatch.begin(context.perspectiveCamera)
        context.depthBatch.render(renderables, context)
        context.depthBatch.end()

//        manager.sceneManager.currentScene!!.sceneRenderer.renderDepth()
        depthBuffer.end()
        depthTexture = depthBuffer.colorBufferTexture
    }

}
