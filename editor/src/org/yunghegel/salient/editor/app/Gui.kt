package org.yunghegel.salient.editor.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.actors.onEnter
import ktx.actors.onExit
import ktx.scene2d.textTooltip
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute
import org.yunghegel.gdx.utils.TypedPayload
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.editor.app.configs.ui.LayoutConfig
import org.yunghegel.salient.editor.app.configs.ui.UIConfig
import org.yunghegel.salient.editor.plugins.base.systems.pauseHotkeys
import org.yunghegel.salient.editor.plugins.base.systems.resumeHotkeys
import org.yunghegel.salient.editor.plugins.gizmos.tools.PlacementTool
import org.yunghegel.salient.editor.plugins.picking.tools.HoverTool
import org.yunghegel.salient.editor.scene.ObjectFactory
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.ui.AppBar
import org.yunghegel.salient.editor.ui.ViewportContextMenu
import org.yunghegel.salient.editor.ui.ViewportSplit
import org.yunghegel.salient.editor.ui.assets.AssetsView
import org.yunghegel.salient.editor.ui.assets.browser.AssetBrowser
import org.yunghegel.salient.editor.ui.project.ProjectControls
import org.yunghegel.salient.editor.ui.project.panel.NotificationsView
import org.yunghegel.salient.editor.ui.project.panel.ProjectView
import org.yunghegel.salient.editor.ui.scene.SceneManagementView
import org.yunghegel.salient.editor.ui.scene.SceneView
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.asset.type.TextureAsset
import org.yunghegel.salient.engine.api.undo.action
import org.yunghegel.salient.engine.graphics.shapes.BuilderUtils
import org.yunghegel.salient.engine.graphics.shapes.Primitive
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.PickableComponent
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.perf.Memory
import org.yunghegel.salient.engine.ui.DndTarget
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.ui.widgets.PercentageIndicator
import org.yunghegel.salient.engine.ui.widgets.SimpleTextEditor
import org.yunghegel.salient.engine.ui.widgets.aux.Console
import org.yunghegel.salient.engine.ui.widgets.aux.LogView
import org.yunghegel.salient.engine.ui.widgets.notif.Notifications
import org.yunghegel.salient.engine.ui.widgets.notif.toast
import org.yunghegel.salient.engine.ui.widgets.viewport.ViewportPanel


@OptIn(ExperimentalStdlibApi::class)
class Gui : EditorFrame() {


    val viewportWidget: ViewportPanel

    val appBar = AppBar()
    val projectView: ProjectView

    val logView: LogView
    val console: Console
    val sceneTree: SceneView
    val assetsView: AssetsView
    val sceneManagementView: SceneManagementView
    val assetBrowser: AssetBrowser
    val notifications: Notifications
    val notificationsView: NotificationsView


    val textEditor: SimpleTextEditor = SimpleTextEditor(skin, "Shader Editor")

    val scene: Scene by lazy { inject() }
    val salient: Salient = inject()

    val viewportSplit: ViewportSplit

