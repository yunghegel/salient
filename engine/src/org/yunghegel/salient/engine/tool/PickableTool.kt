package org.yunghegel.salient.engine.tool

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.ext.draw
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.gdx.utils.selection.PickerShader
import org.yunghegel.salient.engine.api.tool.InputTool
import org.yunghegel.salient.engine.events.scene.onSingleGameObjectSelected
import org.yunghegel.salient.engine.scene3d.component.PickableComponent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.widgets.notif.notify

abstract class PickableTool(name: String, val picker : Picker) : InputTool(name) {

   val handles = GdxArray<ToolHandle>()

    var currentHandle : ToolHandle? = null
        set (value) {
            if (value == null) {
                if (field != null) handleEndPick(field!!)
                field = null
                return
            }
            if (field == value) return
            field = value
            value.let { handle ->
                println("Picked handle [${handle.id}]")
                handlePick(handle)
            }
        }

    open var hoveredHandle : ToolHandle? = null

    init {

        onSingleGameObjectSelected { go ->

        }
    }




    override fun render(modelBatch: ModelBatch, environment: Environment?) {
        if (active) {
                if (environment != null) modelBatch.render(handles, environment)
                else modelBatch.render(handles)
        }
        with(sceneContext) {
            picker.texture?.draw(spriteBatch)
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


    abstract fun handlePick(handle: ToolHandle)

    abstract fun handleEndPick(handle: ToolHandle)




}