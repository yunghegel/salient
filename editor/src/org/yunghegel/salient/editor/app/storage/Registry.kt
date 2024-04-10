package org.yunghegel.salient.editor.app

import com.badlogic.gdx.utils.ObjectSet
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import org.yunghegel.salient.editor.app.event.onEditorInitialized
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.events.asset.onAssetAdded
import org.yunghegel.salient.engine.events.asset.onAssetDiscovery
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.events.onProjectDiscovery
import org.yunghegel.salient.engine.events.project.onProjectCreated
import org.yunghegel.salient.engine.events.scene.onSceneDiscovery
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.scene3d.events.onGameObjectAdded
import org.yunghegel.salient.engine.sys.Filepath.Companion.pathOf
import org.yunghegel.salient.engine.sys.Paths

@Serializable
class Registry {



    val project_index = mutableListOf<ProjectHandle>().apply {
        onShutdown {
            forEach { handle ->
                val project = handle
                println("Saving project ${project.name}")
                val data = Yaml.default.encodeToString(ProjectHandle.serializer(),project)
                save(Paths.PROJECTS_DIR.child("${project.name}.project").path) { data }
            }
        }
    }

    val asset_index = mutableListOf<AssetHandle>()

    private val registry = mutableMapOf<String,String>()

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
                println("${it.go.name} added to scene")
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
            println("ERROR: Project handle already exists")
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
            println("ERROR: Asset handle already exists")
        }
        asset_index.add(asset)
    }

    fun verifyHandle(handle: AssetHandle): Boolean {
        return asset_index.any { it != handle } && handle.path.exists
    }

}