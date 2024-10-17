package org.yunghegel.gdx.meshgen.data.attribute.types

import com.badlogic.gdx.math.Vector2
import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.attribute.ElementAttributeReference
import org.yunghegel.gdx.meshgen.data.attribute.BaseAttribute

class Vector2ElementAttribute<E: Element>(elementAttributeReference: ElementAttributeReference<E>) : BaseAttribute<E,Vector2>(elementAttributeReference) {

    init {
        setter = {element, value ->
            val vec2 = get(element)
            vec2.set(value)
        }
    }

    override fun alloc(size: Int): Array<Vector2> {
        return Array(size) { Vector2() }
    }
}
