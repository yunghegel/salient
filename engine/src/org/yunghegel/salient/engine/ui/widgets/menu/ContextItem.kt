package org.yunghegel.salient.engine.ui.widgets.menu

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.widget.MenuItem
import org.yunghegel.salient.engine.ui.UI

class ContextItem(text: String, val icon: Drawable?=null, val action: ()->Unit) : MenuItem(text,icon) {

    constructor(text:String, icon: String, action: ()->Unit) : this(text,UI.drawable(icon),action)
    constructor(text:String, action: ()->Unit) : this(text,null,action)

    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action()
            }
        })
    }

}