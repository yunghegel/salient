package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.tommyettinger.textra.Font
import com.github.tommyettinger.textra.KnownFonts
import com.kotcrab.vis.ui.VisUI
import com.ray3k.stripe.FreeTypeSkin
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin
import org.yunghegel.salient.engine.UIModule
import org.yunghegel.salient.engine.api.Resizable
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.provide
import org.yunghegel.salient.engine.system.singleton
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.widgets.notif.Notifications

object UI : UIModule(), Resizable {

    val viewport: ScreenViewport
    lateinit var skin : Skin

    lateinit var goNotoUniversal : Font
    lateinit var dejaVuSansMono : Font

    lateinit var font : BitmapFont
    lateinit var mono : BitmapFont
    private lateinit var notifications : Notifications


    var loaded = false
        private set

    lateinit var root: EditorFrame

    var dnd = DragAndDrop()

    init {
        this.viewport = super.getViewport() as ScreenViewport
    }

    var touchFocusActor : Actor? = null
    var scrollFocusActor : Actor? = null

    override val registry: Context.() -> Unit = {
        if(!loaded) init()

        bindSingleton(skin)
        bindSingleton(font)
        info("UI dependencies initialized for use ;")
    }

    fun init() {
        if(loaded) return
        loaded = true
        this.skin = loadSkin("skin/uiskin.json")
        font = skin.getFont("default")
        mono = skin.getFont("mono")
        font.data.markupEnabled = true
        mono.data.markupEnabled = true
        VisUI.load(skin)
        Scene2DSkin.defaultSkin = skin
        TooltipManager.getInstance().apply{
            initialTime = 0.5f
            resetTime = 0.5f
            offsetX = -10f
            offsetY = 10f
        }
        KnownFonts.setAssetPrefix("skin/")

        goNotoUniversal = KnownFonts.getGoNotoUniversal()
        dejaVuSansMono = KnownFonts.getDejaVuSansMono()

        addCaptureListener(object : FocusListener(){
            override fun scrollFocusChanged(event: FocusEvent?, actor: Actor?, focused: Boolean) {
                actor?.let { scrollFocusActor = actor }
                super.scrollFocusChanged(event, actor, focused)
            }

            override fun keyboardFocusChanged(event: FocusEvent?, actor: Actor?, focused: Boolean) {
                actor?.let { touchFocusActor = actor }
                super.keyboardFocusChanged(event, actor, focused)
            }
        })
    }

    fun restoreTouchFocus() {
        setKeyboardFocus(root.centerSplit)
        setScrollFocus(root.centerSplit)
    }

    fun layout(layout: EditorFrame) {
        root = layout
        provide<EditorFrame> {layout}
        notifications = Notifications(this)
        singleton(notifications)

        addActor(root)
    }

    fun drawable(name:String, color : Color? = null) : Drawable {
        val drawable = skin.getDrawable(name)
        if (drawable is NinePatchDrawable && color !=null) return drawable.tint(color)
        return drawable
    }

    fun buildSharedContext() {

    }

    private fun loadSkin(path:String): Skin  {
        return FreeTypeSkin(Gdx.files.internal(path))
    }

    override fun resize(width: Int, height: Int) {
        getViewport().update(width, height, true)
    }





}