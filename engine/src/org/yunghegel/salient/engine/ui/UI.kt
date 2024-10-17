package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
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
import org.yunghegel.salient.engine.system.InjectionContext
import ktx.scene2d.Scene2DSkin
import org.yunghegel.gdx.utils.ext.lazyMutable
import org.yunghegel.salient.engine.UIModule
import org.yunghegel.salient.engine.api.properties.Resizable
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
    var notifications : Notifications? = null


    var loaded = false
        private set

     var root: EditorFrame by lazyMutable { EditorFrame() }

    var dnd = DragAndDrop()

    init {
        this.viewport = super.getViewport() as ScreenViewport
    }

    var touchFocusActor : Actor? = null
    var scrollFocusActor : Actor? = null

    override val registry: InjectionContext.() -> Unit = {
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
            offsetY = 25f
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
        addActor(root)
    }

    fun attachNotifications(notifications: Notifications) {
        this.notifications = notifications
        singleton(UI.notifications!!)
    }



    fun drawable(name:String, color : Color? = null) : Drawable {

        val drawable = skin.getDrawable(name)
        if (drawable is NinePatchDrawable && color !=null) return drawable.tint(color)
        return drawable
    }

    fun screenToViewport(x: Int, y: Int): Pair<Float, Float> {
        val proj = root.centerContent.screenToLocalCoordinates(Vector2(x.toFloat(), y.toFloat()))
        return proj.x to proj.y
    }

    fun viewportToScreen(x: Int, y: Int): Pair<Float, Float> {
        val proj = root.centerContent.localToScreenCoordinates(Vector2(x.toFloat(), y.toFloat()))
        return proj.x to proj.y
    }

    private fun loadSkin(path:String): Skin  {
        return FreeTypeSkin(Gdx.files.internal(path))
    }

    override fun resize(width: Int, height: Int) {
        getViewport().update(width, height, true)
    }

    fun addKeyListener(listener: (Int) -> Boolean) {
        Gdx.input.inputProcessor = object : com.badlogic.gdx.InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                return listener(keycode)
            }
        }
    }



    object DialogStage : Stage()  {

    }

}