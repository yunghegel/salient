package org.yunghegel.salient.editor.ui.scene.inspector

import org.yunghegel.debug.Drawable
import org.yunghegel.gdx.utils.data.Mask
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.ui.flag
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.ui.container.Panel

abstract class Inspector(val title:String, val icon:String) : Panel() {

    init {
        createIcon(icon)
        createTitle(title,"")
    }

    abstract fun createLayout()





}

abstract class ComponentInspector<T,C>(val type: Class<T>,title:String,icon:String) : Inspector(title,icon) where T:EntityComponent<C> {

    val settings = STable()

    var current : T? = null

    init {

    }



    abstract fun populate(component: T)

    fun setComponent(component: T) {
        populate(component)
    }



}