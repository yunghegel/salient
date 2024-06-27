package org.yunghegel.salient.editor.plugins.gizmos.lib.transform

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import org.yunghegel.salient.editor.app.configs.Settings.Companion.i
import org.yunghegel.salient.editor.input.delegateInput
import org.yunghegel.salient.editor.input.undelegateInput
import org.yunghegel.salient.editor.plugins.gizmos.lib.Gizmo
import org.yunghegel.salient.editor.plugins.gizmos.lib.GizmoHandle
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.engine.events.scene.onSingleGameObjectSelected
import org.yunghegel.salient.engine.graphics.Transformable
import org.yunghegel.salient.engine.helpers.Pools
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.inject

abstract class TransformGizmo<T:Transformable, H:GizmoHandle<T>>(val system: GizmoSystem, name: String) : Gizmo<T,H>(name) {

    


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

    fun renderHandles(batch: ModelBatch) {
        for (handle in handles) {
            handle.render(batch)
        }
    }

    override fun activate() {
        system.activeGizmo = this as Gizmo<GameObject, *>
        delegateInput(listener = this)
        super.activate()
    }

    override fun deactivate() {
        system.activeGizmo = null
        undelegateInput(listener = this)
        super.deactivate()
    }

    override fun handleStateChange(state: GizmoState) {
        when (state) {
            GizmoState.Active -> {
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
            }


        }
    }

    override fun handleSelected(handle: GizmoHandle<T>) {
        state = TransformAxis.entries.find { it.id == handle.id } ?: TransformAxis.NONE
        initTransform = true
        if (handle.id in 1..4) {
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

    override fun handleDeselected(handle: GizmoHandle<T>) {
        state = TransformAxis.NONE
        initTransform = false
        if (handle.id in 1..4) {
            when (handle.id) {
                TransformAxis.X.id -> {
                    handle.setColor(COLOR_X)
                }
                TransformAxis.Y.id -> {
                    handle.setColor(COLOR_Y)
                }
                TransformAxis.Z.id -> {
                    handle.setColor(COLOR_Z)
                }
                TransformAxis.XYZ.id -> {
                    handle.setColor(COLOR_XYZ)
                }
            }
        }
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