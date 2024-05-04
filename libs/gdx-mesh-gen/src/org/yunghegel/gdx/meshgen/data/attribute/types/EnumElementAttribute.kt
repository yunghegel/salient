package org.yunghegel.gdx.meshgen.data.attribute.types

import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.attribute.ElementAttributeReference
import org.yunghegel.gdx.meshgen.data.attribute.BaseAttribute

class EnumElementAttribute<Enum:kotlin.Enum<Enum>,E: Element>(elementAttributeReference: ElementAttributeReference<E>, val enumClass: Class<Enum>) : BaseAttribute<E,Enum>(elementAttributeReference) {

    val allocator = ArrayFactory<Enum> { size -> java.lang.reflect.Array.newInstance(enumClass,size) as Array<Enum> }

    override fun alloc(size: Int): Array<Enum> {
        return allocator.alloc(size)
    }

    override fun set(element:E, data:Enum) {
        this.data!![indexOf(element)] = data
    }

}
