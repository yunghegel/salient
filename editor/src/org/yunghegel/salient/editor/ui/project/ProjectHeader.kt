package org.yunghegel.salient.editor.ui.project

import com.badlogic.gdx.utils.Align
import org.yunghegel.salient.engine.ui.scene2d.SImageTextButton
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.SImageButton

class ProjectHeader : STable() {

    val projectButton = SImageTextButton("Project","project")
    val refeshButton = SImageButton("refresh")
    val dropDown = SImageButton("dropdown")
    val overflow = SImageButton("overflow-menu")

    val container = STable()

    init {
        setBackground("button-underline")
        container.add(projectButton).pad(2f)
        container.add(dropDown).pad(2f,5f,2f,2f)
        container.align(Align.left)

        add(container).pad(2f).growX()

        add(refeshButton).pad(2f).align(Align.right)
        add(overflow).pad(2f).row()
    }

}