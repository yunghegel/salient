package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Scaling
import imgui.extension.imguifiledialog.ImGuiFileDialog.openDialog
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.dialog


class ColorBox(
    title: String = "",
    private val colorModel: ()->Color,
    private val alpha: Boolean,
    skin:Skin,
    width: Float = 16f,
) : Table(skin) {
    private val colorPreview: Image
    private var alphaPreview: Image? = null

    var callback : (Color)->Unit = {}


    /** skin should contains "white pixel"  */
    init {
        val bt = if (title == "") Button(skin) else TextButton(title, skin)

        colorPreview = Image(skin, "white")
        colorPreview.setScaling(Scaling.stretch)

        val height = if (alpha) 16f else 12f

        bt.add(colorPreview).size(width, height)
        bt.row()

        if (alpha) {
            alphaPreview = Image(skin, "white")
            alphaPreview!!.setScaling(Scaling.stretch)
            bt.add(alphaPreview).size(width, 4f)
            bt.row()
        }


        updatePreview()

        add(bt)
        bt.onChange {
            openDialog(title, stage)
        }
    }

    class ColorPreview(skin : Skin, colorModel: ()->Color, width: Float = 16f) : Table(skin) {

        val image: Image
        val alphaImage: Image

        init {
            image = Image(skin, "white")
            image.setScaling(Scaling.stretch)
            add(image).size(width, 12f)
            row()

            alphaImage = Image(skin, "white")
            alphaImage.setScaling(Scaling.stretch)
            add(alphaImage).size(width, 4f)
            row()
        }

    }


    private fun openDialog(title: String, stage: Stage) {
       val dialog = dialog(createPicker(), title,skin)
        dialog.width = 300f
        dialog.height = 200f
        dialog.show(stage)
//        nativeColorInput {
//            onFinished  { colorHex ->
//                val c = Color.valueOf(colorHex)
//                colorModel().set(c)
//                callback(c)
//                updatePreview()
//            }
//        }
    }

    private fun createPicker(): Actor {
        return ColorPicker(colorModel(), alpha, skin) { this.updatePreview(); colorPreview.setColor(it) }
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