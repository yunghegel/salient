package org.yunghegel.salient.editor.plugins

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import org.yunghegel.salient.engine.system.Index
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.editor.app.App
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.api.ecs.*
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.SystemLoadedEvent
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.system.inject

open class BaseSystem (override val name: String,prio : Int=0, family:Family = Family.all().get()): System<Project, Scene>(prio,family) {

    constructor(name: String, enum: Enum<*>, family: Family) : this(name,enum.ordinal,family)

    val project : Project by lazy { inject () }

    val engine: Pipeline by lazy { inject() }

    val scene : Scene by lazy { inject() }

    val sceneContext : SceneContext by lazy { inject() }

    val app : App = inject()

    val index : Index<Named> = inject()

    fun <T: Tool> tool(name: String) : T {
        var tool : T? = null
        index.list(Tool::class.java)?.forEach {
            if (it.name == name) {
                tool = it as T
            }
        }
        return tool!!
    }



    init {
        post(SystemLoadedEvent(this))
    }

    override fun processEntity(p0: Entity?, p1: Float) {
        return
    }

}