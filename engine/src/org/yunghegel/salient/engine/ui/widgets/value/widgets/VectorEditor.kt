package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import ktx.actors.onChange
import ktx.scene2d.scene2d
import ktx.scene2d.table
import org.checkerframework.checker.units.qual.h
import org.yunghegel.gdx.utils.ext.padVertical
import org.yunghegel.gdx.utils.reflection.*
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextField
import org.yunghegel.salient.engine.ui.widgets.value.DefaultFieldEditor
import java.lang.reflect.Field

class VectorEditor : DefaultFieldEditor() {



    override fun createEditable(accessor: FieldAccessor): Actor {
        return when (val value = accessor.get()) {
            is Vector2 -> createVec2(accessor)
            is Vector3 -> createVec3(accessor)
            is Vector4 -> createVec4(accessor)
            else -> throw IllegalArgumentException("Unsupported vector type: ${value.javaClass}")
        }
    }

    private fun createVec2(accessor: FieldAccessor): Table {
        return scene2d.table {

            val xField = field(Vector2::class.java, "x")
            val yField = field(Vector2::class.java, "y")
            val accessorX = FieldAccessor(accessor, xField)
            val accessorY = FieldAccessor(accessor, yField)

            val floatEditorX = FloatEditor()
            add(floatEditorX.create(accessorX))

            val floatEditorY = FloatEditor()
            add(floatEditorY.create(accessorY))

            align(Align.center)

            onChange {
                accessor.set(Vector2(accessorX.get() as Float, accessorY.get() as Float))
            }
        }
    }

    private fun createVec3 (accessor: Accessor): Table {
        return scene2d.table {

            val vec3 = accessor.get() as Vector3
            val xField = getField(vec3,"x",Float::class.java)
            val yField = getField(vec3,"y",Float::class.java)
            val zField = getField(vec3,"z",Float::class.java)

            val horizontalGroup = STable()


            val accessorX = FieldAccessor(vec3, xField)
            val accessorY = FieldAccessor(vec3, yField)
            val accessorZ = FieldAccessor(vec3, zField)

            val floatEditorX = floatEditor(50f,"x",xField,vec3,accessorX)
            val floatEditorY = floatEditor(50f,"y",yField,vec3,accessorY)
            val floatEditorZ = floatEditor(50f,"z",zField,vec3,accessorZ)

            val container = STable()
            container.add(floatEditorX).padVertical(3f)
            container.add(floatEditorY).padVertical(3f)
            container.add(floatEditorZ).padVertical(3f)

            floatEditorX.onChange {
                accessorX.set(floatEditorX.float)
            }
            floatEditorY.onChange { accessorY.set(floatEditorY.float) }
            floatEditorZ.onChange { accessorZ.set(floatEditorZ.float) }


            add(container)








            align(Align.center)

            onChange {
                accessor.set(Vector3(accessorX.get() as Float, accessorY.get() as Float, accessorZ.get() as Float))
            }
        }
    }

    private fun createVec4 (accessor: Accessor): Table {
        return scene2d.table {

            val xField = field(Vector4::class.java, "x")
            val yField = field(Vector4::class.java, "y")
            val zField = field(Vector4::class.java, "z")
            val wField = field(Vector4::class.java, "w")


            val accessorX = FieldAccessor(accessor, xField)
            val accessorY = FieldAccessor(accessor, yField)
            val accessorZ = FieldAccessor(accessor, zField)
            val accessorW = FieldAccessor(accessor, wField)








            align(Align.center)

            onChange {
                accessor.set(Vector4(accessorX.get() as Float, accessorY.get() as Float, accessorZ.get() as Float, accessorW.get() as Float))
            }
        }
    }

    class MaxWidthFloatEditor(val max:Float,val inputLabel: String) : FloatEditor() {
        override fun createEditable(accessor: FieldAccessor): Actor {
            val container = STable()
            val value = accessor.get() as Float
            val input = STextField("$value")
            input.textFieldFilter = TextField.TextFieldFilter { _, c -> c.isDigit() || c == '.' }
            input.onChange {
                val text = input.text
                if (text.isEmpty()) return@onChange
                val newValue = text.toFloat()
                if (newValue > max) {
                    input.text = max.toString()
                    accessor.set(max)
                } else {
                    accessor.set(newValue)
                }
            }

            container.add(input).pad(3f).maxWidth(max)
            return container
        }
    }

    fun floatEditor(max:Float,label: String,field: Field, obj: Any,accessor: FieldAccessor) : LabeledFloatField {
        val actor =  LabeledFloatField(label, max.toInt(),false)
        actor.setTextFieldText(field.get(obj).toString())
        actor.onChange {
            if (!actor.text.isNullOrEmpty()) {
                accessor.set(actor.float)
            }
        }
        return actor
    }
}