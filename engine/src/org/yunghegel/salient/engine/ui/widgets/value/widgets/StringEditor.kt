package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.actors.onChange
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visTextField
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.salient.engine.ui.widgets.value.DefaultFieldEditor

class StringEditor : DefaultFieldEditor() {

    override fun createEditable(accessor: FieldAccessor): Actor {
        val value = accessor.get() as String
        return scene2d.visTextField(value) {
            onChange {
                accessor.set(text)
            }
        }
    }

}
