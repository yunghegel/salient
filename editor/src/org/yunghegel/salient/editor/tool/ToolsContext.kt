package org.yunghegel.salient.editor.tool

import com.badlogic.gdx.utils.ObjectSet
import org.yunghegel.salient.editor.plugins.BaseSystem

class ToolSystem : BaseSystem(0), ToolManager{

    override val toolRegistry : ObjectSet<Class<out Tool>> = ObjectSet()

    override val tools : ObjectSet<Tool> = ObjectSet()

    override val active : ObjectSet<Tool> = ObjectSet()
}