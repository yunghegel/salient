package org.yunghegel.salient.editor.plugins.picking

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import ktx.inject.Context
import org.yunghegel.salient.editor.app.Salient
import org.yunghegel.salient.editor.app.Salient.Companion.addSystem
import org.yunghegel.salient.editor.app.salient
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.editor.plugins.picking.tools.PickingTool
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.plugin.Plugin
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.tool.Tool

class PickingPlugin : Plugin {

    val pickingSystem = PickingSystem()

    val pickerTool = PickingTool(pickingSystem)

    override val name: String = "picking_plugin"

    override val systems: MutableList<System<*,*>> = mutableListOf(pickingSystem)

    override val tools: MutableList<Tool> = mutableListOf(pickerTool)

    override val registry: Context.() -> Unit = {
        bindSingleton(pickingSystem)
        bindSingleton(pickerTool)
        bindSingleton(PickingSystem.picker)
    }

    override fun init(engine: Engine) {
        salient {
            addSystem(pickingSystem)
            gui.viewportWidget.tools.createTool("select",pickerTool)
        }
    }


}