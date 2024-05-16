package org.yunghegel.salient.editor.plugins

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import org.yunghegel.salient.engine.system.Index
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.editor.app.App
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.ecs.*
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.SystemLoadedEvent
import org.yunghegel.salient.engine.system.inject

open class BaseSystem (override val name: String,prio : Int=0, family:Family = Family.all().get()): System<Project, Scene>(prio,family) {

    val project : Project by lazy { inject () }

    val scene : Scene by lazy { inject() }

    val app : App = inject()

    val index : Index<Named> = inject()

    init {
        post(SystemLoadedEvent(this))
    }

    override fun processEntity(p0: Entity?, p1: Float) {
        return
    }

}