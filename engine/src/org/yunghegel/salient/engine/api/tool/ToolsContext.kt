package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.utils.ObjectSet

class ToolsContext : ToolManager {

    override val toolRegistry: ObjectSet<Class<out InputTool>> = ObjectSet()

    override val tools: ObjectSet<InputTool> = ObjectSet()

    override val active: ObjectSet<InputTool> = ObjectSet()
}