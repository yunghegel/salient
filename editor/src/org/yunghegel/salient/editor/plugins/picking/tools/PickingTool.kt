package org.yunghegel.salient.editor.plugins.picking.tools

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import org.yunghegel.gdx.utils.selection.Pickable
import org.yunghegel.salient.editor.input.delegateInput
import org.yunghegel.salient.editor.input.undelegateInput
import org.yunghegel.salient.editor.plugins.intersect.tools.IntersectorTool
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.api.tool.InputTool
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.graphics.util.DebugDrawer
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.widgets.menu.ContextMenu

class PickingTool( val pickingSystem : PickingSystem) : InputTool("picking_tool") {



    override val blocking: Boolean = false

    init {
        renderMask.set(Tool.RenderUsage.SHAPE_RENDERER,true)
    }

    val intersector : IntersectorTool by lazy { inject() }

    var intersection : IntersectorTool.IntersectionResult? = null

    val drawer : DebugDrawer by lazy { inject() }

    override fun activate() {
        delegateInput(listener = this )
        super.activate()
    }

    override fun deactivate() {
        undelegateInput(listener = this)
        super.deactivate()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        pickingSystem.pick(screenX.toFloat(),screenY.toFloat(),button,key,isDoubleClick)
        if (button == Input.Buttons.RIGHT) ContextMenu().apply {
            addOption("Delete") { }
            addOption("Duplicate") { }
            attachListener(UI.root)
         }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    fun queryFor(x: Float, y: Float,pickables : List<Pickable>, cb : (Int)->Unit) : Pickable? {
        return pickingSystem.pick(x,y,pickables,cb)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun render(renderer: ShapeRenderer) {
        intersector.lastResult?.takeIf { it.intersection != Vector3.Zero }?.let {
            tmp1.set(it.intersection)
            tmp2.set(tmp1.x + 0.125f,tmp1.y,tmp1.z)
            tmp3.set(tmp1.x,tmp1.y,tmp1.z + 0.125f)
            tmpx.set(tmp1.x - 0.125f,tmp1.y,tmp1.z)
            tmpy.set(tmp1.x,tmp1.y,tmp1.z - 0.125f)
            renderer.color.set(Color.BLACK)
            drawer.drawWireDisc(it.intersection, Vector3.Y,0.125f)
            renderer.line(tmp2,tmpx)
            renderer.line(tmp3,tmpy)

//            cross
        }
    }

    private companion object {
        val tmp1 = Vector3()
        val tmp2 = Vector3()
        val tmp3 = Vector3()
        val tmpx = Vector3()
        val tmpy = Vector3()
    }
}