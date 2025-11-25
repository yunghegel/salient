package org.yunghegel.salient.editor.plugins.rendering

import com.badlogic.ashley.core.Entity
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.rendering.components.GameObjectComponent
import org.yunghegel.salient.editor.plugins.rendering.tools.RenderTool
import org.yunghegel.salient.engine.api.tool.Tool
import kotlin.jvm.java

class RenderingSystem : BaseSystem("rendering_plugin", 0, com.badlogic.ashley.core.Family.one(GameObjectComponent::class.java).get()) {

    val renderingTool : RenderTool = RenderTool()

    init {
        index.list(Tool::class.java)?.add(renderingTool)
    }

    override fun processEntity(p0: Entity?, p1: Float) {
        val component = p0?.getComponent(GameObjectComponent::class.java)
        if (component != null) {
            renderingTool.useComponent(component, p0)
        }

    }
}