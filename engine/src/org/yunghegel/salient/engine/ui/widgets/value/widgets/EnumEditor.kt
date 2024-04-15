package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.ObjectMap
import ktx.actors.onChange
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.selectBox
import org.yunghegel.gdx.utils.ext.pascalCaseToHumanReadable
import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.reflection.Editable
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.salient.engine.ui.widgets.value.DefaultFieldEditor

class EnumWidget<T : Enum<*>?>(enumeration: Class<T>) : DefaultFieldEditor() {

    private val values: ObjectMap<String, T> = ObjectMap<String, T>()

    init {
        for (value in enumeration.enumConstants) {
            var formatted = value!!.name.replace("_", "")
            formatted = pascalCaseToHumanReadable(formatted)

            values.put(formatted, value)
        }
    }

    override fun createLabel(accessor: FieldAccessor, editable: Editable?): Label {
        return scene2d.label(accessor.getName()?.let { it1 ->pascalCaseToHumanReadable(it1) } + ": ")
    }

    override fun createEditable(accessor: FieldAccessor): Actor {
        return scene2d.selectBox<String> {
            val current = findCurrent(accessor)
            setItems(values.keys().toArray())
            items.sort()
            current?.let { setSelected(it) } ?: setSelected(values.keys().firstNotNullOf { it })
            onChange { accessor.set(values.get(selected)) }

        }
    }

    fun findCurrent(accessor: Accessor): String? {
        return values.findKey(accessor.get(), true)
    }
}
