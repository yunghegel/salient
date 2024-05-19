package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.utils.ObjectSet

class ToolsContext : ToolManager {

    override val toolRegistry : ObjectSet<Class<out Tool>> = ObjectSet()

    override val tools : ObjectSet<Tool> = ObjectSet()

    override val active : ObjectSet<Tool> = ObjectSet()
}