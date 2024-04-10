package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.yunghegel.gdx.utils.reflection.Accessor

open class DefaultFieldEditor : FieldEditor {

    override fun create(accessor: Accessor): Table {
        return Table()
    }
}