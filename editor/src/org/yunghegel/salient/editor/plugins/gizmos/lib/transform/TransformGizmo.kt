package org.yunghegel.salient.editor.plugins.gizmos.lib.transform

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import org.yunghegel.salient.editor.app.configs.Settings.Companion.i
import org.yunghegel.salient.editor.input.delegateInput
import org.yunghegel.salient.editor.input.pauseViewportControls
import org.yunghegel.salient.editor.input.resumeViewportControls
import org.yunghegel.salient.editor.input.undelegateInput
import org.yunghegel.salient.editor.plugins.gizmos.lib.Gizmo
import org.yunghegel.salient.editor.plugins.gizmos.lib.GizmoHandle
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.engine.events.scene.onSingleGameObjectSelected
import org.yunghegel.salient.engine.graphics.Transformable
import org.yunghegel.salient.engine.helpers.Pools
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.inject

abstract class TransformGizmo<T:Transformable, H:GizmoHandle<T>>(system: GizmoSystem, name: String,key:Int) : Gizmo<T,H>(name,system,key) {

    


    protected enum class TransformAxis(val id :Int) {
        X(1), Y(2), Z(3), XY(4), XZ(5), YZ(6), XYZ(7), NONE(-1)
    }



    init {
        onSingleGameObjectSelected {  go ->
            if (target is GameObject?) {
                target = go as T
            }
        }
    }



    var initTransform = false

    val cam : PerspectiveCamera = inject()

    protected var state : TransformAxis = TransformAxis.NONE

    override val blocking: Boolean
        get() {
            return state != TransformAxis.NONE
        }

    fun renderHandles(batch: ModelBatch) {
        for (handle in handles) {
            handle.render(batch)
        }
    }

    override fun activate() {
        system.activeGizmo = this as Gizmo<GameObject, *>
        delegateInput(listener = this,)
        super.activate()
    }

    override fun deactivate() {
        system.activeGizmo = null
        undelegateInput(listener = this)

        super.deactivate()
    }

    override fun handleStateChange(state: GizmoState) {
        debug("state changed: $state")
        when (state) {
            GizmoState.Active -> {
                pauseViewportControls()
                activeHandle?.let { handle ->
                    when (handle.id) {
                        TransformAxis.X.id -> {
                            handle.setColor(COLOR_X_SELECTED)
                        }
                        TransformAxis.Y.id -> {
                            handle.setColor(COLOR_Y_SELECTED)
                        }
                        TransformAxis.Z.id -> {
                            handle.setColor(COLOR_Z_SELECTED)
                        }
                        TransformAxis.XYZ.id -> {
                            handle.setColor(COLOR_XYZ_SELECTED)
                        }
                    }

                }
            }

            GizmoState.Hovered -> {
                activeHandle?.let { handle ->
                    when (handle.id) {
                        TransformAxis.X.id -> {
                            handle.setColor(COLOR_X_SELECTED)
                        }
                        TransformAxis.Y.id -> {
                            handle.setColor(COLOR_Y_SELECTED)
                        }
                        TransformAxis.Z.id -> {
                            handle.setColor(COLOR_Z_SELECTED)
                        }
                        TransformAxis.XYZ.id -> {
                            handle.setColor(COLOR_XYZ_SELECTED)
                        }
                    }

                }

            }

            GizmoState.Idle -> {
                for (handle in handles) {
                    handle.restoreColor()
                }
                resumeViewportControls()
            }


        }
    }
    @OptIn(ExperimentalStdlibApi::class)
    override fun handleSelected(handle: GizmoHandle<T>) {
        state = TransformAxis.entries.find { it.id == handle.id } ?: TransformAxis.NONE
        handle.setColor(handle.getColor()?.cpy()?.mul(1.5f,1.5f,1.5f,1f))
        initTransform = true

    }

    override fun handleHovered(handle: GizmoHandle<T>) {
        handle.setColor(handle.getColor()?.cpy()?.mul(1.5f,1.5f,1.5f,1f))
    }

    override fun handleDeselected(handle: GizmoHandle<T>) {
        state = TransformAxis.NONE
        handle.restoreColor()
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        state = TransformAxis.NONE

        return super.touchUp(screenX, screenY, pointer, button)
    }

    open inner class TransformGizmoHandle<T: Transformable>(model: Model, id :Int) : GizmoHandle<T>(model,id) {



        var scl = 0f
        val tmp = Vector3()
        

        override fun update(delta: Float, target: T?) {
            if (target == null) return
            val here : T = target
            here.getPosition(position)
            val dst = position.dst(camera.position)
            val scl = dst * scaleFactor
            tmp.set(scl,scl,scl)
            val scaleamnt : Vector3 = Pools.vector3Pool.obtain().set(scl,scl,scl)
            scale.set(scaleamnt)
            Pools.vector3Pool.free(scaleamnt)
            target.applyTransform()
        }

        override fun render(batch: ModelBatch) {
            batch.render(instance)
        }

        override fun renderPick(batch: ModelBatch) {
            batch.render(instance)
        }
    }
}