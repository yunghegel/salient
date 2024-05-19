package org.yunghegel.salient.engine.ui.widgets.viewport

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import ktx.scene2d.*
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.ui.ToolButton


internal fun button(icon: String, tip: String) = scene2d {
    imageButton(icon) {
        textTooltip(text = tip) {
            setAlignment(Align.right)
        }
    }
}

fun toolButton(icon:String,tool: Tool) : ToolButton {
    val button = ToolButton(icon,tool)
    button.setSize(32f,32f)
    return button
}

class Tools : VerticalGroup() {

//    val translateButton = button("translate", "Translate")
//    val rotateButton = button("rotate", "Rotate")
//    val scaleButton = button("scale", "Scale")
//    val selectButton = button("select", "Select")
    val toolGroup = ButtonGroup<ToolButton>()

    fun createTool(icon:String,tool: Tool) {
        val button = toolButton(icon,tool)
        addActor(button)
        toolGroup.add(button)
    }

    init {
//        addActor(translateButton)
//        addActor(rotateButton)
//        addActor(scaleButton)
//        addActor(selectButton)
        space(5f)
//        toolGroup.add(translateButton)
//        toolGroup.add(selectButton)
//        toolGroup.add(rotateButton)
//        toolGroup.add(scaleButton)
//        selectButton.isChecked = true
        toolGroup.setMinCheckCount(0)
        }
}