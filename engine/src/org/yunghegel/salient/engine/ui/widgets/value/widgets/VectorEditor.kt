package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.scene2d
import ktx.scene2d.table
import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.gdx.utils.reflection.field
import org.yunghegel.salient.engine.ui.widgets.value.DefaultFieldEditor

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

            val xField = field(Vector3::class.java, "x")
            val yField = field(Vector3::class.java, "y")
            val zField = field(Vector3::class.java, "z")



            val accessorX = FieldAccessor(accessor, xField)
            val accessorY = FieldAccessor(accessor, yField)
            val accessorZ = FieldAccessor(accessor, zField)

            val floatEditorX = FloatEditor()
            add(floatEditorX.create(accessorX))

            val floatEditorY = FloatEditor()
            add(floatEditorY.create(accessorY))

            val floatEditorZ = FloatEditor()
            add(floatEditorZ.create(accessorZ))

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

            val floatEditorX = FloatEditor()
            add(floatEditorX.create(accessorX))

            val floatEditorY = FloatEditor()
            add(floatEditorY.create(accessorY))

            val floatEditorZ = FloatEditor()
            add(floatEditorZ.create(accessorZ))

            val floatEditorW = FloatEditor()
            add(floatEditorW.create(accessorW))

            align(Align.center)

            onChange {
                accessor.set(Vector4(accessorX.get() as Float, accessorY.get() as Float, accessorZ.get() as Float, accessorW.get() as Float))
            }
        }
    }
}