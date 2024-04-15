package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import ktx.actors.onChange
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visTextField
import org.yunghegel.gdx.utils.ext.validateFloat
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.salient.engine.ui.widgets.value.DefaultFieldEditor

class FloatEditor : DefaultFieldEditor() {



//    override fun create(accessor: FieldAccessor): Table {
//        return scene2d.table {
//
//            label(camelCaseToReadableFormat(accessor.getName()!!)) {
//                labelDefault(it)
//            }
//
//            var disabled = false
//
//            parseConfig(accessor) {
//                disabled = it.readonly
//            }
//
//            val value = accessor.get() as Float
//            visTextField(value.toString()) {
//                onChange {
//                    if(validateFloat(text)) accessor.set(text.toFloat())
//                }
//                textFieldFilter = FloatDigitsOnlyFilter(true)
//                actorDefault(it)
//            }
//        }
//    }

    override fun createEditable(accessor: FieldAccessor): Actor {
        val value = accessor.get() as Float
        return scene2d.visTextField(value.toString()) {
            onChange {
                if(validateFloat(text)) accessor.set(text.toFloat())
            }
            textFieldFilter = FloatDigitsOnlyFilter(true)
        }
    }


}
