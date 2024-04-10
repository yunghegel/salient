package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.yunghegel.gdx.utils.ext.camelCaseToReadableFormat
import org.yunghegel.gdx.utils.ext.capitalizeFirst
import org.yunghegel.gdx.utils.ext.isCamelCase
import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.reflection.Editable

interface FieldEditor {

    fun create(accessor: Accessor): Table

    fun parseConfig(accessor: Accessor, parse: (Editable) -> Unit) {
        val config = accessor.config(Editable::class.java)
        if (config != null) {
            parse(config)
        }
    }

    fun formatName(name: String): String {
        var formatted = name
        if (isCamelCase(name)) {
            formatted = camelCaseToReadableFormat(name)
        }
        formatted = formatted.replace("_", " ")
        if(formatted.length==1) {
            formatted = capitalizeFirst(formatted)
        }
        return "$formatted:"

    }

}
