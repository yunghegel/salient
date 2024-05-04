package org.yunghegel.gdx.meshgen.data.attribute.ref

interface TypedAttribute<Ref,Computed,Store> {

    fun alloc(size: Int): Store

    fun get(ref: Ref) : Computed

    fun set(ref: Ref, value:Computed)

    fun clear()

    fun copy(from:Ref,to:Ref)

}
