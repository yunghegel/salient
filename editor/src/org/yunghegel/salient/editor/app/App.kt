package org.yunghegel.salient.editor.app

import com.charleskorn.kaml.Yaml
import org.yunghegel.salient.engine.events.lifecycle.onEditorInitialized
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.ProjectDiscoveryEvent
import org.yunghegel.salient.engine.events.scene.SceneDiscoveryEvent
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext
import org.yunghegel.salient.engine.graphics.scene3d.SceneRenderer
import org.yunghegel.salient.engine.io.*
import org.yunghegel.salient.engine.io.Filepath.Companion.pathOf
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.widgets.notif.AlertStrategy
import org.yunghegel.salient.engine.ui.widgets.notif.alert
import org.yunghegel.salient.engine.ui.widgets.notif.notify

typealias engine = Salient.Companion

typealias ui = UI

typealias scene = Scene

typealias stage = UI



class App {


    internal val projectManager: ProjectManager = ProjectManager()

    internal val sceneManager: SceneManager = SceneManager()

    internal val assetManager: AssetManager = AssetManager()

    internal val project: Project
        get() = inject()

    internal val scene: Scene
        get() = inject()

    internal lateinit var meta: Meta

    val appManagement: Triple<ProjectManager, SceneManager, AssetManager> = Triple(projectManager, sceneManager, assetManager)

    val appState: Pair<Project, Scene>  by lazy {Pair(project, scene)}

    init {
        onEditorInitialized {
            Log.processStack()
            alert("Editor Initialized")
            notify("Editor Initialized", strategy =  AlertStrategy.IMMEDIATE)
        }
    }


    fun bootstrap() : Scene?{

        singleton(projectManager)
        singleton(sceneManager)
        singleton(assetManager)


        provide { projectManager.currentProject ?: projectManager.createDefault() }
        provide { if (projectManager.currentProject != null && projectManager.currentProject?.currentScene != null) projectManager.currentProject?.currentScene!! else sceneManager.createDefault() }
        provide<SceneContext> { scene.context }
        provide<SceneRenderer<Scene, SceneGraph>> { scene.renderer }

        ensureDirectoryStructure()

        Paths.PROJECTS_DIR.children().filter { it.value.isDirectory }.forEach { (path, file) ->
            post(ProjectDiscoveryEvent(ProjectHandle(file.name(),file.pathOf())))
        }
        var scene : Scene? = null
        meta = fetchMeta().conf {
            if (lastLoadedProject != null) {
                debug("Reloading last loaded project by configuration - ${lastLoadedProject!!.name}")
                val proj = projectManager.loadProject(lastLoadedProject!!.path, true)
                lastLoadedScene = rebuildIndexes(proj, this)
                if (lastLoadedScene != null)  sceneManager.loadScene(this.lastLoadedScene!!.path,true)
                    .let {
                        scene = it
                        debug("Reloaded last loaded scene by configuration - ${lastLoadedScene!!.name}")
                    }
            } else {
                bootstrapDefaultProject = true
                bootstrapDefaultScene = true
            }
        }.also { meta ->
            if (meta.bootstrapDefaultProject) projectManager.createDefault().also { projectManager.initialize(it) }.also { debug("Bootstrapped default project for fresh configuration") }
            if (meta.bootstrapDefaultScene) sceneManager.createDefault().also {
                scene = it
                sceneManager.initialize(it, true).also { debug("Bootstrapped default project for fresh configuration") } }
        }

        singleton(meta)
        return scene
    }

    fun ensureDirectoryStructure() {

        debug("Ensuring Directory Structure:")
        debug("Salient Home: ${Paths.SALIENT_HOME}")
        debug("Projects Directory: ${Paths.PROJECTS_DIR}")


        if (!Paths.SALIENT_HOME.exists) {
            Paths.SALIENT_HOME.mkdir()
            debug("Created Salient Home Directory at ${Paths.SALIENT_HOME.path}")
        }

        if (!Paths.SALIENT_METAFILE.exists) {
            Paths.SALIENT_METAFILE.mkfile()
            debug("Created Salient Metafile")
        }

        if (!Paths.PROJECTS_INDEX_DIR.exists) {
            Paths.PROJECTS_INDEX_DIR.mkdir()
            debug("Created Salient Projects Index Directory")
        }

        if (!Paths.PROJECTS_DIR.exists) {
            Paths.PROJECTS_DIR.mkdir()
            debug("Created Salient Projects Directory")
        }
        if (!Paths.PROJECT_SCOPE_ASSETS_DIR_FOR("default").exists) {
            Paths.PROJECT_SCOPE_ASSETS_DIR_FOR("default").mkdir()
            debug("Created Default Project Assets Directory")
        }

    }

    fun fetchMeta(): Meta {
        return if (Paths.SALIENT_METAFILE.exists && Paths.SALIENT_METAFILE.size != 0L) Yaml.default.decodeFromString(
            Meta.serializer(),
            Paths.SALIENT_METAFILE.readString
        ) else Meta()
    }

    fun rebuildIndexes(project: Project, appMeta: Meta): SceneHandle? {
        project.path.parent.children().filter { it.value.isDirectory }.forEach { (path, file) ->
            post(ProjectDiscoveryEvent(ProjectHandle(file.name(),file.pathOf())))
        }

        project.path.child("scenes").children().filter { it.value.extension() == "scene" }.forEach { (file, path) ->
            ({ SceneHandle(file.name, file) }.let {
                project.sceneIndex.add(it().also { post(SceneDiscoveryEvent(it)) }.also { debug("Scene discovery: $it ") })
            })
        }


        val refs = mutableListOf<AssetHandle>().apply {
            project.path.child("assets").children()
                .filter { file -> !file.value.isDirectory && (file.value.extension().equals("asset")) }
                .forEach { (file, _) ->
                    ({ AssetHandle(file.path) }.run { add(this()) })
                }
        }
        refs.forEach {
            debug(it.toString())
            Yaml.default.decodeFromString(AssetHandle.serializer(), it.path.readString).let { asset ->
                project.assetIndex.add(asset)
            }
        }

        var mostRecentScene = project.sceneIndex.maxByOrNull { it.path.lastModified }
        mostRecentScene?.let { handle -> debug("Discovered most recent scene: ${handle.name}")}
        return mostRecentScene
    }
}





