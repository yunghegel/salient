package org.yunghegel.salient.editor.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.textTooltip
import org.yunghegel.gdx.textedit.SimpleTextEditor
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.editor.app.configs.ui.LayoutConfig
import org.yunghegel.salient.editor.app.configs.ui.UIConfig
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.ui.AppBar
import org.yunghegel.salient.editor.ui.ViewportContextMenu
import org.yunghegel.salient.editor.ui.ViewportSplit
import org.yunghegel.salient.editor.ui.assets.AssetsView
import org.yunghegel.salient.editor.ui.project.ProjectView
import org.yunghegel.salient.editor.ui.scene.SceneManagementView
import org.yunghegel.salient.editor.ui.scene.SceneView
import org.yunghegel.salient.engine.system.perf.Memory
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.layout.ConstrainedMultiSplitPane

import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.widgets.PercentageIndicator
import org.yunghegel.salient.engine.ui.widgets.aux.Console
import org.yunghegel.salient.engine.ui.widgets.aux.LogView
import org.yunghegel.salient.engine.ui.widgets.viewport.ViewportPanel
import org.yunghegel.salient.engine.ui.layout.Panel
import org.yunghegel.salient.engine.ui.scene2d.STextButton


class Gui : EditorFrame() {


    val viewportWidget : ViewportPanel

    val appBar = AppBar()
    val projectView : ProjectView

    val logView : LogView
    val console : Console
    val sceneTree : SceneView
    val assetsView : AssetsView
    val sceneManagementView : SceneManagementView


    val textEditor: SimpleTextEditor = SimpleTextEditor(skin,"Shader Editor")

    val scene : Scene = inject()
    val salient : Salient = inject()

    val viewportSplit : ViewportSplit

    init {
        setFillParent(true)

        viewportWidget = ViewportPanel(inject())
        viewportSplit = ViewportSplit(viewportWidget,centerContent)


        val enableGlslEditor = STextButton("Shader Editor","soft-blue").apply {
            onChange {
                if (isChecked) {
                    viewportSplit.append(textEditor)
                } else {
                    viewportSplit.removeLast()
                }
            }
        }
        centerContent.addTitleActor(enableGlslEditor)

        val viewportContextMenu = ViewportContextMenu()

        viewportContextMenu.attachListener(viewportWidget)

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
        setContent(viewportSplit,scene.name)
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

    fun updateviewport() {
        viewportWidget.viewportWidget.updateViewport(false)
    }

    fun restore(config: UIConfig) {
        layout()
        layoutLeft(config.leftLayout)
        layoutRight(config.rightLayout)
        layoutBottom(config.bottomLayout)
    }

    fun export(out: UIConfig) {
        out.leftLayout.hidden = left.hidden
        out.leftLayout.splitAmount = split.cache[left.container] ?: left.prefererred
        out.rightLayout.hidden = right.hidden
        out.rightLayout.splitAmount = split.cache[right.container] ?: right.prefererred
        out.bottomLayout.hidden = center.hidden
        out.bottomLayout.splitAmount = centerSplit.cache[centerSplit] ?: center.prefererred
    }

    fun layoutLeft(layout: LayoutConfig) {
        if (layout.hidden) {
            hide(LEFT)
        } else {
            split.setSplit(0,layout.splitAmount)
        }
    }

    fun layoutRight(layout: LayoutConfig) {
        if (layout.hidden) {
            hide(RIGHT)
        } else {
            split.setSplit(1,layout.splitAmount)
        }
    }

    fun layoutBottom(layout: LayoutConfig) {
        if (layout.hidden) {
            hide(BOTTOM)
        } else {
            centerSplit.setSplit(0,layout.splitAmount)
        }
    }



}