package org.yunghegel.gdx.utils.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.gdx.utils.ui.ColorPicker
import org.yunghegel.gdx.utils.ui.FieldEditor

class ColorEditor : FieldEditor {

    override fun create(accessor: Accessor): Table {
        val table =Table()
        var color = accessor.get() as Color
        table.add(scene2d.label(formatName(accessor.getName()!!))).left().growX()
        table.add(ColorBox("",{color},true,Scene2DSkin.defaultSkin))
        return table
    }

}