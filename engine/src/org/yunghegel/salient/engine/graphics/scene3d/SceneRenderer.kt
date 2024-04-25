package org.yunghegel.salient.engine.graphics.scene3d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import org.yunghegel.debug.AFTER_DEPTH
import org.yunghegel.debug.DebugContext
import org.yunghegel.debug.Drawable
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.State
import org.yunghegel.salient.engine.api.Resizable
import org.yunghegel.salient.engine.api.ecs.DEBUG_ALL
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.api.scene.EditorSceneGraph
import org.yunghegel.salient.engine.api.scene.EditorSceneRenderer
import org.yunghegel.salient.engine.events.lifecycle.onWindowResized
import org.yunghegel.salient.engine.graphics.GenFrameBuffer
import org.yunghegel.salient.engine.helpers.DepthBatch
import org.yunghegel.salient.engine.io.inject

class SceneRenderer<E:EditorScene,G:EditorSceneGraph>(val scene : E) : EditorSceneRenderer<E,G>, Resizable {

    val camera : PerspectiveCamera = inject()
    val batch : ModelBatch = inject()

    val depthBatch : DepthBatch = inject()
    val debugContext : DebugContext by lazy { inject ()}
    val pipeline: Pipeline = inject()





    var depthFbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height,true)

    init {

    }

    override fun renderGraph(scene: E, graph:G, context: SceneContext) {
        renderDepth(scene, graph.root)
        renderColor(scene, graph.root, context)
        renderDebug(scene, graph.root, context)
    }

    fun renderDepth(scene: E,context : SceneContext) {
        renderDepth(scene, scene.sceneGraph.root)
    }

    fun renderColor(scene: E,context : SceneContext) {
        renderColor(scene, scene.sceneGraph.root, context)
    }

    fun renderDebug(scene: E,context : SceneContext) {
        renderDebug(scene, scene.sceneGraph.root, context)
    }

    fun renderDepth(scene: E, go: GameObject) {
        depthFbo.begin()
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT or GL20.GL_COLOR_BUFFER_BIT)
        depthBatch.begin(camera)
        go.render(Gdx.graphics.deltaTime,depthBatch,scene.context)
        depthBatch.end()
        depthFbo.end()
    }

    private fun renderColor(scene: E, go: GameObject, sceneContext: SceneContext) {
        batch.begin(camera)
        go.render(Gdx.graphics.deltaTime,batch,sceneContext)
        renderGameObject(scene, go, sceneContext, batch)
        batch.end()
    }

    private fun renderDebug(scene: E, go: GameObject, sceneContext: SceneContext) {
        go.components.forEach { cmp ->
            if (cmp is Drawable && (cmp.shouldDraw || go.check(DEBUG_ALL))) {
                if (cmp.has(AFTER_DEPTH)) pipeline.once(State.COLOR_PASS, func = {
                    debugContext.start(cmp.mask)
                    cmp.renderDebug(debugContext)
                    debugContext.end(cmp.mask)
                }) else {
                    pipeline.once(State.BEFORE_COLOR_PASS,func= {
                        debugContext.start(cmp.mask)
                        cmp.renderDebug(debugContext)
                        debugContext.end(cmp.mask)

                    })
                }

            }
        }
        go.children.forEach { renderDebug(scene, it, sceneContext) }
    }

    override fun updateGraph(scene: E, go: GameObject, context: SceneContext) {
        go.components.forEach { cmp ->
           if (cmp.updater) {
               cmp.update(scene, go, context)
           }
        }
        go.children.forEach { updateGraph(scene, it, context) }
    }

    private fun renderGameObject(scene: E, go: GameObject, sceneContext: SceneContext, modelBatch: ModelBatch) {
        go.components.forEach { cmp ->
            if (cmp.renderer) {
                cmp.render(batch, camera, sceneContext)
            }
        }
        go.children.forEach { renderGameObject(scene, it, sceneContext,modelBatch) }
    }


    override fun renderContext(context: SceneContext) {
        camera.update()
            context.grid.render(camera)
    }

    override fun prepareContext(context: SceneContext,depth:Boolean) {
       context.conf {
           setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
           setDepthTest(GL20.GL_LEQUAL)
           setDepthMask(true)
           setCullFace(GL20.GL_BACK)
       }
    }

    override fun resize(width: Int, height: Int) {
    }

}