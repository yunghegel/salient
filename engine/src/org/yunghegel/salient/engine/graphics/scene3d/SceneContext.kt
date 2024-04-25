package org.yunghegel.salient.engine.graphics.scene3d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import net.mgsx.gltf.scene3d.shaders.PBRDepthShader
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig
import org.yunghegel.debug.DebugContext

import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.helpers.DepthBatch
import org.yunghegel.salient.engine.helpers.Grid
import org.yunghegel.salient.engine.helpers.WireBatch
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.io.singleton


class SceneContext(private var scene:EditorScene) : Environment(), Disposable {

    val modelBatch: ModelBatch
    val depthBatch : DepthBatch
    val wireBatch : WireBatch
    val perspectiveCamera: PerspectiveCamera
    val orthographicCamera: OrthographicCamera
    val viewport: ScreenViewport
    val debugContext : DebugContext
    val shapeCache : ShapeCache

    val grid : Grid by lazy { inject() }

    init {
        modelBatch = ModelBatch()
        wireBatch = WireBatch()
        orthographicCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        perspectiveCamera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        viewport = ScreenViewport(perspectiveCamera)
        depthBatch = DepthBatch(PBRDepthShaderProvider(PBRDepthShaderProvider.createDefaultConfig()))
        shapeCache = ShapeCache()
        debugContext = DebugContext(inject(), perspectiveCamera, inject(), inject(), modelBatch,shapeCache)

        supplyDependencies()


    }

    fun supplyDependencies() {

        singleton(modelBatch)
        singleton(depthBatch)
        singleton(perspectiveCamera)
        singleton(orthographicCamera)
        singleton(viewport)
        singleton(debugContext)
        singleton(wireBatch)
        singleton(shapeCache)
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
//        perspectiveCamera.position.set(1f, 10f, 1f)
//        perspectiveCamera.lookAt(0f, 0f, 0f)
//        perspectiveCamera.near = 0.5f
//        perspectiveCamera.far = 300f
//        perspectiveCamera.update()
//
//
//        orthographicCamera.position.set(0f, 0f, 0f)
//        orthographicCamera.lookAt(0f, 0f, 0f)
//        orthographicCamera.near = 0.1f
//        orthographicCamera.far = 300f
//        orthographicCamera.update()

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