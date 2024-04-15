package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.alpha
import org.yunghegel.gdx.utils.ui.ToggleContainer
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.ui.container.Panel

open class CollapsePanel(val title: String, val icon: String?=null, val contentActor: Table?=null) : Panel() {


    var content : Table = STable()
        set(value) {
            content.add(value).expand().fill()
        }
    var contentContainer = ToggleContainer(content)

    var _vis = true

    init {
        content = contentActor ?: STable().apply { add("Empty")}
        contentContainer.actor = content
        pad(8f)
        contentContainer.fill()
        if(icon!=null) {
            createIcon(icon)
        }
        createTitle(title,"")
        val expandCollapse = SImageButton("expand_collapse")
        addButton(expandCollapse)

        add(contentContainer).expand().fill().row()
        expandCollapse.onChange {
            contentContainer.toggle()

//            contentContainer.actor?.isVisible = !contentContainer.actor?.isVisible!!
        }
        titleTable.background = UI.drawable("tab_down",Color(0.5f,0.5f,0.5f,0.25f))
        content.skin= UI.skin
        contentContainer.background = UI.drawable("tab_panel", Color(0.8f,0.8f,0.8f,1f).alpha(0.2f))

    }

    fun toggle() {
        if(_vis) {
            contentContainer.actor = null
        } else {
            contentContainer.actor = content
        }
        _vis = !_vis
    }


}