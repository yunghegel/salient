package org.yunghegel.salient.editor.app

import com.charleskorn.kaml.Yaml
import ktx.reflect.Reflection
import org.yunghegel.gdx.utils.ext.inc
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.gdx.utils.ext.nullOrNotNull
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.editor.scene.ObjectFactory.assetManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.*
import org.yunghegel.salient.engine.api.EditorProjectManager
import org.yunghegel.salient.engine.api.Meta
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.undo.ActionHistory
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.ProjectDiscoveryEvent
import org.yunghegel.salient.engine.events.lifecycle.onEditorInitialized
import org.yunghegel.salient.engine.events.scene.SceneDiscoveryEvent
import org.yunghegel.salient.engine.helpers.encodestring
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
    lateinit var meta: Meta

    lateinit var index: MutableMap<ProjectHandle, List<SceneHandle>>

    @OptIn(Reflection::class)
    override val registry: InjectionContext.() -> Unit = {
        bindSingleton(actionHistory)
        this.bind<EditorProjectManager<*,*>>(EditorProjectManager::class, ProjectManager::class){ projectManager }
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
            InjectionContext.bind(EditorProject::class, Project::class) {
                projectManager.currentProject ?: projectManager.createDefault().also { projectManager.initialize(it) }
            }
            provide { if (projectManager.currentProject != null && projectManager.currentProject?.currentScene != null) projectManager.currentProject?.currentScene!! else sceneManager.createDefault() }


            ensureDirectoryStructure()
            state++

            var project: Project by notnull()
            var scene: Scene by notnull()

            index = projectIndices().associateBy({ it }, { sceneIndices(it) }).toMutableMap()




            meta = fetchMeta().apply {
                debug(this.toString())

            }.conf {
                lastLoadedProject.nullOrNotNull {
                    notNull { lastProject ->
                        profile("load project from file") {
                            project = projectManager.loadProject(lastProject.file).also { projectManager.initialize(it) }
                        }
                        rebuildIndexes(project, this@conf).nullOrNotNull {
                            notNull { last ->
                                profile("load scene from file") {
                                scene = sceneManager.loadScene(last.file, true).also { sceneManager.initialize(it,true) }
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

    private fun ensureDirectoryStructure() {
        state = VALIDATE_DIRECTORIES action {
            debug("Ensuring Directory Structure:")
            debug("Salient Home: ${Paths.SALIENT_HOME}")
            debug("Projects Directory: ${Paths.PROJECTS_DIR}")


            if (!Paths.SALIENT_HOME.exists) {
                Paths.SALIENT_HOME.mkdir()
                debug("Created Salient Home Directory at ${Paths.SALIENT_HOME.path}")
            }



            if (!Paths.PROJECTS_INDEX_DIR.exists) {
                Paths.PROJECTS_INDEX_DIR.mkdir()
                debug("Created Salient Projects Index Directory")
            }

            if (!Paths.PROJECTS_DIR.exists) {
                Paths.PROJECTS_DIR.mkdir()
                debug("Created Salient Projects Directory")
            }
            if (!Paths.PROJECT_ASSET_INDEX_FOR("default").exists) {
                Paths.PROJECT_ASSET_INDEX_FOR("default").mkdir()
                debug("Created Default Project Assets Directory")
            }
            true
        }


    }

    fun fetchMeta(): Meta {
        return if (Paths.SALIENT_METAFILE.exists) Yaml.default.decodeFromString(
            Meta.serializer(),
            Paths.SALIENT_METAFILE.readString
        ) else Meta().apply { org.yunghegel.salient.engine.helpers.save(Paths.SALIENT_METAFILE.path) { encodestring(this)} }
    }

    fun projectIndices() : List<ProjectHandle> {
        state = DISCOVER_PROJECTS
        val projects = mutableListOf<ProjectHandle>()
        profile("discover projects") {
            Paths.PROJECTS_DIR.children.forEach { t, u ->
                if (u.isDirectory) {
                    val projHandle : ProjectHandle = ProjectHandle(t.name, t)
                    projects.add(projHandle)
                    post(ProjectDiscoveryEvent(projHandle))
                }
            }
            }
            return projects
        }

    fun sceneIndices(project: ProjectHandle): List<SceneHandle> {
        state = PROJECT_SCENE_INDEX_DISCOVERY
        val scenes = mutableListOf<SceneHandle>()
        profile("discover scenes") {
            project.file.child("scenes").children.filter { it.value.isDirectory }.forEach {
                it.value.list().filter { it.extension() == "scene" }.forEach { file ->
                    ({ SceneHandle(file.nameWithoutExtension(), file.pathOf()) }.let {
                        scenes.add(it().also { post(SceneDiscoveryEvent(it)) }
                            .also { debug("Scene discovery: $it ") })
                    })
                }
            }
        }
        return scenes
    }



    fun rebuildIndexes(project: Project, appMeta: Meta): SceneHandle? {
        profile("rebuild directory indices") {

            state = PROJECT_SCENE_INDEX_DISCOVERY doing {
                project.file.child("scenes").children.filter { it.value.isDirectory }.forEach {
                    it.value.list().filter { it.extension() == "scene" }.forEach { file ->
                        ({ SceneHandle(file.nameWithoutExtension(), file.pathOf()) }.let {
                            project.sceneIndex.add(it().also { post(SceneDiscoveryEvent(it)) }
                                .also { debug("Scene discovery: $it ") })
                        })
                    }
                }
            }

            val refs = mutableListOf<AssetHandle>()

            refs.addAll(assetManager.loadProjectIndex(project))

//                refs.apply {
//                    project.file.child("assets").children
//                        .filter { file -> !file.value.isDirectory && (file.value.extension().equals("asset")) }
//                        .forEach { (file, _) ->
//                            ({ AssetHandle(file.path) }.run { add(this()) })
//                        }
//                }
//                refs.forEach {
//                    debug("Asset Discovery: ${it.name}")
//                    Yaml.default.decodeFromString(AssetHandle.serializer(), it.file.readString).let { asset ->
//                        assetManager.indexHandle(asset,project)
//                    }
//                }



        }
        var mostRecentScene = project.sceneIndex.maxByOrNull { it.file.lastModified }
        mostRecentScene?.let { handle -> debug("Discovered most recent scene: ${handle.name}") }
        return mostRecentScene
    }


}










