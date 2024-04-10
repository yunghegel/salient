package org.yunghegel.gdx.utils.ui.widgets

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.vis.visTextField
import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.gdx.utils.ui.FieldEditor

class VectorEditor : FieldEditor {

    override fun create(accessor: Accessor): Table {
        val value = accessor.get()
        return when (value) {
            is Vector2 -> create(accessor, value)
            is Vector3 -> create(accessor, value)
            is Vector4 -> create(accessor, value)
            else -> throw IllegalArgumentException("Unsupported vector type: ${value?.javaClass}")
        }
    }

    fun create(accessor: Accessor, value: Vector2): Table {
        return scene2d.table {
            label(accessor.getName()!!) {
                it.left()
                it.padRight(5f)
                it.growX()
            }
            var accessorX = FieldAccessor(accessor, "x")
            var accessorY = FieldAccessor(accessor, "y")

            var floatEditorX = FloatEditor()
            add(floatEditorX.create(accessorX))

            var floatEditorY = FloatEditor()
            add(floatEditorY.create(accessorY))

            align(Align.center)

            onChange {
                accessor.set(Vector2(accessorX.get() as Float, accessorY.get() as Float))
            }
        }
    }

    fun create (accessor: Accessor, value: Vector3): Table {
        return scene2d.table {
            label(accessor.getName()!!) {
                it.left()
                it.padRight(5f)
                it.growX()
            }
            var accessorX = FieldAccessor(accessor, "x")
            var accessorY = FieldAccessor(accessor, "y")
            var accessorZ = FieldAccessor(accessor, "z")

            var floatEditorX = FloatEditor()
            add(floatEditorX.create(accessorX))

            var floatEditorY = FloatEditor()
            add(floatEditorY.create(accessorY))

            var floatEditorZ = FloatEditor()
            add(floatEditorZ.create(accessorZ))

            align(Align.center)

            onChange {
                accessor.set(Vector3(accessorX.get() as Float, accessorY.get() as Float, accessorZ.get() as Float))
            }
        }
    }

    fun create (accessor: Accessor, value: Vector4): Table {
        return scene2d.table {
            label(accessor.getName()!!) {
                it.left()
                it.padRight(5f)
                it.growX()
            }
            var accessorX = FieldAccessor(accessor, "x")
            var accessorY = FieldAccessor(accessor, "y")
            var accessorZ = FieldAccessor(accessor, "z")
            var accessorW = FieldAccessor(accessor, "w")

            var floatEditorX = FloatEditor()
            add(floatEditorX.create(accessorX))

            var floatEditorY = FloatEditor()
            add(floatEditorY.create(accessorY))

            var floatEditorZ = FloatEditor()
            add(floatEditorZ.create(accessorZ))

            var floatEditorW = FloatEditor()
            add(floatEditorW.create(accessorW))

            align(Align.center)

            onChange {
                accessor.set(Vector4(accessorX.get() as Float, accessorY.get() as Float, accessorZ.get() as Float, accessorW.get() as Float))
            }
        }
    }
}