package org.yunghegel.salient.engine.api.properties

import com.badlogic.gdx.utils.Array

interface Parent<T> : Type {
    val children : Array<T>
    val parent : T?
}