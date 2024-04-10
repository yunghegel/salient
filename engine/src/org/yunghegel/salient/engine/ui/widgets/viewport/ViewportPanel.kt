package org.yunghegel.salient.engine.ui.widgets.viewport

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ray3k.stripe.PopTable
import ktx.actors.onClick
import org.yunghegel.salient.engine.sys.inject
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.ui.scene2d.SImageButton

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
        compass.update(Gdx.graphics.deltaTime,width,height)
        viewportWidget.updateViewport(false)
        compass.render(delta = Gdx.graphics.deltaTime)

    }

    override fun act(delta: Float) {


        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {

        super.draw(batch, parentAlpha)

    }

}