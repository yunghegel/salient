package org.yunghegel.salient.engine.ui.widgets.value

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTextField
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.ext.padVertical
import org.yunghegel.gdx.utils.reflection.*
import org.yunghegel.gdx.utils.ui.LabelSupplier
import org.yunghegel.salient.engine.system.set_property
import org.yunghegel.salient.engine.system.storage.Registry
import org.yunghegel.salient.engine.ui.scene2d.STable
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class ReflectionBasedEditor(val obj: Any,val category: String = "global", var filter: (Field)->Boolean = {false},val fieldFilter: (String)->Boolean= {false}) : Table() {

    private val groups = GdxArray<FieldGroup>()
    private var lastType : Class<*>? = null
    var count = 0

    private val editors = mutableListOf<DefaultFieldEditor>()
    init {
        align(Align.left)
        scan()
    }

    private fun configurator(editor: DefaultFieldEditor,config: Editable) {
        if(config.group != "default") {
            with(editor) {
                val group = groups.find { it.name==config.group }
                if(group ==null) {
                    groups.add(FieldGroup(config.group))
                } else {
                    accessor?.let { accessor ->
                        group.fields += accessor
                    }
                }
            }
        }
        editor.accessor?.group = category
        editor.accessor!!.actor = editor.createEditable(editor.accessor!!).apply{
            userObject = object : LabelSupplier {
                override var label = config.name.ifEmpty { editor.accessor?.label }
            }
        }
        set_property(editor.fieldName,editor.accessor!!)
    }

    private fun scan() {
        val fields = obj.javaClass.declaredFields
        for (field in fields) {
            if (!field.trySetAccessible()) continue

            if (Modifier.isStatic(field.modifiers)) continue


            val identifier = FieldEditor.formatName(field.name)
            val accessor = FieldAccessor(obj, field, identifier)
            if (filter(field)) continue
            if (field.type.toString().contains("Companion") || field.type.toString().contains("KSerializer")) continue
            val editor = EditorFactory.create(accessor)  ?: error("Unsupported type: ${field.type}")

            accessor.config(Editable::class.java)?.let { config ->
                for (i in 0 until editor.children.size) {
                    configurator(editor,config)
                }
                config
            }

            val editorContainer = STable()
            editorContainer.align(Align.center)
            editorContainer.add(editor).growX()
            add(editorContainer).growX().padRight(5f).left().padVertical(5f).padLeft(4f).row()
            editors.add(editor)



            count++
            lastType = field.type
        }

    }

    private fun checkIgnore(field: Field) : Boolean {
        val ignore = field.getAnnotation(Ignore::class.java)
        return (ignore == null || (!EditorFactory.checkType(field.type))|| filter(field) )
    }

    companion object AccessorRegistry : Registry<Accessor>()




}