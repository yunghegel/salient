package org.yunghegel.salient.engine.graphics.scene3d.graph

import com.badlogic.gdx.utils.Array

interface GraphNode<T> {

    fun addChild(child: T)

    fun removeChild(child: T)

    fun getChildren(): Array<T>

    fun getParent(): T?

    fun setParent(parent: T?)

}