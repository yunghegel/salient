package org.yunghegel.salient.editor.plugins.gizmos.tools

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.salient.editor.plugins.gizmos.lib.GizmoHandle
import org.yunghegel.salient.editor.plugins.gizmos.lib.GizmoModels
import org.yunghegel.salient.editor.plugins.gizmos.lib.transform.TransformGizmo
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.inject


class RotateTool( system: GizmoSystem) : TransformGizmo<GameObject, RotateTool.RotateHandle>(system,"rotate_tool") {

    init {
        val x = GizmoModels.torus(Material(ColorAttribute.createDiffuse(COLOR_X)), .5f, 0.01f, 50, 50)
        val y = GizmoModels.torus(Material(ColorAttribute.createDiffuse(COLOR_Y)), .5f, 0.01f, 50, 50)
        val z = GizmoModels.torus(Material(ColorAttribute.createDiffuse(COLOR_Z)), .5f, 0.01f, 50, 50)

        handles.add(RotateHandle(x, TransformAxis.X.id))
        handles.add(RotateHandle(y, TransformAxis.Y.id))
        handles.add(RotateHandle(z, TransformAxis.Z.id))

        renderMask.set(RenderUsage.MODEL_BATCH,true)

    }

    private val rotatePicker = Picker()
    val viewport : ScreenViewport = inject()



    override fun update(delta: Float, target: GameObject?) {
        if (target != null) {
            for (handle in handles) {
                handle.update(delta, target)
            }
        }
    }

    override fun render(delta: Float, batch: ModelBatch) {
        renderHandles(batch)
    }

    override fun render(modelBatch: ModelBatch, environment: Environment?) {
        renderHandles(modelBatch)
    }

    fun pickHandles(x: Int, y: Int) {
        rotatePicker.pick(viewport, PickingSystem.batch,camera,x, y, handles.toList()) { handle ->
            println("picked handle: ${handle.id}")
            activeHandle = handle as RotateHandle
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        pickHandles(screenX, screenY)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return super.touchUp(screenX, screenY, pointer, button)
    }

    inner class RotateHandle(model: Model, id:Int) : GizmoHandle<GameObject>(model,id) {

        val euler = Vector3()

        override fun render(batch: ModelBatch) {
            batch.render(instance)
        }

        override fun renderPick(batch: ModelBatch) {
            batch.render(instance)
        }

        override fun update(delta: Float, target: GameObject?) {
            target?.getPosition(position)
            val dst: Float = camera.position.dst(position)
            val scl = dst * scaleFactor
            scale = Vector3(scl, scl, scl)
            position = position
            when (id) {
                X_HANDLE_ID -> this.euler.y = 90f
                Y_HANDLE_ID -> this.euler.x = 90f
                Z_HANDLE_ID -> this.euler.z = 90f
            }
            rotation.setEulerAngles(euler.x, euler.y, euler.z)
            instance.transform.set(position, rotation, scale)
        }
    }
}