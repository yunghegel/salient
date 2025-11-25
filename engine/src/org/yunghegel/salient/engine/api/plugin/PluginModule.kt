package org.yunghegel.salient.engine.api.plugin

import com.badlogic.ashley.core.EntitySystem
import org.yunghegel.salient.engine.Module
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.engine.api.tool.Tool
import kotlin.reflect.KClass

open class PluginModule() : Module {

    override val priority: Int = 0


    open val systems: MutableList<EntitySystem> = mutableListOf()


     override val plugins: MutableList<KClass<out Plugin>> = mutableListOf()

    open val tools: MutableList<Tool> = mutableListOf()


    override val afterEval : MutableList<()->Unit> = mutableListOf()

    override val beforeEval: MutableList<() -> Unit> = mutableListOf()
    override var registry: InjectionContext.() -> Unit = {}


    abstract class Builder {

        abstract val _systems: MutableList<KClass<out EntitySystem>>

        abstract val _plugins: MutableList<KClass<out Plugin>>

        abstract val _tools: MutableList<KClass<out Tool>>

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

        fun systems(configure: ()->List<KClass<out EntitySystem>>) {
            _systems.addAll(configure())
        }

        fun plugins(configure: ()->List<KClass<out Plugin>>) {
            _plugins.addAll(configure())
        }

        fun tools(configure: () -> List<KClass<out Tool>>) {
            _tools.addAll(configure())
        }

        fun provide(supply: InjectionContext.()->Unit) {
            _registry = supply
        }

        fun finalize(supply: ()->Unit) {
            finalizer = supply
        }

    }
}

fun module(configure: PluginModule.Builder.()->Unit): PluginModule {
    val builder = object: PluginModule.Builder() {

        val registry: InjectionContext.()->Unit get() = _registry

        override val _systems: MutableList<KClass<out EntitySystem>> = mutableListOf()

        override val _plugins: MutableList<KClass<out Plugin>> = mutableListOf()

        override val _tools: MutableList<KClass<out Tool>> = mutableListOf()

    }
    builder.configure()
    return PluginModule().apply {
        beforeEval.addAll(builder._doBefore)
        afterEval.addAll(builder._doLast)
        registry = builder._registry
        plugins.addAll(builder._plugins)
    }
}

