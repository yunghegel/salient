package org.yunghegel.salient.engine.ui.widgets

import ktx.actors.onChange
import ktx.collections.GdxArray
import org.yunghegel.salient.engine.ui.scene2d.SSelectBox

class BooleanSelectBox(val setter: (Boolean)->Unit) : SSelectBox<BooleanSelectBox.BooleanItem>() {

    enum class BooleanItem {
        True, False
    }

        init {
            val items = GdxArray<BooleanItem>()
            items.add(BooleanItem.True)
            items.add(BooleanItem.False)
            setItems(items)

            onChange {
                setter(selected == BooleanItem.True)
            }
        }



}
