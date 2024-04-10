package org.yunghegel.salient.editor.ui.scene.inspector

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.textTooltip
import ktx.scene2d.tooltip
import ktx.scene2d.vis.visTooltip
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.gdx.utils.ext.padVertical
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.pop
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel

class InspectorSidebar(private val container: STable) : STable() {

    var current : Actor? = null
        set(value) {
            field = value
            container.clearChildren()
            if(value != null) {
                container.add(value).grow().row()
            }
        }

    val buttonGroup = ButtonGroup<SidebarButton>()

    init {
        pad(2f)
        align(Align.top)
        setBackground("background-pixel")
        buttonGroup.setMinCheckCount(1)
        buttonGroup.setMaxCheckCount(1)

    }


    fun addOption(icon:String,actor:BaseInspector) {
        val button = SidebarButton(icon,actor)
        add(button).size(20f).pad(3f).top().row()
        buttonGroup.add(button)

    }

    inner class SidebarButton(val icon:String,val actor:BaseInspector) : SImageButton(icon) {

        fun insertSpaceBeforeUpperCase(s: String): String {
            return s.replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")
        }

        init {
            style.up = UI.skin.getDrawable("button-rounded-edge-over")
            style.checked = UI.skin.getDrawable("button-rounded-edge-blue")
            setProgrammaticChangeEvents(true)
            onChange {
                current = actor
            }
            tooltip("scroll") {
                add(SLabel(insertSpaceBeforeUpperCase(actor::class.simpleName!!))).pad(4f)



            }
        }
    }


}