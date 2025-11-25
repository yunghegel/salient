package org.yunghegel.salient.engine.ui.widgets.menu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import org.yunghegel.salient.engine.ui.UI.SInput

class SelectionContextMenu<T,A>(val actor: Actor,val selectionResolver: ()->A,val objResolver: (A)->T) : PopupMenu() {

    init {
        actor.addListener(object :ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            if (button== Input.Buttons.RIGHT) showMenu(actor.stage,x,y)
                return super.touchDown(event, x, y, pointer, button)
            }
        })
    }

    fun addAction(text:String, action: (T)->Unit) {
        val menu = MenuItem(text)
        menu.addListener(object:ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val obj = objResolver(selectionResolver())
                action(obj)
            }
        })
        addItem(menu)
    }

}