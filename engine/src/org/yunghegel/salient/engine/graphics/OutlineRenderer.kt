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
import org.yunghegel.salient.core.scene.EditorSceneGraph
import org.yunghegel.salient.core.scene.GameObject
import org.yunghegel.salient.core.scene.graph.components.RenderableComponent
import org.yunghegel.salient.core.util.GenFrameBuffer
import org.yunghegel.salient.modules.events.Events.subscribe
import org.yunghegel.salient.modules.project.Projects.manager
import org.yunghegel.salient.modules.scene.common.events.GameObjectSelectedEvent
import org.yunghegel.salient.modules.ui.UI.gui

class OutlineRenderer : GameObjectSelectedEvent.Listener {
    private val depthBuffer: FrameBuffer
    private val depthBatch: ModelBatch
    var batch: SpriteBatch = SpriteBatch()
    var buf: GenFrameBuffer = GenFrameBuffer(true)
    var modelInstance: RenderableProvider? = null
    var outlineShaderProgram: ShaderProgram =
        ShaderProgram(
            Gdx.files.internal("shaders/object_outline.vert").readString(),
            Gdx.files.internal("shaders/object_outline.frag").readString()
        )
    var modelBatch: ModelBatch = ModelBatch()
    var modelInstances: Array<ModelInstance> = Array()
    var texture: Texture? = null
    var textureRegion: TextureRegion? = null
    var outlineDepth: OutlineDepth
    var gameObject: GameObject? = null
    private var depthTexture: Texture? = null


    init {
        if (!outlineShaderProgram.isCompiled) {
            println(outlineShaderProgram.log)
        }
        outlineShaderProgram.bind()
        batch.shader = outlineShaderProgram

        outlineDepth = OutlineDepth(true)
        depthBuffer = FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.width, Gdx.graphics.height, true)
        depthBatch = ModelBatch(PBRShaderProvider.createDefaultDepth(144))
        subscribe(this)
    }

    fun setModelInstance(modelInstance: ModelInstance) {
        modelInstances.clear()
        modelInstances.add(modelInstance)
        //        Editor.i().sceneContext.sceneManager.getRenderableProviders().add(modelInstance);
    }

    fun setGameObject(gameObject: GameObject) {
        this.gameObject = gameObject
        for (c in gameObject.components) {
            if (c is RenderableComponent) {
                modelInstance = c.obj
            }
        }
    }

    fun deselectGameObject() {
        gameObject = null
        modelInstances.clear()
    }

    fun captureFBO(sceneGraph: EditorSceneGraph) {
        buf.ensureFboSize(Gdx.graphics.width, Gdx.graphics.height)
        buf.begin(gui!!.viewportWidget.viewport)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        modelBatch.begin(sceneGraph.currScene.perspectiveCamera)
        modelBatch.render(modelInstance)
        modelBatch.end()
        buf.end()
        //        texture = buf.getColorBufferTexture();
//        textureRegion = new TextureRegion(texture);
//        textureRegion.flip(false, true);
    }

    fun renderFBO(sceneGraph: EditorSceneGraph?) {
        batch.begin()
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.draw(buf.fboTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.end()
    }

    fun render(sceneGraph: EditorSceneGraph) {
        if (modelInstances.isEmpty) return
        captureFBO(sceneGraph)
        renderFBO(sceneGraph)
    }

    fun captureDepth(sceneGraph: EditorSceneGraph) {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        depthBuffer.begin()
        ScreenUtils.clear(.2f, .2f, .2f, 0f, true)
        depthBatch.begin(sceneGraph.currScene.perspectiveCamera)
        depthBatch.render(modelInstance, sceneGraph.currScene.sceneContext.env)
        depthBatch.end()

        manager.sceneManager.currentScene!!.sceneRenderer.renderDepth()
        depthBuffer.end()
        depthTexture = depthBuffer.colorBufferTexture
    }

    fun renderDepthOutline(sceneGraph: EditorSceneGraph) {
        captureDepth(sceneGraph)
        outlineDepth.render(batch, depthTexture, sceneGraph.currScene.perspectiveCamera)
    }


    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
        setGameObject(event.gameObject)
    }
}
