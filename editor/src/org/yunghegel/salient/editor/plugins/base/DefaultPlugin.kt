package org.yunghegel.salient.editor.plugins.base

import com.badlogic.ashley.core.Engine
import ktx.inject.Context
import org.yunghegel.salient.editor.plugins.base.systems.ToolSystem
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.cmd.Selection
import org.yunghegel.salient.engine.ui.widgets.aux.Console

class DefaultPlugin() : Plugin {

    val toolSystem = ToolSystem()

    override val registry: Context.() -> Unit = {
        bindSingleton(toolSystem)
    }

    override fun init(engine: Engine) {
        engine.addSystem(toolSystem)
        Console.register(Selection())
    }

    override val name: String = "default_plugin"

    override val systems: MutableList<System<*, *>>
        get() = mutableListOf(toolSystem)

    override val tools: MutableList<Tool>
        get() = mutableListOf()

}