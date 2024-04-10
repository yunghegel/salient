package org.yunghegel.salient.editor.plugins

import org.yunghegel.salient.editor.app.App
import org.yunghegel.salient.editor.app.storage.Registry
import org.yunghegel.salient.editor.plugins.events.SystemLoadedEvent
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.io.inject

open class BaseSystem (prio : Int): System<Project, Scene>(prio) {

    val project : Project by lazy { inject () }

    val scene : Scene by lazy { inject() }

    val app : App = inject()
    val registry : Registry = inject()

    init {
        registry.indexSystem(this)
        post(SystemLoadedEvent(this))
    }

}