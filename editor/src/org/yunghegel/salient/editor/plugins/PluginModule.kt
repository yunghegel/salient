package org.yunghegel.salient.editor.plugins

import org.yunghegel.salient.editor.tool.Tool

open class PluginModule() {

    val systems: MutableList<BaseSystem> = mutableListOf()

    val plugins: MutableList<Plugin> = mutableListOf()

    val tools: MutableList<Tool>  = mutableListOf<Tool>()

    var injector: ()->Unit = {}

    var finalize = {}


    abstract class Builder() {

        abstract val _systems: MutableList<BaseSystem>

        abstract val _plugins: MutableList<Plugin>

        abstract val _tools: MutableList<Tool>

        var init: ()->Unit  = {}

        var finalizer : ()->Unit = {}





        fun systems(configure: ()->List<BaseSystem>) {
            _systems.addAll(configure())
        }

        fun plugins(configure: ()->List<Plugin>) {
            _plugins.addAll(configure())
        }

        fun tools(configure: ()->List<Tool>) {
            _tools.addAll(configure())
        }

        fun provide(supply: ()->Unit) {
            init = supply
        }

        fun finalize(supply: ()->Unit) {
            finalizer = supply
        }

        fun build(): PluginModule {
            return PluginModule().apply {
                systems.addAll(_systems)
                plugins.addAll(_plugins)
                tools.addAll(_tools)
                injector = init
                finalize = finalizer
            }
        }

        }
}

fun module(configure: PluginModule.Builder.()->Unit): PluginModule {
    val builder = object: PluginModule.Builder() {

        override val _systems: MutableList<BaseSystem> = mutableListOf()

        override val _plugins: MutableList<Plugin> = mutableListOf()

        override val _tools: MutableList<Tool> = mutableListOf()

        init {
            configure()
        }

    }
    builder.configure()
    return PluginModule()
}

