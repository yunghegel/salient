package org.yunghegel.salient.editor.app

import com.charleskorn.kaml.Yaml
import ktx.inject.Context
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.gdx.utils.ext.nullOrNotNull
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.AppModule
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.undo.ActionHistory
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.onEditorInitialized
import org.yunghegel.salient.engine.events.scene.SceneDiscoveryEvent
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.scene3d.SceneRenderer
import org.yunghegel.salient.engine.system.*
import org.yunghegel.salient.engine.system.file.Filepath.Companion.pathOf
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.widgets.notif.AlertStrategy
import org.yunghegel.salient.engine.ui.widgets.notif.alert
import org.yunghegel.salient.engine.ui.widgets.notif.notify

typealias engine = Salient.Companion

typealias ui = UI

typealias scene = Scene

typealias stage = UI

typealias pipeline = Salient.Companion

typealias State = Pair<Project, Scene>


class App : AppModule() {

    internal val projectManager: ProjectManager = ProjectManager()
    internal val sceneManager: SceneManager = SceneManager()
    internal val assetManager: AssetManager = AssetManager()
    private val actionHistory = ActionHistory(100)
    private lateinit var meta: Meta

    override val registry: Context.() -> Unit = {
        bindSingleton(actionHistory)
        bindSingleton(projectManager)
        bindSingleton(sceneManager)
        bindSingleton(assetManager)

    }

    internal val project: Project
        get() = inject()

    internal val scene: Scene
        get() = inject()

    init {
        onEditorInitialized {
            Log.processStack()
            alert("Editor Initialized")
            notify("Editor Initialized", strategy =  AlertStrategy.IMMEDIATE)
        }
    }

    fun bootstrap() : State{
        profile("bootstrap app state") {
            provide {
                projectManager.currentProject ?: projectManager.createDefault().also { projectManager.initialize(it) }
            }
            provide { if (projectManager.currentProject != null && projectManager.currentProject?.currentScene != null) projectManager.currentProject?.currentScene!! else sceneManager.createDefault() }
            provide<SceneContext> { scene.context }
            provide<SceneRenderer<Scene, SceneGraph>> { scene.renderer }

            ensureDirectoryStructure()


            var project: Project by notnull()
            var scene: Scene by notnull()

            meta = fetchMeta().conf {
                lastLoadedProject.nullOrNotNull {
                    notNull { lastProject ->
                        profile("load project from file") {
                            project = projectManager.loadProject(lastProject.path).also { projectManager.initialize(it) }
                        }
                        rebuildIndexes(project, this@conf).nullOrNotNull {
                            notNull { last ->
                                profile("load scene from file") {
                                scene = sceneManager.loadScene(last.path, true).also { scene = it }
                            }
                            }
                            isNull {
                                scene = sceneManager.createDefault().also { sceneManager.initialize(it, true) }
                            }
                        }

                    }
                    isNull {
                        project = projectManager.createDefault().also { projectManager.initialize(it) }
                        scene = sceneManager.createDefault().also { sceneManager.initialize(it, true) }
                        sceneManager.saveScene(scene)
                    }
                }
            }

//        meta = fetchMeta().conf {
//            if (lastLoadedProject != null) {
//                debug("Reloading last loaded project by configuration - ${lastLoadedProject!!.name}")
//                project = projectManager.loadProject(lastLoadedProject!!.path)
//                projectManager.initialize(project)
//                rebuildIndexes(project, this).nullOrNotNull {
//                    notNull { last ->
//                        sceneManager.loadScene(last.path,true).also { scene = it }
//                    }
//                    isNull { bootstrapDefaultScene = true }
//                }
////                if (lastLoadedScene != null) scene = sceneManager.loadScene(this.lastLoadedScene!!.path,true)
//            } else {
//                bootstrapDefaultProject = true
//                bootstrapDefaultScene = true
//            }
//        }.also { meta ->
//            if (meta.bootstrapDefaultProject) project =  projectManager.createDefault().also { projectManager.initialize(it) }.also { debug("Bootstrapped default project for fresh configuration") }
//            if (meta.bootstrapDefaultScene) scene = sceneManager.createDefault().also {
//                sceneManager.initialize(it, true).also { debug("Bootstrapped default project for fresh configuration") } }
//        }
            singleton(meta)
        }
        return State(project, scene)
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
        profile("rebuild directory indices") {

            project.path.child("scenes").children.filter { it.value.isDirectory }.forEach {
                it.value.list().filter { it.extension() == "scene" }.forEach { file ->
                    ({ SceneHandle(file.nameWithoutExtension(), file.pathOf()) }.let {
                        project.sceneIndex.add(it().also { post(SceneDiscoveryEvent(it)) }
                            .also { debug("Scene discovery: $it ") })
                    })
                }
            }


            val refs = mutableListOf<AssetHandle>().apply {
                project.path.child("assets").children
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


        }
        var mostRecentScene = project.sceneIndex.maxByOrNull { it.path.lastModified }
        mostRecentScene?.let { handle -> debug("Discovered most recent scene: ${handle.name}") }
        return mostRecentScene
    }


}










