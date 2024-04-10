package org.yunghegel.gdx.utils.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.util.IntDigitsOnlyFilter
import ktx.actors.onChange
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.vis.visTextField
import org.yunghegel.gdx.utils.ui.FieldEditor
import org.yunghegel.gdx.utils.reflection.Accessor

class IntEditor : FieldEditor {

    override fun create(accessor: Accessor): Table {
        return scene2d.table {
            label(accessor.getName()!!) {
                it.left()
                it.padRight(5f)
            }

            val value = accessor.get() as Int
            visTextField(value.toString()) {
                onChange {
                    accessor.set(text.toInt())
                }
                textFieldFilter = IntDigitsOnlyFilter(true)
                it.right()
                it.growX()
            }
        }
    }

}
