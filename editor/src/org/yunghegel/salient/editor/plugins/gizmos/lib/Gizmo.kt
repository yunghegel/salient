package org.yunghegel.salient.editor.plugins.gizmos.lib

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector3
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.editor.plugins.picking.PickablesBag
import org.yunghegel.salient.engine.api.tool.MouseTool
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.tool.ToolHandle

/**
 * A tool that allows the user to manipulate objects in the scene.
 * Unique in that it has a 3D visual representation and interactivity via [GizmoHandle] objects.
 * @param T The type of object that this gizmo can manipulate - supplied to the [GizmoHandle] objects to create
 * visual representations of the gizmo's functionality and enable interaction.
 */

abstract class Gizmo<T, H : GizmoHandle<T>>(name: String, val system: GizmoSystem, key: Int) : MouseTool(name, key),
    Component {

    private var tmp : InputProcessor? = null

    enum class GizmoState {
        Active, Hovered, Idle
    }

    private val tmpv = Vector3()



    val handles: MutableList<H> = mutableListOf()
    private val bag = PickablesBag(handles.toList())


    var activeHandle : GizmoHandle<*>? = null
        set (value) {
            if (field == value) return
            if (field != null) {
                handleDeselected(field!! as GizmoHandle<T>)
            }
            field = value
            if (field != null) {
                handleSelected(field!! as GizmoHandle<T>)
            }
            debug("active handle: $field")
        }

    var gizmoState = GizmoState.Idle
        set(value) {
            if (field == value) return
            field = value
            handleStateChange(value)
        }

    var toggleKey = -1
    var target : T? = null

    val picker : Picker by lazy { inject() }

    override fun activate() {


        bag.picked = { pickable ->
            if (pickable is ToolHandle) {
                debug("picked handle: ${pickable.id}")
            }
        }
        entity.add(bag)


        super.activate()
    }



    override fun deactivate() {

        super.deactivate()
    }

    fun set (handle : GizmoHandle<T>?) {
        activeHandle = handle
    }

    override fun keyDown(keycode: Int): Boolean {
        if (toggleKey == -1) return true
        if (keycode == toggleKey) {
            if (active) {
                deactivate()
            } else {
                activate()
            }
        }
        return false
    }

    abstract fun handleStateChange(state: GizmoState)

    open fun handleHovered(handle: GizmoHandle<T>) {debug("hovered handle: ${handle.id}")}

    abstract fun handleSelected(handle: GizmoHandle<T>)

    abstract fun handleDeselected(handle: GizmoHandle<T>)

    abstract fun update(delta: Float, target: T)

    abstract fun render(delta: Float, batch: ModelBatch)

    fun pickHandles(screenX: Int, screenY: Int, hover : Boolean = false) : GizmoHandle<T>? {
        var handle : GizmoHandle<T>? = null
        with (sceneContext) {


                system.picker.queryFor(screenX.toFloat(), screenY.toFloat(), handles, buffersize = 5) { picked ->
                    val picked = handles.find { it.id == picked }
                    if (picked != null) {
                        handle = handles.find { it.id == picked.id }
                        if (handle != null) {
                            gizmoState = if (hover) GizmoState.Hovered else GizmoState.Active
                            debug("picked handle: ${handle!!.id}")
                        }
                }
                    }


//                pipeline.once(State.COLOR_PASS) {
//                    val picked = picker.pick(viewport, PickingSystem.batch, camera, screenX, screenY, handles)
//                    if (picked != null) {
//                        handle = handles.find { it.id == picked.id }
//                        if (handle != null) {
//                            gizmoState = if (hover) GizmoState.Hovered else GizmoState.Active
//                            println("picked handle: ${handle!!.id}")
////                        }
//                    }
//                }



        }
        return handle
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!active) return false
//        else Input.pauseExcept(this)
        if (target != null && button == 0) {
            activeHandle = pickHandles(screenX, screenY)
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (activeHandle != null) {
            activeHandle = null
        }
        if (gizmoState != GizmoState.Idle) {
            gizmoState = GizmoState.Idle
        }
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (!active) return false
        val handle = pickHandles(screenX, screenY, true)
        if (gizmoState == GizmoState.Idle && handle == null) {
            activeHandle = null
        }
        if (handle != null) {
            handleHovered(handle)
        }
        return true
    }





    companion object {
        var COLOR_X: Color = Color(.8f, 0f, 0f, 1f)
        var COLOR_Y: Color = Color(0f, .8f, 0f, 1f)
        var COLOR_Z: Color = Color.valueOf("#0074F7")
        var COLOR_XYZ: Color = Color(0f, .8f, .8f, 1f)

        var COLOR_X_SELECTED: Color = Color.RED
        var COLOR_Y_SELECTED: Color = Color.GREEN
        var COLOR_Z_SELECTED: Color = Color.BLUE
        var COLOR_XYZ_SELECTED: Color = Color.CYAN


        const val X_HANDLE_ID: Int = 1

        const val Y_HANDLE_ID: Int = 2

        const val Z_HANDLE_ID: Int = 3

        const val XYZ_HANDLE_ID: Int = 4
    }

}