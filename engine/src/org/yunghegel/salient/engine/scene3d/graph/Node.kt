package org.yunghegel.salient.engine.scene3d.graph

import org.yunghegel.salient.engine.api.Dirty
import org.yunghegel.salient.engine.api.DirtyListener
import org.yunghegel.salient.engine.api.SelectionSource
import org.yunghegel.salient.engine.api.properties.Selectable

open class Node<T,O> (name:String, uuid: String? = null) : BaseNode<T>(name,uuid, getUniqueID()), Dirty<T>, Selectable where T:Node<T,O> {

    override var dirty: Boolean = false

    override val sources: List<SelectionSource> = listOf(SelectionSource.ALL)

    override var selected: Boolean = false

    override fun setSelect(selected: Boolean) {
        this.selected = selected
    }

    override val dirtySubscribers: MutableList<DirtyListener<T>> = mutableListOf()

    companion object {

        private val trackedIDs = mutableSetOf<Int>()

        fun checkID(id: Int) = trackedIDs.contains(id)

        fun getUniqueID(): Int {
            return nodeCount
        }

        private var nodeCount = 0
            get() {
                val id = field++
                println("Node count: $id")
                return id
            }
    }


}