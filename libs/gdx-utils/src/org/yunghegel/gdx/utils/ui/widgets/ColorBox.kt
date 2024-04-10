package org.yunghegel.gdx.utils.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Scaling
import ktx.actors.onChange
import org.yunghegel.gdx.ui.widgets.ColorPicker
import org.yunghegel.gdx.utils.ui.scene2d.STable


class ColorBox(
    title: String?,
    private val colorModel: ()->Color,
    private val alpha: Boolean,
) : STable() {
    private val colorPreview: Image
    private var alphaPreview: Image? = null



    /** skin should contains "white pixel"  */
    init {
        val bt = Button(skin)

        colorPreview = Image(skin, "white")
        colorPreview.setScaling(Scaling.stretch)
        bt.add(colorPreview).size(16f, 12f)
        bt.row()

        if (alpha) {
            alphaPreview = Image(skin, "white")
            alphaPreview!!.setScaling(Scaling.stretch)
            bt.add(alphaPreview).size(16f, 4f)
            bt.row()
        }


        updatePreview()

        add(bt)
        bt.onChange {
            openDialog(title, UI.stage)
        }
    }

    private fun openDialog(title: String?, stage: Stage) {
       dialog(createPicker(), title).show(stage)
    }

    private fun createPicker(): Actor {
        return ColorPicker(colorModel(), alpha, getSkin()) { this.updatePreview() }
    }

    private fun updatePreview() {
        val c = colorModel()
        colorPreview.setColor(c.r, c.g, c.b, 1f)
        if (alpha) {
            alphaPreview!!.setColor(c.a, c.a, c.a, 1f)
        }
        fire(ChangeListener.ChangeEvent())
    }
}