package org.yunghegel.salient.engine.api

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.events.project.onProjectChanged
import org.yunghegel.salient.engine.events.scene.onSceneChanged
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.system.file.Paths

@Serializable
class Meta {

    init {
        onProjectChanged { event ->
            lastLoadedProject = event.new.handle
            pushRecentProject(event.new.handle)
        }
        onSceneChanged { event ->
            lastLoadedScene = event.new.ref
        }
        onShutdown {
            val content = Yaml.default.encodeToString(serializer(), this)
            save(Paths.SALIENT_METAFILE.path) { content }
        }

    }

    var lastLoadedProject: ProjectHandle? = null

    var lastLoadedScene: SceneHandle? = null

    val recentProjects: MutableList<ProjectHandle> = mutableListOf()

    @Transient
    var bootstrapDefaultProject: Boolean = false

    @Transient
    var bootstrapDefaultScene: Boolean = false

    val lastModified: Long
        get() = lastLoadedProject?.path?.lastModified?.toLong() ?: 0L

    fun conf(action: Meta.() -> Unit) = this.apply(action)

    private fun pushRecentProject(project: ProjectHandle) {
        if (recentProjects.contains(project)) recentProjects.remove(project)
        if (recentProjects.size >= 5) recentProjects.removeAt(0)
        recentProjects.add(project)
    }

}