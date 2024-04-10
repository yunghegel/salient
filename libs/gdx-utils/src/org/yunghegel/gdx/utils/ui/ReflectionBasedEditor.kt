package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTextField
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.gdx.utils.ext.padVertical
import org.yunghegel.gdx.utils.reflection.Editable
import org.yunghegel.gdx.utils.reflection.FieldAccessor


class ReflectionBasedEditor(val obj: Any) : Table() {

    init {
        align(Align.left)
        scan()
    }

    var lastType : Class<*>? = null
    var count = 0

    fun scan() {
        val fields = obj.javaClass.declaredFields
        for (field in fields) {
            val accessible = field.trySetAccessible()
            val accessor = FieldAccessor(obj, field)
            val editor = EditorFactory.create(accessor) ?: error("Unsupported type: ${field.type}");

//            if (lastType != null && lastType != field.type) {
//                row()
//            } else {
//                if (count % 2 == 0) {
//                    row()
//                }
//            }



            add(editor).growX().padRight(15f).left().padVertical(5f).padLeft(4f)
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

            val config = accessor.config(Editable::class.java) ?: continue
//            configurator(editor, config)
            count++
            lastType = field.type
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
