package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.utils.Array
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.system.inject


class ToolGroup(var handler: ToolGroupHandler) : InputMultiplexer() {

    val tools: Array<InputTool> = Array()

    private var activeTool: InputTool? = null
    private var defaultTool: InputTool? = null

    private val group: ButtonGroup<Button>

    init {
        group = ButtonGroup<Button>()
        group.setMinCheckCount(0)
    }

    fun setDefaultTool(defaultTool: InputTool?) {
        this.defaultTool = defaultTool
    }

    fun getActiveTool(): InputTool? {
        return activeTool
    }

    fun setActiveTool(tool: InputTool?) {
        if (activeTool != null) {
            activeTool?.deactivate()
            if (tool is MouseTool) {
                removeProcessor(activeTool)
            }

            handler.onToolChanged(null)
            activeTool!!.group = null
        }
        activeTool = tool
        if (activeTool != null) {
            activeTool?.group = this
            handler.onToolChanged(activeTool)
            if (activeTool is MouseTool) {
                addProcessor(activeTool)
            }
            activeTool!!.activate()
        } else {
            group.uncheckAll()
        }
    }

    fun render(renderer: ShapeRenderer = inject()) {
        if (activeTool != null) activeTool!!.render(renderer)
    }

    fun end(tool: InputTool) {
        setActiveTool(defaultTool)
    }

    fun addButton(btTool: Button) {
        group.add(btTool)
    }

    fun clearButtons() {
        group.clear()
    }

    override fun clear() {
        setActiveTool(null)
        clearButtons()
        super.clear()
    }

    fun render(batch: SpriteBatch= inject()) {
        if (activeTool != null) activeTool!!.render(batch)
    }

    fun update(deltaTime: Float = GFX.deltaTime) {
        if (activeTool != null) activeTool!!.update(deltaTime)
    }

    fun removeButton(button: Button?) {
        group.remove(button)
    }
}