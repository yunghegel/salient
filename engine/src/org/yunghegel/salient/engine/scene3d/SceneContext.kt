package org.yunghegel.salient.engine.scene3d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider
import org.yunghegel.debug.DebugContext
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.api.scene.SceneEnvironment
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.graphics.SharedGraphicsResources
import org.yunghegel.salient.engine.helpers.DepthBatch
import org.yunghegel.salient.engine.helpers.WireBatch
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.register
import org.yunghegel.salient.engine.system.singleton


class SceneContext(private var scene:EditorScene) : SceneEnvironment(), Disposable, SceneGraphicsResources, SharedGraphicsResources by GFX {

    override val modelBatch: ModelBatch
    override val depthBatch : DepthBatch
    override val wireBatch : WireBatch
    override val perspectiveCamera: PerspectiveCamera
    override val orthographicCamera: OrthographicCamera
    override val viewport: ScreenViewport
    override val debugContext : DebugContext
    override val shapeCache : ShapeCache

    init {

        modelBatch = ModelBatch(PBRShaderProvider(PBRShaderProvider.createDefaultConfig()), SceneRenderableSorter())
        depthBatch = DepthBatch(PBRDepthShaderProvider(PBRDepthShaderProvider.createDefaultConfig()))
        wireBatch = WireBatch()
        orthographicCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        perspectiveCamera = PerspectiveCamera(  67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        viewport = ScreenViewport(perspectiveCamera)
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

        register {
            bind(SceneGraphicsResources::class) { this }
        }

    }

    fun conf (action : RenderContext.()->Unit) {
        Companion.action()
    }

    fun set(scene:EditorScene) {
        this.scene = scene
        deriveContext(scene)
    }

    fun deriveContext(scene:EditorScene) {

    }

    fun resize (width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    companion object : RenderContext(DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN));

    override fun dispose() {
        modelBatch.dispose()
    }


}