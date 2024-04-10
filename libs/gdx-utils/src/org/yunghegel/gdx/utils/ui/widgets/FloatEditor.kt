package org.yunghegel.gdx.utils.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import ktx.actors.onChange
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.vis.visTextField
import org.yunghegel.gdx.utils.ext.camelCaseToReadableFormat
import org.yunghegel.gdx.utils.ui.FieldEditor
import org.yunghegel.gdx.utils.reflection.Accessor

class FloatEditor : FieldEditor {



    override fun create(accessor: Accessor): Table {
        return scene2d.table {

            label(camelCaseToReadableFormat(accessor.getName()!!)) {
                it.left()
                it.padRight(5f)
                it.growX()
            }

            var disabled = false

            parseConfig(accessor) {
                disabled = it.readonly
            }

            val value = accessor.get() as Float
            visTextField(value.toString()) {
                onChange {
                    if(validateFloat(text)) accessor.set(text.toFloat())
                }
                textFieldFilter = FloatDigitsOnlyFilter(true)
                it.growX()
                it.right()
            }
        }
    }

    fun validateFloat(string: String): Boolean {
        return string.matches(Regex("[-+]?[0-9]*\\.?[0-9]+"))
    }

}
