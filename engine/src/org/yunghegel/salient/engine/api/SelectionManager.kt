package org.yunghegel.salient.engine.api

import com.badlogic.gdx.scenes.scene2d.utils.Selection
import ktx.collections.GdxArray
import ktx.collections.addAll
import org.yunghegel.salient.engine.api.properties.Selectable
import org.yunghegel.salient.engine.api.undo.SelectionListener

interface SelectionManager<T: Selectable> {

    val allowMultiple : Boolean

    val listeners : GdxArray<SelectionListener<T>>

    val selection : Selection<T>


    
    fun select(go: T, append: Boolean = allowMultiple) : Boolean
    
    fun deselect(go: T) : Boolean

    fun checkPresent(go: T) : Boolean

    fun set(items : List<T>) : Boolean {
        if (items.isEmpty()) return false
//        is it a list of one item?
        if (items.size == 1) {
            val item = items[0]
            if (selection.contains(item)) {
                deselect(item)
            } else {
                select(item, true)
            }
        } else {
            val arr = GdxArray<T>()
            arr.addAll(items)
//            the list of items currently selected, difference between the two lists
            val diff = GdxArray<T>()
            diff.addAll(selection.items())
            arr.forEach { item ->
                if (diff.contains(item)) {
                    diff.removeValue(item, true)
                }
            }
            diff.forEach { item ->
                deselect(item)
            }

            arr.forEach { item ->
                select(item, true)
            }
        }
        return true
    }


}