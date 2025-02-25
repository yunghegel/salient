package org.yunghegel.salient.editor.plugins.sys

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import ktx.ashley.contains
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.system.InjectionContext

class ProfilerPlugin : Plugin {

    override val systems: MutableList<System<*, *>> = mutableListOf()

    override val tools: MutableList<Tool> = mutableListOf()

    override val name: String = "profiler"

    override val registry: InjectionContext.() -> Unit = {

    }

    override fun init(engine: Engine) {

        engine.addEntityListener(0, object : EntityListener {
            override fun entityAdded(p0: Entity) {
                if (!p0.contains(ProfilerComponent.mapper)) {
                    p0.add(ProfilerComponent())
                }
                val profilerComponent = ProfilerComponent.mapper.get(p0)
                profilerComponent.start("entity")
            }

            override fun entityRemoved(p0: Entity?) {
                val profilerComponent = ProfilerComponent.mapper.get(p0)
                profilerComponent.end("entity")
            }
        })

    }
}