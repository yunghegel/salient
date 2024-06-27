package org.yunghegel.salient.editor.plugins.gizmos.tools



import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.salient.editor.app.scene
import org.yunghegel.salient.editor.input.delegateInput
import org.yunghegel.salient.editor.input.undelegateInput
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.editor.plugins.picking.PickablesBag
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.editor.plugins.picking.tools.PickingTool
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.tool.Click.x
import org.yunghegel.salient.engine.graphics.TransformState
import org.yunghegel.salient.engine.system.Netgraph
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.tool.PickableTool
import org.yunghegel.salient.engine.tool.ToolHandle
import org.yunghegel.salient.engine.ui.UI

class TranslateTool(val system : GizmoSystem) : PickableTool("translate_tool", inject()) {

    var transformState = TransformState.None
        set(value) {
            field = value
            info("Transform state: $value")
        }

    override var hoveredHandle: ToolHandle? = null
        get() = super.hoveredHandle
        set(value) {
            if (value == null) {
                field?.restoreColor()
            } else {
                value.setColor(value.getColor().cpy().mul(1.5f))
            }
            field = value
        }

    val scene : Scene = inject()


    val xHandle : ToolHandle
    val yHandle : ToolHandle
    val zHandle : ToolHandle
    val xyzHandle : ToolHandle


    override fun update(deltaTime: Float) {
        val selection = scene.graph.selection.lastSelected
        if (selection == null) return
        for (handle in handles) {
            handle.update(selection.combined, scene.context.perspectiveCamera)
        }
    }





    init {

        Netgraph.add("Mode:") {
            if (transformState != TransformState.None) transformState.name else "None"
        }

        val modelBuilder = ModelBuilder()

        val xHandleModel = modelBuilder.createArrow(
            0f,
            0f,
            0f,
            .5f,
            0f,
            0f,
            ARROW_CAP_SIZE,
            ARROW_THICKNESS,
            ARROW_DIVISIONS,
            GL20.GL_TRIANGLES,
            Material(ColorAttribute.createDiffuse(TransformState.X.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong()
        )
        val yHandleModel = modelBuilder.createArrow(
            0f,
            0f,
            0f,
            0f,
            .5f,
            0f,
            ARROW_CAP_SIZE,
            ARROW_THICKNESS,
            ARROW_DIVISIONS,
            GL20.GL_TRIANGLES,
            Material(ColorAttribute.createDiffuse(TransformState.Y.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong()
        )
        val zHandleModel = modelBuilder.createArrow(
            0f,
            0f,
            0f,
            0f,
            0f,
            .5f,
            ARROW_CAP_SIZE,
            ARROW_THICKNESS,
            ARROW_DIVISIONS,
            GL20.GL_TRIANGLES,
            Material(ColorAttribute.createDiffuse(TransformState.Z.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong()
        )
        val xyzHandleModel = modelBuilder.createSphere(
            .25f / 3,
            .25f / 3,
            .25f / 3,
            20,
            20,
            Material(ColorAttribute.createDiffuse(TransformState.XYZ.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong()
        )

        xHandle = ToolHandle(TransformState.X.id, xHandleModel)
        yHandle = ToolHandle(TransformState.Y.id, yHandleModel)
        zHandle = ToolHandle(TransformState.Z.id, zHandleModel)
        xyzHandle = ToolHandle(TransformState.XYZ.id, xyzHandleModel)

        handles.addAll(xHandle, yHandle, zHandle, xyzHandle)

        handles.forEach { handle ->
            handle.setColor(TransformState.fromId(handle.id).color)
        }

        renderMask.set(RenderUsage.MODEL_BATCH,true)

        val bag = PickablesBag(handles.toList())
        bag.picked = { pickable ->
            if (pickable is ToolHandle) {
                handlePick(pickable)
            }
        }

        entity.add(bag)
    }

    override fun handlePick(handle: ToolHandle) {
        transformState = TransformState.fromId(handle.id)
        handle.setColor(transformState.color)
    }

    override fun handleEndPick(handle: ToolHandle) {
        transformState = TransformState.None
        handle.restoreColor()
    }

    override fun activate() {
        super.activate()
        delegateInput(listener = this)
    }

    override fun deactivate() {
        undelegateInput(listener = this)
        super.deactivate()
        system.activeGizmo = null
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val wasNull = currentHandle == null
        currentHandle = if (hoveredHandle != null) hoveredHandle else picker.pick(sceneContext.viewport, PickingSystem.batch, camera, screenX, screenY, handles.toList()) as ToolHandle?
        if (currentHandle != null && wasNull) {
            handlePick(currentHandle!!)
        }
        return currentHandle != null
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (currentHandle != null) {
            handleEndPick(currentHandle!!)
            currentHandle = null
        }
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val (x,y) = UI.viewportToScreen(screenX,screenY)
        val pick = picker.pick(sceneContext.viewport,PickingSystem.batch,camera, x.toInt(), y.toInt(), handles.toList()) as ToolHandle?
        if (pick != null) {
            hoveredHandle = pick
        } else {
            hoveredHandle = null
        }
        return super.mouseMoved(screenX, screenY)
    }

    companion object {

        const val ARROW_THICKNESS: Float = 0.3f

        const val ARROW_CAP_SIZE: Float = 0.1f

        const val ARROW_DIVISIONS: Int = 12
    }


}