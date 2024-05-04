package org.yunghegel.gdx.meshgen.data.attribute

import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.utils.FloatArray

data class AttributeInfo (val offset:Int,val size:Int, val usage: Int, val alias: String) {

    constructor(attr: VertexAttribute) : this(attr.offset/4 ,attr.numComponents,attr.usage, attr.alias)


}
