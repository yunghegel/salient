package org.yunghegel.salient.engine.api

import com.badlogic.gdx.scenes.scene2d.utils.Selection
import org.yunghegel.salient.engine.api.properties.Selectable
import org.yunghegel.salient.engine.api.undo.SelectionListener

val UI_SOURCE = SelectionSource.UI
val VIEWPORT_SOURCE = SelectionSource.VIEWPORT
val CODE_SOURCE = SelectionSource.PROGRAMMATIC

abstract class BaseSelectionManager<T:Selectable>(override val selection: Selection<T> = Selection()) : SelectionManager<T> {

    override val allowMultiple: Boolean = false

    /**
     * Selects the given object. If append is true, the object is added to the selection, otherwise it replaces the current selection.
     * @param go The object to select
     * @param append Whether to append the object to the selection
     * @return True if the selection was changed.
     */

    override fun select(go: T, append:Boolean ) : Boolean {
        if (checkPresent(go)) {
            return false
        }
        if (append) {
            selection.add(go)
        } else {
            selection.set(go)
        }
        return true
    }

    /**
     * Deselects the given object.
     * @param go The object to deselect
     * @return True if the selection was changed.
     */

    override fun deselect(go: T) : Boolean {
        if (!checkPresent(go)) {
            return false
        }
        selection.remove(go)
        return true
    }

    override fun checkPresent(go: T): Boolean {
        return selection.contains(go)
    }

    fun effect(kind: SelectionSource =SelectionSource.ALL, effect : (T?, T?, Boolean) -> Unit) {
        val listener = object : SelectionListener<T> {
            override fun onSelectionChanged(old: T?, new: T?, source: SelectionSource) {
                effect(old,new,source == CODE_SOURCE)
            }
        }
        listeners.add(listener)
    }

    fun getSelected() : T? {
        return selection.first()
    }

}