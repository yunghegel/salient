package org.yunghegel.salient.editor.plugins.default

import com.badlogic.ashley.core.Engine
import ktx.inject.Context
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.Tool

class DefaultPlugin() : Plugin {

    val toolSystem = ToolSystem()

    override val registry: Context.() -> Unit = {
        bindSingleton(toolSystem)
    }

    override fun init(engine: Engine) {
        engine.addSystem(toolSystem)
    }

    override val name: String = "default_plugin"

    override val systems: MutableList<System<*, *>>
        get() = mutableListOf(toolSystem)

    override val tools: MutableList<Tool>
        get() = mutableListOf()

}