package org.yunghegel.salient.editor.tool

import com.badlogic.gdx.utils.ObjectSet

interface ToolManager {

    val tools : ObjectSet<Tool>

    val toolRegistry : ObjectSet<Class<out Tool>>

    val active : ObjectSet<Tool>

    fun <T:Tool> registerTool(type: Class<T>, tool: T) {
        if (!toolRegistry.contains(type)) {
            toolRegistry.add(type)
            tools.add(tool)
        }
    }

    fun <T:Tool> getTool(type: Class<T>): T? {
        return tools.find { it::class.java == type } as T?
    }

    fun <T:Tool> activateTool(type: Class<T>, endOthers: Boolean = true) {
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

    fun deactivateTool(type: Class<out Tool>) {
        val tool = getTool(type)
        if (tool != null) {
            active.remove(tool)
            tool.deactivate()
        }
    }


}