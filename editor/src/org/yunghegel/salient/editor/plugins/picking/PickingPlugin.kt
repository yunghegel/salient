package org.yunghegel.salient.editor.plugins.picking

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.editor.app.Salient
import org.yunghegel.salient.editor.app.Salient.Companion.addSystem
import org.yunghegel.salient.editor.app.salient
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.picking.systems.HoverSystem
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.editor.plugins.picking.tools.HoverTool
import org.yunghegel.salient.editor.plugins.picking.tools.PickingTool
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.scene3d.GameObject

class PickingPlugin : Plugin {

    val pickingSystem = PickingSystem()
    val hoverSystem = HoverSystem()

    val pickerTool = PickingTool(pickingSystem)
    val hoverTool = HoverTool(hoverSystem)

    override val name: String = "picking_plugin"

    override val systems: MutableList<System<*,*>> = mutableListOf(pickingSystem,hoverSystem)

    override val tools: MutableList<Tool> = mutableListOf(pickerTool)

    override val registry: InjectionContext.() -> Unit = {
        bindSingleton(pickingSystem)
        bindSingleton(pickingSystem.picker)
        bindSingleton(hoverSystem)
        bindSingleton(pickerTool)
        bindSingleton(hoverTool)
        setProvider(GameObject::class.java) { pickingSystem.selectionManager.getSelected() ?: pickingSystem.scene.graph.root }
    }

    override fun init(engine: Engine) {
        salient {
            addSystem(pickingSystem)
            addSystem(hoverSystem)
            pipeline.buffers["picking"] = pickingSystem.picker.fbo
            gui.viewportWidget.tools.createTool("select",pickerTool,true)
        }

    }


}