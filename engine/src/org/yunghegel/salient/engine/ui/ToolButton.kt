package org.yunghegel.salient.engine.ui

import ktx.actors.onChange
import org.yunghegel.salient.engine.api.tool.InputTool
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel

class ToolButton(val icon: String,val tool : Tool) : SImageButton(icon) {

    init {
        setProgrammaticChangeEvents(true)
        onChange {
            if (isChecked) {
                tool.activate()
            } else {
                tool.deactivate()
            }
        }

        addTooltip { add(SLabel(tool.name)).pad(2f) }
    }
}