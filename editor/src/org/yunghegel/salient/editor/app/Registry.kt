package org.yunghegel.salient.editor.app

import ktx.collections.GdxArray
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.system.Index
import org.yunghegel.salient.engine.api.tool.InputTool

object Registry {

    val tools = Index<InputTool>()
    val systems = Index<System<*,*>>()
    val plugins = Index<Plugin>()

    val toolKeys = GdxArray<Int>()

    init {
        tools.types[InputTool::class.java] = mutableListOf()
        systems.types[System::class.java] = mutableListOf()
        plugins.types[Plugin::class.java] = mutableListOf()
    }

    fun registerTool(type: Class<out InputTool>, tool: InputTool) {
        tools.types[type]?.add(tool)
    }

    fun registerSystem(type: Class<out System<*,*>>, system: System<*,*>) {
        systems.types[type]?.add(system)
    }

    fun registerPlugin(type: Class<out Plugin>, plugin: Plugin) {
        plugins.types[type]?.add(plugin)
    }








}