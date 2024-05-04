package org.yunghegel.gdx.meshgen.data.base

import org.yunghegel.gdx.meshgen.data.*
import org.yunghegel.gdx.meshgen.data.attribute.*
import kotlin.math.*
import kotlin.reflect.*

const val INITAL_SIZE = 32
const val GROWTH_FACTOR = 1.5f

class ElementData<E: Element>(val mesh: StructuredMesh<*,*,*>,val factory: ElementFactory<E>, ) : Iterable<E> {


    val elements = mutableListOf<E>()



    init {

    }

    var arraySize = INITAL_SIZE

    val attributes = mutableMapOf<String,BaseAttribute<in E, out Any>>()

    var modCount = 0

    var virtualCount = 0

    fun clearAttributes() {
        attributes.clear()
    }

    fun size():Int {
        return elements.size - virtualCount
    }

    fun totalSize():Int {
        return elements.size
    }

    fun <C:MutableCollection<E>> getAll(dest:C):C {
        dest.addAll(elements)
        return dest
    }

    fun getAll():List<E> {
        return elements
    }

    fun clear() {
        elements.clear()
        virtualCount = 0
        modCount++
    }

    fun create() : E {
        val idx = totalSize()
        if (idx >= arraySize) {
            val capacity = ceil(arraySize * GROWTH_FACTOR).toInt()
            ensureCapacity(capacity)
        }
        val element = factory.create(idx)
        elements.add(element)
        modCount++
        return element
    }

    fun create(idx:Int) : E {
        if (idx >= arraySize) {
            val capacity = ceil(arraySize * GROWTH_FACTOR).toInt()
            ensureCapacity(capacity)
        }
        val element = factory.create(idx)
        elements.add(element)
        modCount++
        return element
    }



    fun resize(size:Int, copyLength:Int) {
        for (attr in attributes.values) {
            attr.realloc(size, copyLength)
        }

        arraySize = size
    }

    fun ensureCapacity(capacity:Int) {
        if (capacity > arraySize) {
            resize(capacity, arraySize)
        }
    }

    fun reserveCapacity(capacity:Int) {
        ensureCapacity(size() + capacity);
    }

    inline fun <T:Any> addAttribute(attr:BaseAttribute<E,T>) {
        if(attributes.containsKey(attr.name)) {
            return
        }
        if (attr.data!=null) {
            throw IllegalArgumentException("Attribute ${attr.name} already has data")
        }

        val old = attr.allocReplace(arraySize)

        assert(old == null)
        attributes[attr.name] = attr
    }





    operator fun getValue(thisRef: List<E>?, property: KProperty<*>): Set<E> {
        return elements.toSet()
    }

    override fun iterator(): Iterator<E> {
        return ElementIterator()
    }

    inner class ElementIterator : Iterator<E> {

        var idx = 0

        var lastModCount = modCount

        override fun hasNext(): Boolean {
            return idx < size()
        }

        override fun next(): E {
            if (lastModCount != modCount) {
                throw ConcurrentModificationException()
            }
            return elements[idx++]
        }
    }


}

enum class ChangeType {
    INSERTION,REMOVAL,MODIFICATION
}

interface ElementChange<T:Element,Data: ElementData<T>> {
    val type: ChangeType
    val element: T
    val data: Data?
    val index: Int
}


class ElementChangeEvent<T:Element,Data: ElementData<T>>(override val type: ChangeType, override val element: T, override val data: Data?=null, override val index: Int) : ElementChange<T,Data> {

}

fun interface ElementChangeObserver<T:Element> {

    fun onElementChange(change: ElementChangeEvent<T, ElementData<T>>)

}

fun interface VertexChangeObserver<V:Vertex> : ElementChangeObserver<V>




fun interface FaceChangeObserver<F:Face> : ElementChangeObserver<F>




fun interface EdgeChangeObserver<E:Edge> :  ElementChangeObserver<E>

interface MeshObserver<V:Vertex,F:Face,E:Edge> {

    val vertexListener: VertexChangeObserver<V>

    val faceListener: FaceChangeObserver<F>

    val edgeListener: EdgeChangeObserver<E>

}


interface ElementChangeNotifier<V:Vertex,F:Face,E:Edge> {

    val edgeObservers : MutableList<EdgeChangeObserver<E>>
    val vertexObservers : MutableList<VertexChangeObserver<V>>
    val faceObservers : MutableList<FaceChangeObserver<F>>


    fun subscribe(observer: VertexChangeObserver<V>) {
        vertexObservers.add(observer)
    }

    fun subscribe(observer: FaceChangeObserver<F>) {
        faceObservers.add(observer)
    }

    fun subscribe(observer: EdgeChangeObserver<E>) {
        edgeObservers.add(observer)
    }

    fun unsubscribe(observer: VertexChangeObserver<V>) {
        vertexObservers.remove(observer)
    }

    fun unsubscribe(observer: FaceChangeObserver<F>) {
        faceObservers.remove(observer)
    }

    fun unsubscribe(observer: EdgeChangeObserver<E>) {
        edgeObservers.remove(observer)
    }

}
