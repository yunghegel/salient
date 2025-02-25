package org.yunghegel.salient.engine.api

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.serializer
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.properties.Resource
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.events.project.onProjectChanged
import org.yunghegel.salient.engine.events.scene.onSceneChanged
import org.yunghegel.salient.engine.helpers.encodestring
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.file.Paths

@OptIn(InternalSerializationApi::class)
@Serializable
class Meta : Resource {

    override val file: Filepath = Paths.SALIENT_METAFILE

    init {
        onProjectChanged { event ->
            lastLoadedProject = event.new.handle
            pushRecentProject(event.new.handle)
        }
        onSceneChanged { event ->
            lastLoadedScene = event.new.ref
        }
        onShutdown {
            val content = Yaml.default.encodeToString(Meta::class.serializer(), this)
            println(this.toString())
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
        get() = lastLoadedProject?.file?.lastModified?.toLong() ?: 0L

    fun conf(action: Meta.() -> Unit) = this.apply(action)

    private fun pushRecentProject(project: ProjectHandle) {
        if (recentProjects.any { it.name == project.name || it.file.path == project.file.path }) return
        
        if (recentProjects.contains(project)) recentProjects.remove(project)
        if (recentProjects.size >= 5) recentProjects.removeAt(0)
        recentProjects.add(project)
    }

    override fun toString(): String {
        return encodestring(this )
    }

}