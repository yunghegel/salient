package org.yunghegel.gdx.meshgen.data.gl

import org.yunghegel.gdx.meshgen.data.MeshAttributes
import org.yunghegel.gdx.meshgen.data.ifs.IVertex

class VertexBufferSlice(val vert: IVertex): BufferSlice<IVertex, Float> {

    var logicalSize: Int

    val sliceStartIndex: Int

    val sliceEndIndex : Int

    val slice : Array<Float>

    init {
        logicalSize = 0
        vert.mesh.vertexDataFlags.forEachTrue { logicalSize += it.size() }
        sliceStartIndex = vert.index * logicalSize
        sliceEndIndex = sliceStartIndex + logicalSize

        slice = initArray()
    }

    fun initArray() : Array<Float> {
        val array = Array(logicalSize) { 0f }
        return array
    }

    override fun createSlice(props:MeshAttributes) : Array<Float> {
//
//        props.attributes.forEach {
//            val subslice by subslice(it)
//            this += subslice
//        }

//        vert.mesh.vertexDataFlags.forEachTrue {
//
//
//
//            val attr = vert.getAttr(it)
//            val offset = props.offsetFor(it.alias())
//            val size = it.size
//
//            if (attr is Vector4Attribute) {
//                val vec = attr.get(vert)
//                slice[offset] = vec.x
//                slice[offset + 1] = vec.y
//                slice[offset + 2] = vec.z
//                slice[offset + 3] = vec.w
//            }
//            if (attr is Vector3Attribute) {
//                val vec = attr.get(vert)
//                slice[offset] = vec.x
//                slice[offset + 1] = vec.y
//                slice[offset + 2] = vec.z
//            }
//            if (attr is Vector2Attribute) {
//                val vec = attr.get(vert)
//                slice[offset] = vec.x
//                slice[offset + 1] = vec.y
//            }
//            if (attr is FloatAttribute) {
//                val vec = attr.get(vert)
//                slice[offset] = vec
//            }
//        }
        return slice
    }

//    operator fun plusAssign(subslice: AttributeSubslice) : Unit {
//        val offset = subslice.offset
//        val size = subslice.size
//        val floats = subslice.floats
//        for (i in 0 until size) {
//            slice[offset + i] = floats[i]
//        }
//    }

    override fun toString(): String {
        return slice.contentToString()
    }


}

//class AttributeSubslice(val prop: AttributeInfo,val vert:IVertex) {
//
//
//
//    val ref : VBOAttribute? = VBOAttribute.matchAlias(prop.alias)
//
//    val attr : BaseAttribute<IVertex, *>? = ref?.let { vert.getAttr(it) }
//
//    val offset : Int = prop.offset
//
//    val alias : String = prop.alias
//
//    val size : Int = prop.size
//
//    val floats : Array<Float>
//        get() {
//            val arr = Array<Float>(ref!!.size) { 0f }
//            if (attr is Vector4Attribute) {
//                val vec = attr.get(vert)
//                arr[0] = vec.x
//                arr[1] = vec.y
//                arr[2] = vec.z
//                arr[3] = vec.w
//            }
//            if (attr is Vector3Attribute) {
//                val vec = attr.get(vert)
//                arr[0] = vec.x
//                arr[1] = vec.y
//                arr[2] = vec.z
//            }
//
//            if (attr is Vector2Attribute) {
//                val vec = attr.get(vert)
//                arr[0] = vec.x
//                arr[1] = vec.y
//            }
//
//            if (attr is FloatAttribute) {
//                val vec = attr.get(vert)
//                arr[0] = vec
//            }
//
//            return arr
//
//        }
//
//    operator fun getValue(thisRef: Any?, property: KProperty<*>): Array<Float> {
//        return floats
//    }
//
//    operator fun getValue(thisRef: VertexBufferSlice?, property: KProperty<*>): AttributeSubslice {
//        return this
//    }
//
//}
//
//fun VertexBufferSlice.subslice(prop: AttributeInfo) : AttributeSubslice {
//    return AttributeSubslice(prop,vert)
//}