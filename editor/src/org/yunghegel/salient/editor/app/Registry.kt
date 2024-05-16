package org.yunghegel.salient.editor.app

import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.plugin.Plugin
import org.yunghegel.salient.engine.system.Index
import org.yunghegel.salient.engine.tool.Tool

object Registry {

    val tools = Index<Tool>()
    val systems = Index<System<*,*>>()
    val plugins = Index<Plugin>()

    init {
        tools.types[Tool::class.java] = mutableListOf()
    }




}