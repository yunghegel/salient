package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.scene2d.Scene2DSkin
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.gdx.utils.ui.ColorBox
import org.yunghegel.salient.engine.ui.widgets.value.DefaultFieldEditor

class ColorEditor : DefaultFieldEditor() {

    override fun createEditable(accessor: FieldAccessor): Actor {
        return ColorBox("",{accessor.get() as Color},true,Scene2DSkin.defaultSkin)
    }

}