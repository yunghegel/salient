package org.yunghegel.salient.editor.plugins.base.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.yunghegel.gdx.utils.ext.clearDepth
import org.yunghegel.salient.editor.app.Salient.Companion.once
import org.yunghegel.salient.editor.app.salient
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.engine.State
import org.yunghegel.salient.engine.api.tool.InputTool
import org.yunghegel.salient.engine.graphics.RenderUsage
import org.yunghegel.salient.engine.api.tool.ToolComponent
import org.yunghegel.salient.engine.system.Netgraph

class ToolSystem : BaseSystem("ToolSystem", State.AFTER_COLOR_PASS, Family.one(ToolComponent::class.java).get()) {

    val tools = mutableListOf<InputTool>()

    val buf = engine.buildBuffer("tool_buffer")

    init {
        Netgraph.add("total tools:") { tools.size.toString() }
    }

    override fun processEntity(p0: Entity?, p1: Float) {
        val tool = p0?.getComponent(ToolComponent::class.java)?.tool
        tool?.let { tools.add(it) }
        return
    }

    override fun update(deltaTime: Float) {


        tools.clear()
        super.update(deltaTime)
        if (tools.isEmpty()) return
        tools.filter { it.active }.forEach { it.update(deltaTime) }

        with(sceneContext) {

            salient {
                once(state = State.AFTER_COLOR_PASS) {
//                    pass(buf) {
//                        clearScreen(Color.CLEAR)
                        clearDepth()
                        gui.updateviewport()
                        shapeRenderer.begin()
                        shapeRenderer.projectionMatrix = perspectiveCamera.combined
                        blinnPhongBatch.begin(perspectiveCamera)
                        tools.filter{it.active}.forEach { tool ->
                            tool.update(deltaTime)
                            renderTool(tool, blinnPhongBatch, environment)
                        }
                        blinnPhongBatch.end()
                        shapeRenderer.end()
//                    }.draw(spriteBatch)
                }
            }
        }


        }


    fun renderTool(tool: InputTool, batch: ModelBatch, env: Environment) = with(sceneContext) {
        tool.renderMask.eachTrue { enum ->
            when (enum) {
                RenderUsage.SHAPE_RENDERER -> {
                    tool.render(shapeRenderer)
                }

                RenderUsage.BATCH -> {
                   tool.render(spriteBatch)
                }

                RenderUsage.MODEL_BATCH -> {
                    tool.render(batch,env)
                }
            }
        }
    }

}