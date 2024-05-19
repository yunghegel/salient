package org.yunghegel.salient.engine.ui

import ktx.actors.onChange
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.ui.scene2d.SImageButton

class ToolButton(val icon: String,val tool : Tool) : SImageButton(icon) {

    init {
        onChange {
            if (isChecked) {
                tool.activate()
            } else {
                tool.deactivate()
            }
        }
    }
}