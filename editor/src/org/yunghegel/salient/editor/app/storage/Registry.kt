package org.yunghegel.salient.editor.app.storage

import com.badlogic.gdx.utils.ObjectSet
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.salient.common.util.TypeMap
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.Plugin
import org.yunghegel.salient.editor.tool.Tool
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.events.asset.onAssetAdded
import org.yunghegel.salient.engine.events.asset.onAssetDiscovery
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.events.onProjectDiscovery
import org.yunghegel.salient.engine.events.project.onProjectCreated
import org.yunghegel.salient.engine.events.scene.onSceneDiscovery
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.graphics.scene3d.events.onGameObjectAdded
import org.yunghegel.salient.engine.io.Filepath.Companion.pathOf
import org.yunghegel.salient.engine.io.Paths
import org.yunghegel.salient.engine.io.debug
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.io.warn

@Serializable
class Registry {



    val project_index = mutableListOf<ProjectHandle>().apply {
        onShutdown {
            forEach { handle ->
                val project = handle
                debug("Saving project ${project.name}")
                val data = Yaml.default.encodeToString(ProjectHandle.serializer(),project)
                save(Paths.PROJECTS_DIR.child("${project.name}.project").path) { data }
            }
        }
    }

    val asset_index = mutableListOf<AssetHandle>()

    private val key_val = mutableMapOf<String,String>()

    @Transient
    private val tool_access: TypeMap<Tool> = TypeMap()

    @Transient
    private val plugin_access: TypeMap<Plugin> = TypeMap()

    @Transient
    private val system_acess: TypeMap<BaseSystem> = TypeMap()

    @Transient
    private val system_index : ObjectSet<Class<out BaseSystem>> = ObjectSet()

    @Transient
    private val plugin_index : ObjectSet<Class<out Plugin>> = ObjectSet()

    @Transient
    private val tool_index : ObjectSet<Class<out Tool>> = ObjectSet()

    val allSystems : List<BaseSystem>
        get() = system_index.map { system(it) }

    fun indexTool(tool: Tool) {
        if (tool_index.contains(tool::class.java)) {
            warn("ERROR: Tool ${tool.name} already indexed")
            return
        }
        tool_index.add(tool::class.java)
        tool_access.put(tool::class.java,tool)
    }

    fun indexPlugin(plugin: Plugin) {
        if (plugin_index.contains(plugin::class.java)) {
            warn("ERROR: Plugin ${plugin::class.simpleName} already indexed")
            return
        }
        plugin_index.add(plugin::class.java)
        plugin_access.put(plugin::class.java,plugin)
    }

    fun indexSystem(system: BaseSystem) {
        if (system_index.contains(system::class.java)) {
            warn("ERROR: System ${system::class.simpleName} already indexed")
            return
        }
        system_index.add(system::class.java)
        system_acess.put(system::class.java,system)
    }

    infix fun <T:Tool> tool (tool: Class<T>) : T {
       return tool_access.get(tool) as T
    }

    infix fun <T:Plugin> plugin (plugin: Class<T>) : T {
        return plugin_access.get(plugin) as T
    }

    infix fun <T: BaseSystem> system (system: Class<T>) : T {
        return system_acess.get(system) as T
    }



        init {
            onProjectDiscovery { event ->
                val project = event.project
                indexProject(project.name,project.path.path)
            }

            onSceneDiscovery { event ->
                val scene = event.handle
                indexAsset(scene.name,scene.path.path)
            }

            onProjectCreated { event ->
                val project = event.project
                indexProject(project.name,project.path.path)
            }

            onAssetDiscovery { event ->
                val asset = event.handle
                indexAsset(asset.name,asset.path.path)
            }

            onAssetAdded { event ->
                val asset = event.asset
                indexAsset(asset.file.nameWithoutExtension(),asset.file.path())
            }
            onGameObjectAdded {
                debug("${it.go.name} added to scene")
                it.go.components.forEach {
                    println("Component: ${it::class.simpleName}")
                }
            }
        }
    fun findProject(name: String): ProjectHandle? {
        return project_index.find { it.name == name }
    }

    fun findAsset(name: String): AssetHandle? {
        return asset_index.find { it.name == name }
    }

    fun indexProject(name: String,path:String) {

        val project = findProject(name) ?: ProjectHandle(name,path.pathOf())

        if (!verifyHandle(project)) {
            warn("ERROR: Project handle already exists")
            return
        }
        project_index.add(project)
    }



    fun verifyHandle(handle: ProjectHandle): Boolean {
        return project_index.any { it != handle } && handle.path.exists
    }

    fun indexAsset(name:String, path:String) {

        val asset = findAsset(name) ?: AssetHandle(path)

        if (!verifyHandle(asset)) {
            warn("ERROR: Asset handle already exists")
        }
        asset_index.add(asset)
    }

    fun verifyHandle(handle: AssetHandle): Boolean {
        return asset_index.any { it != handle } && handle.path.exists
    }

}

inline fun <reified T:Tool> tool(): T {
    val registry :Registry = inject()
    return registry tool (T::class.java)
}