    init {
        setFillParent(true)

        viewportWidget = ViewportPanel(inject())
        viewportWidget.viewportMenu.apply {
            Primitive.entries.forEach { prim ->
                add.define(prim.name, prim.icon,
                    toast(title = "Add Primitive", time = -1f) {
                        row {
                            prim.integerParams.forEach { (key,value) ->
                                intInput(key,value) {
                                    result[key] = it
                                }
                            }
                        }
                        row {
                            prim.floatParmas.filter{ it.key !in (listOf("r","g","b","a"))}.forEach { (key,value) ->
                                floatInput(key,value) {
                                    result[key] = it
                                }
                                row()
                            }
                        }
                        row {
                            val color = Array(4) { 0.5f }
                            color("Color", BuilderUtils.getRandomColor()) {
                                result["color"] = it
                            }
                        }




                        addSubmit { r->
                            val params = prim.defaultParams
                            r.forEach { item ->
                                val (key, value) = item
                                when (value) {
                                    is Int -> params.integerParams[key] = value
                                    is Float -> params.floatParmas[key] = value
                                    is Color -> {
                                        val c = value
                                        params.floatParmas["r"] = c.r
                                        params.floatParmas["g"] = c.g
                                        params.floatParmas["b"] = c.b
                                        params.floatParmas["a"] = c.a
                                    }
                                }

                            }
                            println(params)
                            ObjectFactory.createPrimitive(prim,scene,params)
                        }
                    }
                , { r ->
                        r.forEach { (key, value) ->
                            println("$key : $value")
                        }

                    })
            }
        }


        viewportWidget.onExit {
            pauseHotkeys()
        }
        viewportWidget.onEnter {
            resumeHotkeys()
            stage.keyboardFocus=viewportWidget
            stage.scrollFocus=viewportWidget
            
        }
        viewportSplit = ViewportSplit(viewportWidget, centerContent)

        notifications = Notifications(UI, viewportWidget)
        notificationsView = NotificationsView(notifications)
        UI.attachNotifications(notifications)
        val enableGlslEditor = STextButton("Shader Editor", "soft-blue").apply {
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
        assetBrowser = AssetBrowser()



        with(viewportWidget) {
            val hover: HoverTool by lazy { inject() }
            val placement: PlacementTool by lazy { inject() }
            hookAssetDrop {
                var target: GameObject? = null

                shouldAccept { pl ->
                    if (pl.`object` is ModelAsset || pl.`object` is TextureAsset) {

                        true
                    } else false
                }
                handleDrop { asset ->
                    info("dropping ${asset.assetType} on ${target?.name ?: "null"}")
                    when (asset) {
                        is TextureAsset -> {
                            target?.let { gameObject ->
                                val model = gameObject.get(RenderableComponent::class)!!.value as ModelInstance
                                toast(title = "Modify Texture") {
                                    choice(
                                        "Atrribute",
                                        listOf("Diffuse", "Normal", "Specular", "Ambient", "Reflection"),
                                        "Diffuse"
                                    ) { selected ->
                                        result["Atrribute"] = selected
                                    }.pad(4f).colspan(2).row()

                                    withResult { result ->
                                        val kind = result["Atrribute"] as String

                                        val attr = pbrTexture(kind, asset.value!!)
                                        println(kind)
                                        model.materials.forEach { material ->
                                            action {
                                                name = "Apply $kind texture from [${asset.handle.name}"
                                                doAction = {
                                                    userObject = material.pbrGet(kind, asset.value!!)
                                                    material.set(attr)
                                                }
                                                undoAction = {
                                                    material.set(userObject as PBRTextureAttribute)
                                                }
                                            }
                                        }
                                    }
                                    addSubmit()
                                }
                            }
                        }
                        is ModelAsset -> {
                            placement.placeNewGameObject()
                        }
                    }
                }
                handleDrag { payload, x, y ->
                    val screen = localToScreenCoordinates(Vector2(x.toFloat(), y.toFloat()))
                    hover.query(false, screen.x.toInt(), screen.y.toInt()) { pickable ->
                        if (pickable != null) {
                            if (pickable is PickableComponent) {
                                target = pickable.go
                            }
                        } else {
                            target = null
                        }
                    }
                }
            }
        }

        assetBrowser.hookDropTarget(viewportWidget, { payload ->
            val asset = payload.`object`
            if (asset is Asset<*>) {
                when (asset) {
                    is ModelAsset -> true
                    else -> false
                }
            }
            false
        }, { payload ->
            payload?.let {
                when (it.`object` as Asset<*>?) {
                    is ModelAsset -> {
                        println("Adding object to scene")
                    }
                }
            }
        })

    }

    fun configure() {
        menubarSlot.add(appBar.table).growX().colspan(5).row()
        val projControls =ProjectControls()
        left.customizeHeader { table, panelContent ->
            table.add(projControls).right().growX().height(20f)
        }
        addLeft("project", "Project", projectView)
//        addRight("notifications","",notificationsView)
        addRight("scene_icon", "Scene", sceneTree)
        addCenter("log_view", "Log", logView)
        addCenter("terminal", "Terminal", console)
        addLeft("asset_manager", "Assets", assetsView)
        addRight("config_secondary", "Scene Management", sceneManagementView)
        addCenter("asset_browser", "Asset Browser", assetBrowser)
        setContent(viewportSplit, scene.name)
        val fpsLabel = SLabel("FPS", "default") {
            "${Gdx.graphics.framesPerSecond}"
        }
        addFooterItem(PercentageIndicator("") { Memory.getPercentage(type = Memory.Type.GL) }.apply {
            textTooltip(
                "Used: ${trimFloat(Memory.getUsedMemory(Memory.Units.GB, Memory.Type.GL))} / ${
                    trimFloat(
                        Memory.getMaxMemory(
                            Memory.Units.GB,
                            Memory.Type.GL
                        )
                    )
                } GB"
            ).apply {
                actor.setAlignment(Align.left)
            }
        }).padHorizontal(10f)


        addFooterItem(fpsLabel).right().padRight(5f).width(30f)


        addFooterItem(notifications.button).padHorizontal(4f).center().size(40f,18f)

    }

    fun updateviewport() {
        viewportWidget.viewportWidget.updateViewport(false)
    }

    fun <T : Actor> T.hookAssetDrop(conf: DndTarget.Builder<Asset<*>>.() -> Unit) {
        val builder = DndTarget.Builder<Asset<*>>(this)
        builder.conf()
        val res = builder.build()
        assetBrowser.dnd.addTarget(object : DragAndDrop.Target(this) {
            override fun drag(
                p0: DragAndDrop.Source?,
                p1: DragAndDrop.Payload?,
                p2: Float,
                p3: Float,
                p4: Int
            ): Boolean {
                res.handleDrag(p1 as TypedPayload<Asset<*>>, p2.toInt(), p3.toInt())
                if (p1 != null) return res.shouldAccept(p1) else return false
            }

            override fun drop(p0: DragAndDrop.Source?, p1: DragAndDrop.Payload?, p2: Float, p3: Float, p4: Int) {
                if (p1 != null) res.handleDrop(p1.`object` as Asset<*>)
            }

            override fun reset(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?) {
                super.reset(source, payload)
            }
        })

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
            split.setSplit(0, layout.splitAmount)
        }
    }

    fun layoutRight(layout: LayoutConfig) {
        if (layout.hidden) {
            hide(RIGHT)
        } else {
            split.setSplit(1, layout.splitAmount)
        }
    }

    fun layoutBottom(layout: LayoutConfig) {
        if (layout.hidden) {
            hide(BOTTOM)
        } else {
            centerSplit.setSplit(0, layout.splitAmount)
        }
    }


}