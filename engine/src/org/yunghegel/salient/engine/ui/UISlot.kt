package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import org.yunghegel.salient.engine.ui.scene2d.STable

enum class UISlot(val slot : Slot = Slot()) {
    LEFT_PANE, MENU_BAR, VIEWPORT, RIGHT_PANE, BOTTOM_PANE;

    operator fun invoke() = slot

    class Slot : STable() {

        val stack : Stack = Stack()

        init {
            add(stack).grow()
        }

        fun set(table : STable) {
            stack.clear()
            stack.add(table)
        }


    }
}
