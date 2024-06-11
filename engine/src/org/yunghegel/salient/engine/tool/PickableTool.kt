package org.yunghegel.salient.engine.tool

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.salient.engine.system.inject

abstract class PickableTool(name: String, val picker : Picker) : InputTool(name) {

    private val handles = GdxArray<ToolHandle>()

    var currentHandle : ToolHandle? = null

    fun addHandle(handle: ToolHandle) {
        handles.add(handle)
    }

    override fun render(modelBatch: ModelBatch, env: Environment?) {
        if (active) {
                if (env != null) modelBatch.render(handles, env)
                else modelBatch.render(handles)
        }
    }

    override fun activate() {
        super.activate()
        currentHandle = null
    }

    override fun deactivate() {
        super.deactivate()
        currentHandle = null
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (active) {
            currentHandle = picker.pick(inject(),inject(),camera, screenX, screenY, handles.toList()) as ToolHandle?
            currentHandle?.let { handle -> handlePick(handle) }
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (active || currentHandle != null) {
            currentHandle = null
        }
        return super.touchUp(screenX, screenY, pointer, button)
    }

    abstract fun handlePick(handle: ToolHandle)

    abstract fun handleEndPick(handle: ToolHandle)



}