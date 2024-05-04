package org.yunghegel.gdx.meshgen.math.operators

import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.attribute.BaseAttribute

class CalculatedPropertyDelegate<E: Element,Value,Attribute: BaseAttribute<E,Value>>(val element: E, val attribute:Attribute, val resolver: AttributeValueResolver<E, Value, Attribute>) : AttributeProperty<E,Value> {

    override val prop: Value
        get() = resolver.resolve(attribute,element).prop


}