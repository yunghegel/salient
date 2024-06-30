package org.yunghegel.salient.editor.plugins.gizmos.tools

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.gdx.utils.selection.PickerColorEncoder
import org.yunghegel.salient.editor.plugins.gizmos.lib.GizmoHandle
import org.yunghegel.salient.editor.plugins.gizmos.lib.GizmoModels
import org.yunghegel.salient.editor.plugins.gizmos.lib.transform.TransformGizmo
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.inject
import kotlin.math.atan2


class RotateTool( system: GizmoSystem) : TransformGizmo<GameObject, RotateTool.RotateHandle>(system,"rotate_tool",
    Input.Keys.R) {

    private var a = Vector3()
    private var b = Vector3()
    private val localPosition = Vector3()
    private val position = Vector3()
    private val tmp = Matrix4()
    private val tmpQ = Quaternion()
    private val tmpQ2 = Quaternion()

    var degree: Float = 0f
    var rot: Float = 0f
    var lastRot: Float = 0f
    var dst: Double = 0.0


    init {
        val x = GizmoModels.torus(Material(ColorAttribute.createDiffuse(COLOR_X)), .5f, 0.01f, 50, 50)
        val y = GizmoModels.torus(Material(ColorAttribute.createDiffuse(COLOR_Y)), .5f, 0.01f, 50, 50)
        val z = GizmoModels.torus(Material(ColorAttribute.createDiffuse(COLOR_Z)), .5f, 0.01f, 50, 50)



        handles.add(RotateHandle(x, TransformAxis.X.id))
        handles.add(RotateHandle(y, TransformAxis.Y.id))
        handles.add(RotateHandle(z, TransformAxis.Z.id))

        renderMask.set(RenderUsage.MODEL_BATCH,true)

        handles.forEach { h ->
            h.material.set(PickerColorEncoder.encodeRaypickColorId(h.id))
        }

    }

    private val rotatePicker = Picker()
    val viewport : ScreenViewport = inject()



    override fun update(delta: Float, target: GameObject) {

            for (handle in handles) {
                handle.update(delta, target)
            }


        calculateAngle(target)
        if (initTransform) {
            initTransform = false
        }

        rot = degree - lastRot;


            if(state == TransformAxis.X) {
                tmpQ.setEulerAngles(0f,-rot,0f);
                target.rotate(tmpQ);
            }
            else if(state == TransformAxis.Y) {
                tmpQ.setEulerAngles(rot,0f,0f);
                target.rotate(tmpQ);
            }
            else if(state == TransformAxis.Z) {
                tmpQ.setEulerAngles(0f,0f,-rot);
                target.rotate(tmpQ);
            }

            target.getTransform();


            lastRot = degree;



    }

    override fun render(delta: Float, batch: ModelBatch) {
        renderHandles(batch)
    }

    override fun render(modelBatch: ModelBatch, environment: Environment?) {
        renderHandles(modelBatch)
    }
    fun calculateAngle(target: GameObject): Float {
        val ray = viewport.getPickRay(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())

        target.getLocalPosition(position)

        a = camera.project(position)
        b = camera.project(ray.origin)
        degree = Math.toDegrees(atan2((b.x - a.x).toDouble(), (b.y - a.y).toDouble())).toFloat()
        if (degree < 0) {
            degree += 360
        }
        return degree
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