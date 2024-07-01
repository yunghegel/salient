package org.yunghegel.salient.editor.plugins.gizmos.tools

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import ktx.math.minus
import ktx.math.plus
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
import net.mgsx.gltf.scene3d.utils.MaterialConverter
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.app.Salient.Companion.push
import org.yunghegel.salient.editor.app.salient
import org.yunghegel.salient.editor.app.scene
import org.yunghegel.salient.editor.input.delegateInput
import org.yunghegel.salient.editor.input.undelegateInput
import org.yunghegel.salient.editor.plugins.intersect.lib.IntersectionQuery
import org.yunghegel.salient.editor.plugins.intersect.tools.IntersectorTool
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.State
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.graphics.shapes.primitives.Cylinder
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.scene3d.events.GameObjectAddedEvent
import org.yunghegel.salient.engine.system.Netgraph
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.api.tool.InputTool
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.widgets.notif.notify
import kotlin.math.max

class PlacementTool : InputTool("placement_tool") {

    var ghostMat = Material()
    val context : SceneContext by lazy { inject() }
    var instance : ModelInstance ? = null
    val pipeline : Pipeline = inject()

    private val updateRoutine : Entity

    init {
        ghostMat.set(PBRColorAttribute.createBaseColorFactor(Color.WHITE.cpy().alpha(0.5f)))
        ghostMat.set(BlendingAttribute(true,0.5f))
        ghostMat.set(IntAttribute.createCullFace(GL30.GL_NONE))


        updateRoutine = pipeline.createRoutine(State.COLOR_PASS,"placement_tool", { !active }) { delta ->
            val fbo = pipeline.buffers["placement_tool"]!!
            with(context) {
                    update(delta)
                    updatePosition()
                    (UI.root as Gui).updateviewport()
                    debugDrawer.renderer.color = Color.WHITE.cpy().alpha(0.7f)
                    debugDrawer.begin()
                    debugDrawer.drawWireDisc(intersection?.intersection ?: Vector3.Zero, Vector3.Y, state.radius)
                    debugDrawer.end()
                    batch.begin(camera)
                    glEnable(GL30.GL_BLEND)
                    glBlendMode(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA)
                    instance?.let {
                        batch.render(it,context)
                    }
                    batch.end()
            }

        }



    }

    private var state = object {
        var tmpMats = mutableListOf<Material>()
        var ghostInstance : ModelInstance? = null
        var mat4 = Matrix4()
        val center =Vector3()
        var asset : ModelAsset? = null
        var radius = 1f
        var objectHeight = 1f
        var boundingBox = BoundingBox()
        var pos = Vector3()
        var cylinder: Model = Cylinder(radius, objectHeight, radius, 16,Color.WHITE.cpy()).createModel()!!
        var highlighter : ModelInstance = ModelInstance(cylinder)
        var ghostModel : Model? = null
            set(value) {
                field = value
                field?.let { mdl ->
                    tmpMats.clear()
                    mdl.materials.forEach { tmpMats.add(it.copy()) }
                    mdl.materials.clear()
                    mdl.materials.add(ghostMat)
                    ghostInstance = ModelInstance(mdl)
                    mdl.calculateBoundingBox(boundingBox)
//                    mdl.meshes.forEach { mesh->
//
//                        mesh.calculateBoundingBox().apply {
//                            getCenter(center)
//                            objectHeight = max(height, max.x - min.x)
//                        }
//                        var rad = mesh.calculateRadius(center)
//                        if (rad > radius) {
//                            radius = rad
//                        }
//                    }
//                    cylinder = Cylinder(radius, objectHeight, 0.1f, 16).createModel()!!
//                    cylinder.materials.forEach { mat ->
//                        mat.set(PBRColorAttribute.createBaseColorFactor(Color.WHITE.cpy().alpha(0.5f)))
//                        mat.set(BlendingAttribute(true,0.5f))
//                        mat.set(IntAttribute.createCullFace(GL30.GL_FRONT_FACE))
//                        MaterialConverter.makeCompatible(mat)
//                    }
                    highlighter = ModelInstance(cylinder)

//                    mdl.materials.each { mat ->
//                        MaterialConverter.makeCompatible(mat)
//                    }
                }

            }


        fun reset () {
            ghostModel?.materials?.clear()
            ghostModel?.materials?.addAll(*tmpMats.toTypedArray())
            ghostInstance = null
            ghostModel = null
            asset = null
            tmpMats.clear()
        }

    }

    val intersector : IntersectorTool by lazy { inject() }

    var intersection : IntersectorTool.IntersectionResult? = null



    fun start(asset : ModelAsset) {
        val model = asset.value!!
        state.asset = asset
        state.ghostModel = model
        instance = ModelInstance(model)
        instance!!.materials.forEach { mat ->
            mat.set(PBRColorAttribute.createBaseColorFactor(Color.WHITE.cpy().alpha(0.5f)))
            mat.set(BlendingAttribute(true,0.8f))
            mat.set(IntAttribute.createCullFace(GL30.GL_NONE))
        }
        pipeline.push(updateRoutine)
        activate()
    }

    fun stop() {
        state.reset()
        pipeline.pull(updateRoutine)
        deactivate()
    }

    override fun activate() {
        delegateInput(listener = this)
        super.activate()
    }

    override fun deactivate() {
        undelegateInput(listener = this)

        super.deactivate()
    }

    override fun update(deltaTime: Float) {
        if (active) {

            intersection = intersector.query(IntersectionQuery.PLANE_XZ) { result ->
                intersection = result
                val unadjusted = Vector3(result.intersection)
                val center = Vector3()
                state.boundingBox.getCenter(center)
                val pos = Vector3(unadjusted - center)
                state.pos = pos
                state.mat4.idt().setTranslation(pos)





            }

        }
    }

    fun placeNewGameObject() {
        val transform = state.mat4.cpy()
        val scene : Scene = inject()
        val go = GameObject("New Game Object",scene = scene)

        val modelC= ModelComponent(state.asset!!,go)
        state.asset!!.useAsset(state.asset!!.value!!,go)
        go.add(modelC)
        notify("New GameObject created")

        scene.graph.addGameObject(go,scene.graph.root)


        val adjustment = Vector3()

        (go.get(RenderableComponent::class)?.value as ModelInstance).transform.set(state.mat4.idt().setTranslation(state.pos    ))

    }



    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.RIGHT) {
            if (active) {
                stop()
            }
        }

        if (button == Input.Buttons.LEFT) {
            if (active) {
                placeNewGameObject()
                stop()
            }
        }

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (active && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            stop()
        } else {
            if (active) {
                placeNewGameObject()
                stop()
            }
        }
        return super.touchUp(screenX, screenY, pointer, button)
    }

    fun updatePosition() {
        if (instance != null) {
            instance!!.transform.set(state.mat4)
        }
    }

    companion object {
        private val batch : ModelBatch = ModelBatch()
    }

}