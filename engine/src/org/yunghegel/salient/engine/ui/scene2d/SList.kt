package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.*
import org.yunghegel.gdx.utils.ext.toGdxArray
import org.yunghegel.salient.engine.ui.UI


class SList<T> : List<T>(UI.skin) {

    fun interface SListListener<T> {
        fun onSelectionChanged(list: SList<T>, previousSelectedIndex: Int, newSelectedIndex: Int)
    }

    private var selectedIndex = -1
    private var previousSelectedIndex = -1

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
                    field?.onSelectionChanged(this@SList, previousSelectedIndex, selectedIndex)
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


    override fun setSelectedIndex(index: Int) {
        previousSelectedIndex = selectedIndex
        selectedIndex = index
        super.setSelectedIndex(index)
    }

}
