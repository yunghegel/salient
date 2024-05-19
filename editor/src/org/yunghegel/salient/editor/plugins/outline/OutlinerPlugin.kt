package org.yunghegel.salient.editor.plugins.outline

import com.badlogic.ashley.core.Engine
import ktx.inject.Context
import org.yunghegel.salient.core.graphics.util.OutlineDepth
import org.yunghegel.salient.editor.app.Salient.Companion.addSystem
import org.yunghegel.salient.editor.app.salient
import org.yunghegel.salient.editor.plugins.outline.lib.Outliner
import org.yunghegel.salient.editor.plugins.outline.systems.OutlineSystem
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.api.tool.Tool

class OutlinerPlugin : Plugin {

    val outliner : Outliner = Outliner()
    val outlineDepth = OutlineDepth()
    val outlineSystem = OutlineSystem(outlineDepth)



    override val name: String = "outline_plugin"

    override val systems: MutableList<System<*, *>> = mutableListOf(outlineSystem)

    override val tools: MutableList<Tool> = mutableListOf()

    override val registry: Context.() -> Unit = {
        bindSingleton(outliner)
        bindSingleton(outlineSystem)
        bindSingleton(outlineDepth)
    }

    override fun init(engine: Engine) {
        salient {
            addSystem(outlineSystem)
            pipeline.buffers["outline"] = outlineSystem.outlinefbo
        }
    }


}