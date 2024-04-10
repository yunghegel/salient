package org.yunghegel.gdx.utils.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Scaling
import ktx.actors.onChange
import ktx.scene2d.dialog
import ktx.scene2d.scene2d
import org.yunghegel.gdx.utils.ext.dialog
import org.yunghegel.gdx.utils.ui.ColorPicker


class ColorBox(
    title: String,
    private val colorModel: ()->Color,
    private val alpha: Boolean,
    skin:Skin
) : Table(skin) {
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
            openDialog(title, stage)
        }
    }

    private fun openDialog(title: String, stage: Stage) {
       val dialog = dialog(createPicker(), title,skin)
        dialog.width = 300f
        dialog.height = 200f
        dialog.show(stage)
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