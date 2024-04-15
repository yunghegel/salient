package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.salient.engine.ui.widgets.BooleanSelectBox
import org.yunghegel.salient.engine.ui.widgets.value.DefaultFieldEditor

class BooleanEditor : DefaultFieldEditor() {



    override fun createEditable(accessor: FieldAccessor): Actor {

        val box = BooleanSelectBox() { value
            -> accessor.set(value)
        }

        return box
    }
}
