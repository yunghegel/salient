package org.yunghegel.salient.engine.api.properties

interface Parent<T:Subtype> : Type {
    val children : List<T>
}