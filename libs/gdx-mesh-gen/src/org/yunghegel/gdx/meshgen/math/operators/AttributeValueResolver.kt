package org.yunghegel.gdx.meshgen.math.operators

import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.attribute.BaseAttribute

interface AttributeValueResolver<E: Element,Value,Attribute:BaseAttribute<E,Value>> {

        fun resolve(attribute: Attribute, element: E): AttributeProperty<E,Value>

}