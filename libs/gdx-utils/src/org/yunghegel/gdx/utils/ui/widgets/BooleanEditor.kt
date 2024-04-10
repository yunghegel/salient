package org.yunghegel.gdx.utils.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.checkBox
import ktx.scene2d.scene2d
import ktx.scene2d.table
import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.ui.FieldEditor

class BooleanEditor : FieldEditor {

    override fun create(accessor: Accessor): Table {
        return scene2d.table {

            checkBox(formatName(accessor.getName()!!)) {
                isChecked = accessor.get() as Boolean
                onChange {
                    accessor.set(isChecked)
                }
                this.labelCell.padLeft(5f)
                it.left()
            }
            align(Align.left)
        }
    }
}
