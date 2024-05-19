package org.yunghegel.salient.engine.scene3d.graph

import com.badlogic.gdx.utils.Array
import org.yunghegel.salient.engine.api.properties.Parent

interface GraphNode<T> {

    fun addChild(child: T)

    fun removeChild(child: T)

    fun getChildren(): Array<T>

    fun getParent(): T?

    fun setParent(parent: T?)

}