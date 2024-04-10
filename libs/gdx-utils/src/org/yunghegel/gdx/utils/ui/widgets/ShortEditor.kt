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

class ShortEditor : FieldEditor {

    override fun create(accessor: Accessor): Table {
        val value = accessor.get() as Short
        return scene2d.table {
            label(accessor.getName()!!) {
                it.left()
                it.padRight(5f)
                it.growX()
            }
            visTextField(value.toString()) { cell ->
                onChange {
                    accessor.set(text.toShort())
                }
                textFieldFilter = IntDigitsOnlyFilter(true)
                cell.growX()
                cell.right()
            }
        }
    }
}
