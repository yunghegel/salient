package org.yunghegel.salient.editor.plugins.picking.tools

import com.badlogic.gdx.Gdx
import org.yunghegel.gdx.utils.selection.Pickable
import org.yunghegel.salient.editor.input.delegateInput
import org.yunghegel.salient.editor.plugins.picking.systems.HoverSystem
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.engine.api.flags.GameObjectFlag
import org.yunghegel.salient.engine.scene3d.component.PickableComponent
import org.yunghegel.salient.engine.system.Netgraph
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.tool.InputTool
import org.yunghegel.salient.engine.ui.UI

class HoverTool(val pickingSystem: HoverSystem) : InputTool("hover_tool"){

    var previous : Pickable? = null
        set(value) {

            if (field != null) {
                if(field is PickableComponent) {
                    (field as PickableComponent).go.clear(GameObjectFlag.HOVERED)
                }
            }
            field = value

        }

    private var hovered : Pickable? = null
        set(value) {
            if (field != value && field != null) {
                if(field is PickableComponent) {
                    (field as PickableComponent).go.clear(GameObjectFlag.HOVERED)
                }
            } else {
                if(value is PickableComponent) {
                    (value as PickableComponent).go.set(GameObjectFlag.HOVERED)
                }
            }
            field = value

        }
    val pickingSys : PickingSystem by lazy {inject()}
    var refresh = false
    var x: Int = 0
    var y : Int = 0
    var action : ((Pickable?)->Unit)? = null

    init {
        Netgraph.add("hovered") { (hovered?.id ?: "null").toString() }
        Netgraph.add("input") { "$x,$y" }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        refresh = true
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        x = -1
        y = -1
        hovered = null
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        x = screenX
        y = Gdx.graphics.height - screenY
        hovered = pickingSystem.hover(x,y) {
            action?.invoke(it)
        }
        return super.mouseMoved(screenX, screenY)
    }

    fun query(refresh:Boolean = false, screenX:Int =x ,screenyY:Int =y,action: (Pickable?)->Unit) : Pickable? {
        this.refresh =refresh

        this.action = action
        mouseMoved(screenX,screenyY )

         return hovered
    }


    override fun activate() {
        super.activate()
        delegateInput(listener = this)
    }

    override fun deactivate() {
        delegateInput(listener = null)
        super.deactivate()
        action = null
    }



}