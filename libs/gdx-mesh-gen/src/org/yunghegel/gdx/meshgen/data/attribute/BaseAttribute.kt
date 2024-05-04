package org.yunghegel.gdx.meshgen.data.attribute

import org.yunghegel.gdx.meshgen.data.base.*
import org.yunghegel.gdx.meshgen.data.ifs.*

abstract class BaseAttribute<E: Element,Data>
    (override val ref: ElementAttributeReference<E>,val resolutionStrategy: ((E,Data)->Data)?=null)
    : AbstractAttribute<E, Data, Array<Data>>(ref),ComputedNotifier<E,Data> {

    var setter: ((E,Data)->Unit)? = null
    var dependencies: Set<BaseAttribute<*,*>> = setOf()
    val observers = mutableListOf<ElementAttributeObserver<E,Data>>()

    override val attr: BaseAttribute<E, Data>
        get() = this

    override fun observers(): MutableList<ElementAttributeObserver<E, Data>> {
        return observers
    }

    override fun get(element: E): Data {
        return data!![indexOf(element)]
    }

    override fun set(element: E, data: Data) {
        val old = get (element)

        if (old.hashCode() != data.hashCode()) {
            this.data!![indexOf(element)] = data
            element.setDirty()
            if (element.mesh.initialConstructionComplete) {
               when (element) {
                    is IVertex -> {
                        val event = ElementChangeEvent<IVertex,ElementData<IVertex>>(ChangeType.MODIFICATION,element,element.elementData, element.index)
//                        element.mesh.emitVertxChanged(event)
                    }
                    is IFace -> {
                        val event = ElementChangeEvent<IFace,ElementData<IFace>>(ChangeType.MODIFICATION,element,element.elementData, element.index)
//                        element.mesh.emitFaceChanged(event)
                    }
                    is IEdge -> {
                        val event = ElementChangeEvent<IEdge,ElementData<IEdge>>(ChangeType.MODIFICATION,element,element.elementData, element.index)
//                        element.mesh.emitEdgeChanged(event)
                    }
                    else     -> throw IllegalStateException("Unknown element type")
                }


            }

        }
    }

    fun getElementType() : Class<out Element> {
        return when (name[0]) {
            'v' -> IVertex::class.java
            'f' -> IFace::class.java
            'e' -> IEdge::class.java
            else -> throw IllegalStateException("Unknown element type")
        }
    }

    override fun clear(element: E) {
        val idx = indexOf(element)
        for (i in 0 until numComponents) {
            this.data!![idx + i] = null!!
        }
    }

    override fun indexOf(element: E): Int {
        val idx = element.index
        return idx * numComponents
    }

    override fun indexOf(element: E, component: Int): Int {
        if (component < 0 || component >= numComponents) {
            throw IndexOutOfBoundsException("component must be between 0 and $numComponents")
        }
        return indexOf(element) + component
    }

    override fun copy(element: E, other: E) {
        val iFrom = indexOf(element)
        val iTo = indexOf(other)
        System.arraycopy(data!!, iFrom, data!!, iTo, numComponents)
    }

    override fun toString(): String {
        return "BaseAttribute(name='$name', components=$numComponents)"
    }
}

abstract class AbstractAttribute<E: Element,Data,Array>(val reference: ElementAttributeReference<E>): ElementAttribute<E,Data,Array, Any?> {

    override val name: String
        get() = reference.alias()

    override val type: Class<*>
        get() = reference.type()

    override val numComponents: Int
        get() = reference.size()


    override var data: Array? = null

    protected abstract fun alloc(size: Int): Array

    override fun allocReplace(size: Int): Array? {
        val oldArray: Array? = data
        data = alloc(size * numComponents)
        return oldArray
    }

    override fun realloc(size: Int, copyLength: Int) {
        val oldArray: Array? = allocReplace(size)
        System.arraycopy(oldArray!!, 0, data!!, 0, copyLength * numComponents)
    }

}

interface DerivedMeshProperty<Property,Data,Array> {



}
