package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.math.component1
import ktx.math.component2
import org.yunghegel.gdx.utils.ext.toGdxArray
import org.yunghegel.salient.engine.events.autoemit
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SList.SListListener


class SList<T>() : List<T>(UI.skin) {

    fun interface SListListener<T> {
        fun onSelectionChanged(list: SList<T>, previousSelectedIndex: Int, newSelectedIndex: Int)
    }

    private var _selectedIndex = -1
    private var previousSelectedIndex = -1

    var selectionWidthSupplier : (() -> Vector2) ? = null

    constructor(widthSupplier : () -> Vector2) : this() {
        selectionWidthSupplier = widthSupplier
    }

    constructor(listener : (SList<T>,Int,Int)->Unit) : this() {
        this.listener =
            SListListener<T> { list, previousSelectedIndex, newSelectedIndex -> listener(list,previousSelectedIndex,newSelectedIndex) }
    }

    init {
        setSelectedIndex(-1)
    }

    private var changeListener: ChangeListener? = null
        set(value) {
            if (field != null) {
                super.removeListener(field)
            }
            field = value
            super.addListener(value)
        }

    var listener: SListListener<T>? = null
        set(value) {
            field = value
            changeListener = object:  ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    field?.onSelectionChanged(this@SList, previousSelectedIndex, _selectedIndex)
                }

                }
            }



    override fun setItems(newItems: Array<out T>?) {
        val gdxArr = newItems?.toGdxArray()
        super.setItems(gdxArr)
        if (newItems != null) {
            if (newItems.isNotEmpty()) {
                setSelectedIndex(0)
            }
        }
    }

    override fun drawSelection(batch: Batch?, drawable: Drawable?, x: Float, y: Float, width: Float, height: Float) {
        if (selectionWidthSupplier != null) {
            val (start, end) = selectionWidthSupplier!!.invoke()
            super.drawSelection(batch, drawable, start, y, end, height)
        } else {
            super.drawSelection(batch, drawable, x, y, width, height)
        }
    }


    override fun setSelectedIndex(index: Int) {
        previousSelectedIndex = _selectedIndex
        _selectedIndex = index
        super.setSelectedIndex(index)
    }


}
