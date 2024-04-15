package org.yunghegel.salient.engine.ui.widgets.viewport

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ray3k.stripe.PopTable
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ext.toOpenGLCoords
import org.yunghegel.gdx.utils.ext.topRight
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.State
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.SImageButton

class ViewportPanel(val viewport : ScreenViewport) : STable() {

    val stack : Stack = Stack()

    val ui : STable = STable()

    val rowOne : STable = STable()

    val rowTwo : STable = STable()

    val popup : PopTable = PopTable()

    val viewportWidget : ViewportWidget = ViewportWidget(viewport)
    val viewportMenu : ViewportMenu = ViewportMenu()
    val config : SImageButton = SImageButton("config")

    val compass : Compass = Compass(inject())

    val pipeline : Pipeline = inject()


    internal val tools = Tools()


    init {
        ui.align(Align.topLeft)
        touchable = Touchable.enabled
        add(stack).grow()
        stack.add(viewportWidget)
        stack.add(ui)
        configUI()
    }

    fun configUI() {
        ui.add(rowOne).growX().row()
        ui.add(rowTwo).growX().row()

        rowOne.align(Align.topLeft)
        rowTwo.align(Align.topLeft)

        rowOne.add(config).align(Align.topRight).pad(10f).size(20f)
        popup.attachToActor(config, Align.bottomLeft, Align.bottomRight, -10f, -10f)
        config.onClick { if (isChecked) popup.show(stage) else popup.hide() }
        rowOne.add(viewportMenu.table).growX()
        rowTwo.add(tools).growY().left().pad(10f).width(20f)
    }

    fun update() {
        val pos = calculateCompassPosition()
        compass.setPos(pos.x,pos.y)
        compass.update(Gdx.graphics.deltaTime,pos.x,pos.y)
        viewportWidget.updateViewport(false)
    }

    fun calculateCompassPosition() : Vector2 {
        val bounds = viewportWidget.bounds
        val topRight = toOpenGLCoords(bounds.topRight())
        return Vector2(topRight.x - .06f, topRight.y - .065f)
    }
    fun layoutCompass() {

    }
    override fun act(delta: Float) {
        pipeline.once(State.UI_PASS) {
            compass.render(delta = Gdx.graphics.deltaTime)
        }
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {

        super.draw(batch, parentAlpha)

    }

}