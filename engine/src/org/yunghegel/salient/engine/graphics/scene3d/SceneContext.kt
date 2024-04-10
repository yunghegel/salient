package org.yunghegel.salient.engine.scene3d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import dev.lyze.gdxtinyvg.utils.WhitePixelUtils
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.graphics.DebugDrawer
import org.yunghegel.salient.engine.helpers.DepthBatch
import org.yunghegel.salient.engine.helpers.Grid
import org.yunghegel.salient.engine.sys.inject
import org.yunghegel.salient.engine.sys.singleton
import space.earlygrey.shapedrawer.ShapeDrawer



class SceneContext(private var scene:EditorScene) : Environment(), Disposable {

    val modelBatch: ModelBatch
    val depthBatch : DepthBatch
    val perspectiveCamera: PerspectiveCamera
    val orthographicCamera: OrthographicCamera
    val viewport: ScreenViewport

    val grid : Grid by lazy { Grid() }

    init {
        modelBatch = ModelBatch()
        orthographicCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        perspectiveCamera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        perspectiveCamera.near = 0.1f
        perspectiveCamera.far = 300f
        perspectiveCamera.position.set(2.5f, 2.5f, 2.5f)
        perspectiveCamera.lookAt(0f, 0f, 0f)
        perspectiveCamera.update()

        viewport = ScreenViewport(perspectiveCamera)

        depthBatch = DepthBatch(DepthShaderProvider())

        supplyDependencies()


    }

    fun supplyDependencies() {

        singleton(modelBatch)
        singleton(depthBatch)
        singleton(perspectiveCamera)
        singleton(orthographicCamera)
        singleton(viewport)
        singleton<Viewport>(viewport)
    }

    fun conf (action : RenderContext.()->Unit) {
        Companion.action()
    }

    fun set(scene:EditorScene) {
        this.scene = scene
        deriveContext(scene)
    }

    fun deriveContext(scene:EditorScene) {
        perspectiveCamera.position.set(1f, 10f, 1f)
        perspectiveCamera.lookAt(0f, 0f, 0f)
        perspectiveCamera.near = 0.5f
        perspectiveCamera.far = 300f
        perspectiveCamera.update()


        orthographicCamera.position.set(0f, 0f, 0f)
        orthographicCamera.lookAt(0f, 0f, 0f)
        orthographicCamera.near = 0.1f
        orthographicCamera.far = 300f
        orthographicCamera.update()

//        viewport.camera = perspectiveCamera




    }

    fun resize (width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    companion object : RenderContext(DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN)) {



    }

    override fun dispose() {
        modelBatch.dispose()
    }


}