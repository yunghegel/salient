package org.yunghegel.salient.editor.plugins.base

import com.badlogic.ashley.core.Engine
import org.yunghegel.salient.editor.cmd.ProjectContext
import org.yunghegel.salient.editor.cmd.UICommands
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.editor.plugins.base.systems.HotkeySystem
import org.yunghegel.salient.editor.plugins.base.systems.ToolSystem
import org.yunghegel.salient.editor.plugins.sys.ProfilerComponent
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.ecs.TagsComponent
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.InputTool
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.cmd.Selection
import org.yunghegel.salient.engine.helpers.Pools
import org.yunghegel.salient.engine.ui.widgets.aux.Console

class DefaultPlugin() : Plugin {

    val toolSystem = ToolSystem()
    val hotkeySystem = HotkeySystem()

    override val registry: InjectionContext.() -> Unit = {
        bindSingleton(toolSystem)
        bindSingleton(hotkeySystem)
    }

    override fun init(engine: Engine) {
        Pools.entityPool.defaults(ProfilerComponent::class, TagsComponent::class)

        engine.addSystem(toolSystem)
        engine.addSystem(hotkeySystem)
        Console.register(Selection())
        Console.register(ProjectContext())
        Console.register(UICommands())

    }

    override val name: String = "default_plugin"

    override val systems: MutableList<System<*, *>>
        get() = mutableListOf(toolSystem)

    override val tools: MutableList<Tool>
        get() = mutableListOf()

}