package org.yunghegel.salient.editor.plugins.picking.tools

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import org.yunghegel.salient.editor.input.delegateInput
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.engine.tool.InputTool
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import org.yunghegel.salient.engine.graphics.TransformState

class PickingTool( val pickingSystem : PickingSystem) : InputTool("picking_tool") {


    override val blocking: Boolean = false

    init {

    }



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

    companion object {

        const val ARROW_THICKNESS: Float = 0.25f
        const val ARROW_CAP_SIZE: Float = 0.1f
        const val ARROW_DIVISIONS: Int = 12
    }


}