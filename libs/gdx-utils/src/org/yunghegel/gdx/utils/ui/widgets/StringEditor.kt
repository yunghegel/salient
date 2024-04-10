package org.yunghegel.gdx.utils.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.vis.visTextField
import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.ui.FieldEditor

class StringEditor : FieldEditor {

    override fun create(accessor: Accessor): Table {
        return scene2d.table {
            label(accessor.getName()!!) {
                it.left()
                it.padRight(5f)
                it.growX()
            }

            val value = accessor.get() as String
            visTextField(value) {
                onChange {
                    accessor.set(text)
                }
                it.growX()
                it.right()
            }
            align(Align.left)
        }
    }

}
