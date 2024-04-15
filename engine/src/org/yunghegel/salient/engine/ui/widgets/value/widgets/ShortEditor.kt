package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.util.IntDigitsOnlyFilter
import ktx.actors.onChange
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visTextField
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.salient.engine.ui.widgets.value.DefaultFieldEditor

class ShortEditor : DefaultFieldEditor() {



    override fun createEditable(accessor: FieldAccessor): Actor {
        val value = accessor.get() as Short
        return scene2d.visTextField(value.toString()) {
            onChange {
                accessor.set(text.toShort())
            }
            textFieldFilter = IntDigitsOnlyFilter(true)
        }
    }
}
