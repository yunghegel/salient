package org.yunghegel.salient.editor.plugins.intersect

import com.badlogic.ashley.core.Engine
import ktx.inject.Context
import org.yunghegel.salient.editor.app.salient
import org.yunghegel.salient.editor.plugins.intersect.tools.IntersectorTool
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.Tool

class IntersectionPlugin : Plugin {

    private val intersectionTool = IntersectorTool()

    override val systems: MutableList<System<*, *>> = mutableListOf()



    override val tools: MutableList<Tool> = mutableListOf(intersectionTool)
    override val registry: Context.() -> Unit = {
        bindSingleton(intersectionTool)
    }

    override fun init(engine: Engine) {
        salient{
            gui.viewportWidget.tools.createTool("select",intersectionTool)
        }
    }

    override val name: String = "intersection_plugin"
}