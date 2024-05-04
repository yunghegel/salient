package org.yunghegel.gdx.meshgen.data.attribute

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.math.*
import org.yunghegel.gdx.meshgen.data.attribute.ref.prop.*
import org.yunghegel.gdx.meshgen.data.base.*
import org.yunghegel.gdx.meshgen.math.*

interface ElementAttributeReference<E: Element> : StringAlias, TypeReference {
    abstract val ref: Triple<String,Class<*>, Int>

    override val alias: String
        get() = ref.first

    override val type: Class<*>
        get() = ref.second



    fun alias() : String {
        return ref.first
    }
    
    fun type() : Class<*>{
        return ref.second
    }
    
    fun size() : Int {
        return ref.third
    }

   fun equalTo(other: ElementAttributeReference<*>) : Boolean {
        return this.alias() == other.alias() && this.type() == other.type()
    }

    operator fun compareTo(other: ElementAttributeReference<*>) : Int {
        return this.alias().compareTo(other.alias())
    }
}


interface AttributeProducer<E:Element> {

    val set : Set<ElementAttributeReference<E>>

    val mask : SetBitmask<ElementAttributeReference<E>>

    fun <T:Any> attributeRef(name:String,type:Class<T>,size:Int) : ElementAttributeReference<E> {
        val attr = object : ElementAttributeReference<E> {
            override val ref: Triple<String, Class<*>, Int>
                get() = Triple(name,type,size)
        }
        return attr
    }

    fun match(alias: String) : ElementAttributeReference<E>? {
        return set.find { it.alias() == alias }
    }

    fun toSetBitmask() : SetBitmask<ElementAttributeReference<E>> {
        return SetBitmask(set)
    }

}

open class ElementAttributes<V:Vertex,F:Face,E:Edge,M:StructuredMesh<V,F,E>> {

    val DATA = DataAttributes<V,F,E,M>()
    val EDGE = EdgeAttributes<E>()
    val FACE = FaceAttributes<F>()
    val VERTEX = VertAttributes<V>()
    val BUFFER = BufferAttributes<V>()

}

class BufferAttributes<V:Vertex> internal constructor() : AttributeProducer<V>  {



    val POSITION = attributeRef("a_position", Vector3::class.java, 3)
    val NORMAL = attributeRef("a_normal", Vector3::class.java, 3)
    val BINORMAL = attributeRef("a_binormal", Vector3::class.java, 3)
    val TANGENT = attributeRef("a_tangent", Vector3::class.java, 3)
    val COLOR = attributeRef("a_color", Vector4::class.java, 4)
    val UV = attributeRef("a_texCoord0", Vector2::class.java, 2)

    fun mapTo(attribute: VertexAttribute) : ElementAttributeReference<V>? {
        return when(attribute.alias) {
            "a_position" -> POSITION
            "a_normal" -> NORMAL
            "a_binormal" -> BINORMAL
            "a_tangent" -> TANGENT
            "a_color" -> COLOR
            "a_texCoord0" -> UV
            else -> null
        }
    }

    fun sizeOf(alias : String) {
        when(alias) {
            "a_position" -> POSITION.size()
            "a_normal" -> NORMAL.size()
            "a_binormal" -> BINORMAL.size()
            "a_tangent" -> TANGENT.size()
            "a_color" -> COLOR.size()
            "a_texCoord0" -> UV.size()
        }
    }

    final override val set = setOf(
        POSITION,
        NORMAL,
        BINORMAL,
        TANGENT,
        COLOR,
        UV
    )

    override val mask : SetBitmask<ElementAttributeReference<V>>
        get() = SetBitmask(set)
}


class DataAttributes<V,F,E,M> internal constructor() where V:Vertex,E:Edge,F:Face,M:StructuredMesh<V,F,E> {

    val FACE  = object : ElementAttributeReference<F> {
        override val ref: Triple<String, Class<*>, Int>
            get() = Triple("face_data",ElementData::class.java, 1)
    }

    val EDGE  = object : ElementAttributeReference<E> {
        override val ref: Triple<String, Class<*>, Int>
            get() = Triple("edge_data",ElementData::class.java, 1)
    }

    val VERTEX  = object : ElementAttributeReference<V> {
        override val ref: Triple<String, Class<*>, Int>
            get() = Triple("vertex_data",ElementData::class.java, 1)
    }




}

class EdgeAttributes<E:Edge> internal constructor() : AttributeProducer<E> {

    val MIDPOINT = attributeRef("e_normal", Vector3::class.java, 3)
    val LENGTH = attributeRef("e_length", Float::class.java, 1)
    val PICKABLE_MAT = attributeRef("e_pickable_mat", Material::class.java, 1)

    override val set = setOf(
        MIDPOINT,
        LENGTH,
        PICKABLE_MAT
    )

    override val mask : SetBitmask<ElementAttributeReference<E>>
        get() = SetBitmask(set)
}

class FaceAttributes<F:Face> internal constructor(): AttributeProducer<F> {

    val CENTER = attributeRef("f_center", Vector3::class.java, 3)
    val NORMAL = attributeRef("f_normal", Vector3::class.java, 3)
    val WINDING = attributeRef("f_winding", WindingOrder::class.java, 1)
    val PICKABLE_MAT = attributeRef("f_pickable_mat", Material::class.java, 1)

    init {

    }

    override val set = setOf(
        CENTER,
        NORMAL,
        WINDING,
        PICKABLE_MAT
    )

    override val mask : SetBitmask<ElementAttributeReference<F>>
        get() = SetBitmask(set)

}

class VertAttributes<V:Vertex> internal constructor(): AttributeProducer<V> {

    val PICKABLE_MAT = attributeRef("v_pickable_mat", Material::class.java, 1)

    override val set = setOf(
        PICKABLE_MAT
    )

    override val mask : SetBitmask<ElementAttributeReference<V>>
        get() = SetBitmask(set)

}

@OptIn(ExperimentalStdlibApi::class)
enum class VBOAttribute(val size: Int) : ElementAttributeReference<Vertex> {

    POSITION(3) {
        override val ref: Triple<String,Class<*>, Int>
            get() = Triple("a_position",Vector3::class.java, size)
    },
    NORMAL(3){
        override val ref: Triple<String,Class<*>, Int>
            get() = Triple("a_normal",Vector3::class.java, size)
    },
    BINORMAL(3){
        override val ref: Triple<String,Class<*>, Int>
            get() = Triple("a_binormal",Vector3::class.java, size)
    },
    TANGENT(3){
        override val ref: Triple<String,Class<*>, Int>
            get() = Triple("a_tangent",Vector3::class.java, size)
    },
    COLOR(4){
        override val ref: Triple<String,Class<*>, Int>
            get() = Triple("a_color", Vector4::class.java, size)
    },
    UV(2){
        override val ref: Triple<String,Class<*>, Int>
            get() = Triple("a_texCoord0", Vector2::class.java, size)
    };


    companion object {


        fun matchAlias(alias: String) : VBOAttribute? {
            return entries.find { it.alias() == alias }
        }

        fun matchVertexAttribute(attribute: VertexAttribute) : VBOAttribute? {
            return entries.find { it.alias() == attribute.alias }
        }

    }
}
