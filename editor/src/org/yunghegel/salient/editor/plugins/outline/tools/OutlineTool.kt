package org.yunghegel.salient.editor.plugins.outline.tools

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Batch
import org.yunghegel.salient.editor.plugins.outline.systems.OutlineSystem
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.api.tool.ComponentTool
import org.yunghegel.salient.engine.api.tool.Tool

class OutlineTool(val system: OutlineSystem) : ComponentTool<RenderableComponent>("outline_tool",RenderableComponent::class.java) {

    override fun useComponent(component: RenderableComponent) {

    }

    override fun activate() {
        super.activate()
    }

    override fun deactivate() {
        super.deactivate()
    }

    fun applyOutline(entity: Entity) {

    }

    override fun render(batch: Batch) {
        super.render(batch)
    }
}