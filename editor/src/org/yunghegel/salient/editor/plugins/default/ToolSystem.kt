package org.yunghegel.salient.editor.plugins.default

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import org.yunghegel.gdx.utils.ext.clearDepth
import org.yunghegel.gdx.utils.ext.clearScreen
import org.yunghegel.gdx.utils.ext.draw
import org.yunghegel.gdx.utils.ext.pass
import org.yunghegel.salient.editor.app.pipeline
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.engine.State
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.api.tool.ToolComponent

class ToolSystem : BaseSystem("ToolSystem", State.OVERLAY_PASS, Family.one(ToolComponent::class.java).get()) {

    val tools = mutableListOf<Tool>()

    val buf = engine.buildBuffer("tool_buffer")

    override fun processEntity(p0: Entity?, p1: Float) {
        val tool = p0?.getComponent(ToolComponent::class.java)?.tool
        tool?.let { tools.add(it) }
        return
    }

    override fun update(deltaTime: Float) {
        tools.clear()
        super.update(deltaTime)


            tools.forEach { tool ->
                renderTool(tool)
            }


    }

    fun renderTool(tool: Tool) = with(sceneContext) {
        tool.renderMask.eachTrue { enum ->
            when (enum) {
                Tool.RenderUsage.SHAPE_RENDERER -> {
                    tool.render(shapeRenderer)
                }
                Tool.RenderUsage.BATCH -> {
                   tool.render(spriteBatch)
                }
                Tool.RenderUsage.MODEL_BATCH -> {
                    tool.render(modelBatch,environment)
                }
            }
        }
    }

}