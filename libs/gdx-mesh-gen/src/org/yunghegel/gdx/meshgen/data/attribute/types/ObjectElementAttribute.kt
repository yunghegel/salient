package org.yunghegel.gdx.meshgen.data.attribute.types

import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.attribute.ElementAttributeReference
import org.yunghegel.gdx.meshgen.data.attribute.BaseAttribute

open class ObjectElementAttribute<T,E: Element>(elementAttributeReference: ElementAttributeReference<E>, val factory:ArrayFactory<T>)  : BaseAttribute<E,T>(elementAttributeReference){

    override fun alloc(size: Int): Array<T> {
        return factory.alloc(size)
    }


}

fun interface ArrayFactory<T> {
    fun alloc(size:Int):Array<T>
}
