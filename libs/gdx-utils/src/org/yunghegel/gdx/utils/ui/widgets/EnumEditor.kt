package org.yunghegel.gdx.utils.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ObjectMap
import ktx.actors.onChange
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.selectBox
import ktx.scene2d.table
import org.yunghegel.gdx.utils.ext.pascalCaseToHumanReadable
import org.yunghegel.gdx.utils.ui.FieldEditor
import org.yunghegel.gdx.utils.reflection.Accessor

class EnumWidget<T : Enum<*>?>(enumeration: Class<T>) : FieldEditor {

    private val values: ObjectMap<String, T> = ObjectMap<String, T>()

    init {
        for (value in enumeration.enumConstants) {
            var formatted = value!!.name.replace("_", "")
            formatted = pascalCaseToHumanReadable(formatted)

            values.put(formatted, value)
        }
    }

    // TODO refactor as SelectorWidget ...
    override fun create(accessor: Accessor): Table {

        val current = findCurrent(accessor)
        return scene2d.table {
            val name = accessor.getName()!!.lowercase()

            label(accessor.getName()?.let { it1 ->pascalCaseToHumanReadable(it1) } + ": ") {
                it.left()
                it.padRight(5f)
                it.growX()

            }

            selectBox<String> {
                setItems(values.keys().toArray())
                items.sort()
                current?.let { setSelected(it) } ?: setSelected(values.keys().firstNotNullOf { it })
                onChange { accessor.set(values.get(selected)) }

            }
        }
    }

    fun findCurrent(accessor: Accessor): String? {
        return values.findKey(accessor.get(), true)
    }
}
