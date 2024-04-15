package org.yunghegel.salient.editor.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Align
import ktx.scene2d.textTooltip
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.ui.AppBar
import org.yunghegel.salient.editor.ui.assets.AssetsView
import org.yunghegel.salient.editor.ui.project.ProjectView
import org.yunghegel.salient.editor.ui.scene.SceneManagementView
import org.yunghegel.salient.editor.ui.scene.SceneView
import org.yunghegel.salient.engine.system.perf.Memory
import org.yunghegel.salient.engine.system.inject

import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.widgets.PercentageIndicator
import org.yunghegel.salient.engine.ui.widgets.aux.Console
import org.yunghegel.salient.engine.ui.widgets.aux.LogView
import org.yunghegel.salient.engine.ui.widgets.viewport.ViewportPanel
import org.yunghegel.salient.ui.container.Panel


class Gui : EditorFrame() {


    val viewportWidget : ViewportPanel
    val appBar = AppBar()
    val projectView : ProjectView

    lateinit var projectPanel: Panel
    lateinit var scenePanel: Panel
    lateinit var logPanel : Panel
    lateinit var cliPanel : Panel

    val logView : LogView
    val console : Console
    val sceneTree : SceneView
    val assetsView : AssetsView
    val sceneManagementView : SceneManagementView

    val scene : Scene = inject()
    val salient : Salient = inject()

    init {
        setFillParent(true)

        viewportWidget = ViewportPanel(inject())

        projectView = ProjectView()
        logView = LogView()
        console = Console()
        assetsView = AssetsView()
        sceneTree = SceneView()
        sceneManagementView = SceneManagementView()

    }

    fun configure() {
        menubarSlot.add(appBar.table).growX().colspan(5).row()
        addLeft("project", "Project", projectView)
        addRight("scene_icon", "Scene", sceneTree)
        addCenter("log_view", "Log", logView)
        addCenter("terminal", "Terminal", console)
        addLeft("asset_manager", "Assets", assetsView)
        addRight("config_secondary","Scene Management",sceneManagementView)
        setContent(viewportWidget,scene.name)
        val fpsLabel = SLabel("FPS","default") {
            "${Gdx.graphics.framesPerSecond}"
        }
        addFooterItem(PercentageIndicator(""){ Memory.getPercentage(type =  Memory.Type.GL)}.apply{
            textTooltip("Used: ${trimFloat(Memory.getUsedMemory(Memory.Units.GB, Memory.Type.GL))} / ${trimFloat(
                Memory.getMaxMemory(
                    Memory.Units.GB,
                    Memory.Type.GL))} GB").apply {
                actor.setAlignment(Align.left)
            } }).padHorizontal(10f)


        addFooterItem(fpsLabel).right().padRight(5f).width(30f)

    }



}