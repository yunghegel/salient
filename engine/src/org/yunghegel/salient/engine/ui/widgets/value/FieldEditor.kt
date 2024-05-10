package org.yunghegel.salient.engine.ui.widgets.value

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.reflection.Editable
import org.yunghegel.gdx.utils.reflection.FieldAccessor

interface FieldEditor {

    fun create(accessor: FieldAccessor, parser: ((Editable)->Unit)={}): Table

    fun parseConfig(accessor: Accessor, parse: (Editable) -> Unit) : Editable? {
        val config = accessor.config(Editable::class.java)
        if (config != null) {
            parse(config)
        }
        return config
    }



    fun labelDefault(cell: Cell<*>) {
        cell.left()
        cell.width(100f)

    }

    fun actorDefault(cell: Cell<*>) {
        if (cell.actor is TextField) {
            cell.maxWidth(20f)
        }
        cell.right()
    }

    companion object {
        fun formatName(name: String): String {
            var formatted = name
            if (isCamelCase(name)) {
                formatted = camelCaseToReadableFormat(name)
            } else
            if(isSnakeCase(name)) {
                formatted = snakeCaseToHumanReadable(name)
            }
            formatted = formatted.replace("_", " ")
            formatted = capitalizeFirst(formatted)

            return "$formatted:"
        }
    }

}