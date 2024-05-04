package org.yunghegel.gdx.meshgen.data.base

import org.yunghegel.gdx.meshgen.data.*
import org.yunghegel.gdx.meshgen.data.attribute.*
import org.yunghegel.gdx.meshgen.data.ifs.*
import org.yunghegel.gdx.meshgen.math.*
import kotlin.reflect.*

sealed class Element(val index: Int, val mesh:IFSMesh) : DirtySyncronized {

    val flags = EnumBitmask(ElementFlags::class.java)

    var attributes = mutableMapOf<String,ElementAttributeReference<*>>()






    val dirtyAttributes = mutableSetOf<ElementAttributeReference<*>>()

    infix fun has (flag: ElementFlags) : Boolean {
        return !getFlag(flag)
    }

    infix fun not (flag: ElementFlags) : Boolean {
        return getFlag(flag)
    }

    fun setFlag(flag: ElementFlags, value: Boolean) {
        flags.set(flag, value)
    }

    fun getFlag(flag: ElementFlags): Boolean {
        return flags.get(flag)
    }

    override fun isDirty(): Boolean {
        return getFlag(ElementFlags.MODIFIED)
    }

    override fun setClean() {
        setFlag(ElementFlags.MODIFIED,false)
    }

    override fun setDirty() {
        setFlag(ElementFlags.MODIFIED,true)
    }

    override fun update() {

    }

    internal inline fun <reified E: Element> reverseLookup(type: KClass<E>, index: Int): E {
        return when (type) {
            IVertex::class -> mesh.vertices().elements[index] as E
            IEdge::class -> mesh.edges().elements[index] as E
            IFace::class -> mesh.faces().elements[index] as E
            else -> throw IllegalArgumentException("Unknown element type")
        }
    }

    internal inline fun <reified E: Element> reverseLookup(type: KClass<E>, index: Int, block: (E) -> Unit) {
        val element = reverseLookup(type,index)
        block(element)
    }

    fun <E:Element,T> attribute(element: E,attr: BaseAttribute<E,T>) : ElementAttributeDelegate<E, T> {
        return ElementAttributeDelegate(element,attr)
    }

    fun <E:Element,T> attribute(attr: BaseAttribute<E,T>) : ElementAttributeDelegate<E, T> {
        return ElementAttributeDelegate(this as E, attr)
    }

    fun <E:Edge,T> eAttribute(attr: BaseAttribute<E,T>) : ElementAttributeDelegate<E, T> {
        return ElementAttributeDelegate(this as E, attr)
    }


}
