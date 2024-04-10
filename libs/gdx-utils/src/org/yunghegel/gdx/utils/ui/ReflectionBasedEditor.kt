package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTextField
import org.yunghegel.salient.modules.ui.scene2d.STable
import org.yunghegel.salient.common.reflect.Editable
import org.yunghegel.salient.common.reflect.FieldAccessor
import org.yunghegel.salient.modules.io.info

class ReflectionBasedEditor(val obj: Any) : STable() {

    init {
        align(Align.left)
        scan()
    }

    fun scan() {
        val fields = obj.javaClass.declaredFields
        for (field in fields) {
            val accessible = field.trySetAccessible()
            val accessor = FieldAccessor(obj, field)
            val editor = EditorFactory.create(accessor)
            if (editor == null) {
                info("No editor for ${field.name}")
                continue
            }
            if (editor != null) {
                info("Creating editor for ${field.name}")
                add(editor).grow().left().pad(2f)
                row()
                if (editor is Table) {
                    for (child in editor.children) {
                        if (child is Label) {
                            editor.getCell(child).minWidth(35f)
                        }
                        if (child is VisTextField) {
                            editor.getCell(child).maxWidth(70f)
                        }
                        if (child is SelectBox<*>) {
                            editor.getCell(child).maxWidth(70f)
                        }
                    }
                }

            }
            val config = accessor.config(Editable::class.java) ?: continue
            configurator(editor, config)
        }

    }

    val configurator: (Actor?, Editable?) -> Unit = { table, editable ->
        run {
            if (table != null && editable != null) {
                if (editable.readonly) {
                    table.touchable = Touchable.disabled
                }
            }

        }

    }

}
