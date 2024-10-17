package org.yunghegel.salient.editor.plugins.gizmos

import com.badlogic.ashley.core.Engine
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.editor.app.Salient.Companion.addSystem
import org.yunghegel.salient.editor.app.Salient.Companion.buffers
import org.yunghegel.salient.editor.app.salient
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.editor.plugins.gizmos.tools.PlacementTool
import org.yunghegel.salient.editor.plugins.gizmos.tools.RotateTool
import org.yunghegel.salient.editor.plugins.gizmos.tools.ScaleTool
import org.yunghegel.salient.editor.plugins.gizmos.tools.TranslateTool
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.Tool

class GizmoPlugin : Plugin {

    val system = GizmoSystem()

    val rotateTool = RotateTool(system)
    val scaleTool = ScaleTool(system)
    val translateTool = TranslateTool(system)
    val placementTool = PlacementTool()




    override val systems: MutableList<System<*, *>> = mutableListOf(system)

    override val tools: MutableList<Tool> = mutableListOf(rotateTool,scaleTool,translateTool,placementTool)

    override val registry: InjectionContext.() -> Unit = {
        bindSingleton(system)
        bindSingleton(rotateTool)
        bindSingleton(scaleTool)
        bindSingleton(translateTool)
        bindSingleton(placementTool)
    }

    override fun init(engine: Engine) {
       salient {
            gui.viewportWidget.tools.createTool("rotate",rotateTool)
            gui.viewportWidget.tools.createTool("scale",scaleTool)
            gui.viewportWidget.tools.createTool("translate",translateTool)
           addSystem(system)

           buffers["translate"] = translateTool.picker.fbo
       }
    }

    override val name: String = "gizmo_plugin"
}