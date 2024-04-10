package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.yunghegel.salient.common.reflect.Accessor
import org.yunghegel.salient.common.reflect.Editable

interface FieldEditor {

    fun create(accesor: Accessor): Table

    fun parseConfig(accesor: Accessor, parse: (Editable) -> Unit) {
        val config = accesor.config(Editable::class.java)
        if (config != null) {
            parse(config)
        }
    }

}
