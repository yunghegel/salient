package org.yunghegel.salient.engine.ui.layout.panel

import com.badlogic.gdx.scenes.scene2d.Actor
import org.yunghegel.salient.engine.ui.layout.EditorFrame.PanelGroup
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable

class PanelHeader(val icon:String, val title:String) : STable() {

    var hidden = true
    var button : PanelGroup.ToolbarButton? = null
    val overflow: ()->Unit={}

    private val actors = object {
        val iconActor : SImageButton = SImageButton(icon)
        val titleActor = SLabel(title,"bold")
        val overflowButton = SImageButton("overflow-menu")
    }

}
