package org.yunghegel.salient.engine.ui.widgets.viewport

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ray3k.stripe.PopTable
import org.yunghegel.gdx.utils.ext.toOpenGLCoords
import org.yunghegel.gdx.utils.ext.topRight
import org.yunghegel.salient.engine.GraphicsModule
import org.yunghegel.salient.engine.system.Netgraph
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.STable

class ViewportPanel(val viewport : ScreenViewport) : STable() {



    val stack : Stack = Stack()

    val ui : STable = STable()

    val rowOne : STable = STable()
    val rowOneLeft : STable = STable()
    val rowOneRight : STable = STable()

    val rowTwo : STable = STable()
    val rowTwoLeft : STable = STable()
    val rowTwoRight : STable = STable()

    val rowThree : STable = STable()
    val rowThreeLeft : STable = STable()
    val rowThreeRight : STable = STable()


    val popup : PopTable = PopTable()

    val viewportWidget : ViewportWidget = ViewportWidget(viewport)
    val viewportMenu : ViewportMenu = ViewportMenu()
    val config : SImageButton = SImageButton("config")

    val compass : Compass = Compass(inject())

    val gfxModule : GraphicsModule = inject()


    val tools = Tools()


    init {
        ui.align(Align.topLeft)
        touchable = Touchable.enabled
        add(stack).grow()
        stack.add(viewportWidget)
        stack.add(ui)
        configUI()

        Netgraph.add("Viewport Dimensions") {
            "${viewport.screenX}  ${viewport.screenY} ${viewport.screenWidth}  ${viewport.screenHeight}"
        }



    }

    fun configUI() {
        rowOneLeft.align(Align.topLeft)
        rowOneRight.align(Align.topRight)

        rowTwoLeft.align(Align.left)
        rowTwoRight.align(Align.right)

        rowThreeLeft.align(Align.bottomLeft)
        rowThreeRight.align(Align.bottomRight)
        rowOne.add(rowOneLeft).grow()
        rowOne.add(rowOneRight).grow()

        rowTwo.add(rowTwoLeft).grow()
        rowTwo.add(rowTwoRight).grow()

        rowThree.add(rowThreeLeft).grow()
        rowThree.add(rowThreeRight).grow()

        ui.add(rowOne).grow().row()
        ui.add(rowTwo).grow().row()
        ui.add(rowThree).grow().row()

//        rowOne.align(Align.topLeft)
//        rowTwo.align(Align.left)
//
        rowThree.align(Align.bottomRight)
        rowThreeRight.apply {
//            Netgraph.listen { key, value ->
//                val label = label("$key : ${value()}",skin = skin, style = "default-small") {
//                    "$key : ${value()}"
//                }.apply {
//                    align(Align.right)
//                }
//                add(label).row()
//            }
        }
//
        rowOneLeft.add(config).align(Align.topLeft).pad(10f).size(20f)
//        popup.attachToActor(config, Align.bottomLeft, Align.bottomRight, -10f, -10f)
//        config.onClick { if (isChecked) popup.show(stage) else popup.hide() }
        rowOneLeft.add(viewportMenu.table).growX()
        rowTwoLeft.add(tools).growY().left().pad(10f).width(20f)

        addListener(compass)
    }

    fun update() {
        viewportWidget.updateViewport(false)
    }

    fun updateCompass() {
        val pos = calculateCompassPosition()
        compass.setPos(pos.x,pos.y)
        compass.update(Gdx.graphics.deltaTime,pos.x,pos.y)
    }

    fun drawCompass(delta: Float) {
        compass.render(delta)
    }

    fun calculateCompassPosition() : Vector2 {
        val bounds = viewportWidget.bounds
        val topRight = toOpenGLCoords(bounds.topRight())
        return Vector2(topRight.x - .06f, topRight.y - .065f)
    }
    fun layoutCompass() {

    }
    override fun act(delta: Float) {
//        pipeline.once(State.UI_PASS) {
//            compass.render(delta = Gdx.graphics.deltaTime)
//        }
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }

}

