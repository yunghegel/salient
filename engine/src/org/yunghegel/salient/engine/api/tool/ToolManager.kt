package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.utils.ObjectSet

interface ToolManager {

    val tools: ObjectSet<InputTool>

    val toolRegistry: ObjectSet<Class<out InputTool>>

    val active: ObjectSet<InputTool>

    fun <T : InputTool> registerTool(type: Class<T>, tool: T) {
        if (!toolRegistry.contains(type)) {
            toolRegistry.add(type)
            tools.add(tool)
        }
    }

    fun <T : InputTool> getTool(type: Class<T>): T? {
        return tools.find { it::class.java == type } as T?
    }

    fun <T : InputTool> activateTool(type: Class<T>, endOthers: Boolean = true) {
        val tool = getTool(type)
        if (tool != null) {
            if (endOthers) {
                active.forEach { it.deactivate() }
                active.clear()
            }
            active.add(tool)
            tool.activate()
        }
    }

    fun deactivateTool(type: Class<out InputTool>) {
        val tool = getTool(type)
        if (tool != null) {
            active.remove(tool)
            tool.deactivate()
        }
    }


}