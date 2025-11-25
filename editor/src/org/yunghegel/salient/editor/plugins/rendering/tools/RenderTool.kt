package org.yunghegel.salient.editor.plugins.rendering.tools

import com.badlogic.ashley.core.Entity
import org.yunghegel.salient.editor.modules.ui
import org.yunghegel.salient.editor.plugins.rendering.components.GameObjectComponent
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.tool.ComponentTool
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.system.inject

class RenderTool() : ComponentTool<GameObjectComponent>("rendering_tool",GameObjectComponent::class.java) {
    val scene  : Scene = inject()
    override fun useComponent(component: GameObjectComponent, entity: Entity) {
        val go = component.gameObject
        with(GFX) {
            with(sceneContext) {
                ui.act()

            }
        }


    }

}