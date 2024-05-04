package org.yunghegel.gdx.meshgen.data

import org.yunghegel.gdx.meshgen.data.attribute.ElementFlags
import org.yunghegel.gdx.meshgen.data.base.Element
import org.yunghegel.gdx.meshgen.data.base.ElementData
import kotlin.reflect.KProperty

class ElementStream<E: Element>(val elementData: ElementData<E>) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>?): Sequence<E> {
        val elements = elementData.elements
        return elements.asSequence()
    }

}

class FilteredSequence<E: Element>(val sequence: Sequence<E>) : Sequence<E> {

    var filter: ElementFilter<E> = ElementFilter()

    var filtered = 0

    override fun iterator(): Iterator<E> {
        return sequence.filter {
           val filtered = filter.filter(it)
            if (!filtered) {
                this.filtered++
            }
            filtered
        }.iterator()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>?): FilteredSequence<E> {
        return this
    }
}

inline fun <reified  E:Element> meshdata( data: ElementData<E>): FilteredSequence<E> {
    val sequence by ElementStream(data)
    return FilteredSequence(sequence)
}

class ElementFilter<E: Element>() {



    val predicates = mutableListOf<ElementPredicate<E>>()

    fun add(predicate: ElementPredicate<E>) {
        predicates.add(predicate)
    }

    fun filter(element: E): Boolean {
        return predicates.all {
            it.test(element)
        }
    }
}

fun interface ElementPredicate<E: Element> {
    fun test(element: E): Boolean
}