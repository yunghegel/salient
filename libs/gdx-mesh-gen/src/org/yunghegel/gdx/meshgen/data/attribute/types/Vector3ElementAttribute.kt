package org.yunghegel.gdx.meshgen.data.attribute.types

import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.data.attribute.*
import org.yunghegel.gdx.meshgen.data.base.*

class Vector3ElementAttribute<E:Element>(elementAttributeReference: ElementAttributeReference<E>,resolutionStrategy: ((E,Vector3)->Vector3)?=null):BaseAttribute<E, Vector3>(elementAttributeReference,resolutionStrategy) {

    init {
        setter = {element, value ->
            val vec3 = get(element)
            vec3.set(value)
        }
    }

    override fun alloc(size: Int): Array<Vector3> {
        return Array(size) {Vector3()}
    }
}
