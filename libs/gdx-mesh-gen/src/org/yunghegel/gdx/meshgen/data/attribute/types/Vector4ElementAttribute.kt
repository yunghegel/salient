package org.yunghegel.gdx.meshgen.data.attribute.types

import com.badlogic.gdx.math.Vector4
import org.yunghegel.gdx.meshgen.data.attribute.ElementAttributeReference
import org.yunghegel.gdx.meshgen.data.attribute.BaseAttribute
import org.yunghegel.gdx.meshgen.data.base.Element

class Vector4ElementAttribute<E: Element>(elementAttributeReference: ElementAttributeReference<E>): BaseAttribute<E, Vector4>(elementAttributeReference){

    override fun alloc(size: Int): Array<Vector4> {
        return Array(size) { Vector4() }
    }

}
