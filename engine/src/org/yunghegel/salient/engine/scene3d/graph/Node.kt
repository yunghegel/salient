package org.yunghegel.salient.engine.scene3d.graph

import com.badlogic.gdx.scenes.scene2d.Actor
import org.checkerframework.checker.units.qual.K
import org.yunghegel.salient.engine.api.Dirty
import org.yunghegel.salient.engine.api.DirtyListener
import org.yunghegel.salient.engine.api.SelectionSource
import org.yunghegel.salient.engine.api.properties.ObjectPayload
import org.yunghegel.salient.engine.api.properties.Selectable

open class Node<T,O> (name:String, uuid: String? = null, id:Int?= null) : BaseNode<T>(name,uuid,id), Dirty<T>, Selectable where T:Node<T,O> {

    override var dirty: Boolean = false

    override val sources: List<SelectionSource> = listOf(SelectionSource.ALL)

    override var selected: Boolean = false

    override fun setSelect(selected: Boolean) {
        this.selected = selected
    }

    override val dirtySubscribers: MutableList<DirtyListener<T>> = mutableListOf()


}