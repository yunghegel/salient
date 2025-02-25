package org.yunghegel.salient.engine.api.plugin

import com.badlogic.ashley.core.EntitySystem
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.engine.api.tool.InputTool

interface PluginModule {

    val systems: MutableList<EntitySystem>

    val plugins: MutableList<Plugin>

    val tools: MutableList<InputTool>

    val afterEval : MutableList<()->Unit>

    val beforeEval : MutableList<()->Unit>

    var registry: InjectionContext.()->Unit

    var finalize: ()->Unit


    abstract class Builder {

        abstract val _systems: MutableList<EntitySystem>

        abstract val _plugins: MutableList<Plugin>

        abstract val _tools: MutableList<InputTool>

        var _doLast : MutableList<() -> Unit> = mutableListOf()

        var _doBefore : MutableList<() -> Unit> = mutableListOf()

        var _registry: InjectionContext.()->Unit  = {}

        var finalizer : ()->Unit = {}




        fun doLast(supply: ()->Unit) {
            _doLast.add(supply)
        }

        fun doBefore(supply: ()->Unit) {
            _doBefore.add(supply)
        }

        fun systems(configure: ()->List<EntitySystem>) {
            _systems.addAll(configure())
        }

        fun plugins(configure: ()->List<Plugin>) {
            _plugins.addAll(configure())
        }

        fun tools(configure: () -> List<InputTool>) {
            _tools.addAll(configure())
        }

        fun provide(supply: InjectionContext.()->Unit) {
            _registry = supply
        }

        fun finalize(supply: ()->Unit) {
            finalizer = supply
        }

        fun build(): PluginModule {
            return object : PluginModule {
                override val systems: MutableList<EntitySystem> = _systems

                override val plugins: MutableList<Plugin> = _plugins

                override val tools: MutableList<InputTool> = _tools

                override val afterEval: MutableList<() -> Unit> = mutableListOf()

                override val beforeEval: MutableList<() -> Unit> = mutableListOf()

                override var registry: InjectionContext.()->Unit = _registry

                override var finalize: ()->Unit = finalizer
            }
        }

        }
}

fun module(configure: PluginModule.Builder.()->Unit): PluginModule {
    val builder = object: PluginModule.Builder() {

        override val _systems: MutableList<EntitySystem> = mutableListOf()

        override val _plugins: MutableList<Plugin> = mutableListOf()

        override val _tools: MutableList<InputTool> = mutableListOf()

        init {
            configure()
        }

    }
    builder.configure()
    return builder.build()
}

