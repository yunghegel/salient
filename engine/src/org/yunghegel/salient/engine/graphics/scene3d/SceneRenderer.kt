package org.yunghegel.salient.engine.scene3d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.yunghegel.salient.engine.RendererRoutine
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.api.scene.EditorSceneGraph
import org.yunghegel.salient.engine.api.scene.EditorSceneRenderer
import org.yunghegel.salient.engine.helpers.DepthBatch
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.sys.inject

class SceneRenderer<E:EditorScene,G:EditorSceneGraph>(val scene : E) : EditorSceneRenderer<E,G> {

    val camera : PerspectiveCamera = inject()
    val batch : ModelBatch = inject()
    val depthBatch : DepthBatch = inject()

    override fun renderGraph(scene: E, graph:G, context: SceneContext) {
        renderDepth(scene, graph.root, context)
        renderColor(scene, graph.root, context)
    }

    fun renderDepth(scene: E,context : SceneContext) {
        renderDepth(scene, scene.sceneGraph.root, context)
    }

    fun renderColor(scene: E,context : SceneContext) {
        renderColor(scene, scene.sceneGraph.root, context)
    }

    private fun renderDepth(scene: E, go: GameObject, sceneContext: SceneContext) {
        depthBatch.begin(camera)
        renderGameObject(scene, go, sceneContext,depthBatch)
        depthBatch.end()
    }

    fun renderColor(scene: E, go: GameObject, sceneContext: SceneContext) {
        batch.begin(camera)
        renderGameObject(scene, go, sceneContext,batch)
        batch.end()
    }

    override fun updateGraph(scene: E, go: GameObject, context: SceneContext) {
        go.components.forEach { cmp ->
           if (cmp.updater) {
               cmp.update(scene, go, context)
           }
        }
        go.children.forEach { updateGraph(scene, it, context) }
    }

    private fun renderGameObject(scene: E, go: GameObject, sceneContext: SceneContext,modelBatch: ModelBatch) {
        go.components.forEach { cmp ->
            if (cmp.renderer) {
                cmp.render(batch, camera, sceneContext)
            }
        }
        go.children.forEach { renderGameObject(scene, it, sceneContext,modelBatch) }
    }


    override fun renderContext(context: SceneContext) {
            context.grid.render(camera)
    }

    override fun prepareContext(context: SceneContext) {
       context.conf {
           setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
           setDepthTest(GL20.GL_LEQUAL)
           setDepthMask(true)
           setCullFace(GL20.GL_BACK)
       }
    }

}