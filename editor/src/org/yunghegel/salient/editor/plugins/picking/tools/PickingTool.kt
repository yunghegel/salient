package org.yunghegel.salient.editor.plugins.picking.tools

import org.yunghegel.salient.editor.input.delegateInput
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.api.tool.InputTool
import org.yunghegel.salient.engine.api.tool.Tool

class PickingTool( val pickingSystem : PickingSystem) : InputTool("picking_tool") {



    override val blocking: Boolean = false
    override fun activate() {
        delegateInput(listener = this )
        super.activate()
    }

    override fun deactivate() {
        delegateInput(listener = null)
        super.deactivate()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        pickingSystem.pick(screenX.toFloat(),screenY.toFloat(),button,key,isDoubleClick)
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return super.touchUp(screenX, screenY, pointer, button)
    }


}