package org.yunghegel.salient.engine.ui.widgets.viewport

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import ktx.scene2d.*


internal fun button(icon: String, tip: String) = scene2d {
    imageButton(icon) {
        textTooltip(text = tip) {
            setAlignment(Align.right)
        }
    }
}

    internal class Tools : VerticalGroup() {

        val translateButton = button("translate", "Translate")
        val rotateButton = button("rotate", "Rotate")
        val scaleButton = button("scale", "Scale")
        val selectButton = button("select", "Select")
        val toolGroup = ButtonGroup<KImageButton>()

        init {
            addActor(translateButton)
            addActor(rotateButton)
            addActor(scaleButton)
            addActor(selectButton)
            space(5f)
            toolGroup.add(translateButton)
            toolGroup.add(selectButton)
            toolGroup.add(rotateButton)
            toolGroup.add(scaleButton)
            selectButton.isChecked = true
            toolGroup.setMinCheckCount(0)

        }





}