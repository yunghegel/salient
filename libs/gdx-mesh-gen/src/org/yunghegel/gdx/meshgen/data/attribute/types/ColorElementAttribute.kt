package org.yunghegel.gdx.meshgen.data.attribute.types

import com.badlogic.gdx.graphics.Color
import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.attribute.ElementAttributeReference
import org.yunghegel.gdx.meshgen.data.attribute.BaseAttribute

class ColorElementAttribute <E: Element>(elementAttributeReference: ElementAttributeReference<E>) : BaseAttribute<E, Color>(elementAttributeReference)
{
    override fun alloc(size: Int): Array<Color> {
        return Array(size) { Color() }
    }
}
