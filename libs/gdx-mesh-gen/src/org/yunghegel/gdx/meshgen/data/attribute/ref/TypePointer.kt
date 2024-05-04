package org.yunghegel.gdx.meshgen.data.attribute.ref

import org.yunghegel.gdx.meshgen.data.attribute.ref.prop.SizeParameter
import org.yunghegel.gdx.meshgen.data.attribute.ref.prop.StringAlias
import org.yunghegel.gdx.meshgen.data.attribute.ref.prop.TypeReference

interface TypePointer<T>  : StringAlias, TypeReference, SizeParameter {

    val ref : Triple<String,Class<*>,Int>

    override val alias: String
        get() = ref.first

    override val type: Class<*>
        get() = ref.second

    override val size: Int
        get() = ref.third

    fun equalTo(other: TypePointer<T>) : Boolean {
        return alias== other.alias && this.type == other.type
    }

    operator fun compareTo(other: TypePointer<*>) : Int {
        return this.alias.compareTo(other.alias)
    }

}