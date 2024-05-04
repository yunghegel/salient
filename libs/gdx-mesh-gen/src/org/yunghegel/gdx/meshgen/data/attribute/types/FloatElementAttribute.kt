package org.yunghegel.gdx.meshgen.data.attribute.types

import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.attribute.ElementAttributeReference
import org.yunghegel.gdx.meshgen.data.attribute.BaseAttribute

open class FloatElementAttribute <E: Element>(elementAttributeReference: ElementAttributeReference<E>): BaseAttribute<E, Float>(elementAttributeReference) {

    override fun alloc(size: Int): Array<Float> {
        return Array(size) { 0f }
    }

    override fun toString(): String {
        return "FloatAttribute(name='$name', components=$numComponents)"
    }


}
