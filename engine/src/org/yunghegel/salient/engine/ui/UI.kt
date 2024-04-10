package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import com.ray3k.stripe.FreeTypeSkin
import ktx.scene2d.Scene2DSkin
import org.yunghegel.salient.engine.api.Resizable
import org.yunghegel.salient.engine.io.singleton
import org.yunghegel.salient.engine.io.info
import org.yunghegel.salient.engine.io.provide
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.widgets.notif.Notifications

object UI : Stage(ScreenViewport()), Resizable {

    val viewport: ScreenViewport
    lateinit var skin : Skin
    lateinit var defaultFont : BitmapFont
    lateinit var notifications : Notifications


    var loaded = false
        private set

    lateinit var root: EditorFrame

    var dnd = DragAndDrop()

    init {
        this.viewport = super.getViewport() as ScreenViewport
    }

    fun init() {
        if(loaded) return
        loaded = true
        this.skin = loadSkin("skin/uiskin.json")
        defaultFont = skin.getFont("default")
        VisUI.load(skin)


        Scene2DSkin.defaultSkin = skin
        TooltipManager.getInstance().apply{
            initialTime = 0.5f
            resetTime = 0.5f
            offsetX = -10f
            offsetY = 10f
        }


    }

    fun layout(layout: EditorFrame) {
        root = layout
        provide<EditorFrame> {layout}
        notifications = Notifications(this)
        singleton(notifications)

        addActor(root)
    }

    fun configure(conf: EditorFrame.()->Unit) {
        root.conf()
    }

    fun buildSharedContext() {
        if(!loaded) init()

        singleton(skin)
        singleton(defaultFont)

        info("UI dependencies initialized for use ;")
    }

    fun loadSkin(path:String): Skin  {
        return FreeTypeSkin(Gdx.files.internal(path))
    }

    override fun resize(width: Int, height: Int) {
        getViewport().update(width, height, true)
    }


}