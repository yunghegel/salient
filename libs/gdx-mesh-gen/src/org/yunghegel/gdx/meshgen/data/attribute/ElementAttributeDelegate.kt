package org.yunghegel.gdx.meshgen.data.attribute

import org.yunghegel.gdx.meshgen.data.*
import org.yunghegel.gdx.meshgen.data.base.*
import org.yunghegel.gdx.meshgen.data.ifs.*

class ElementAttributeDelegate<E: Element,T>(val element: E, val attribute: BaseAttribute<E,T>) : CachedReference<E, T>, ComputedNotifier<E,T> by attribute {

    override val ref : E = element
    override val syncronizer : DirtySyncronizer<E, T> = DirtySyncronizer { ref -> attribute.get(ref) }
    override val listeners : MutableList<DirtyListener<T>> = mutableListOf()
    override var cached : T = syncronizer.getCurrent(ref)



    init {

    }

    operator fun getValue(thisRef: BaseAttribute<E,*>, property: Any?): T {
        return retrieve()
    }

    operator fun setValue(thisRef: E, property: Any?, value: T) {
        if (attribute.setter != null) {
            attribute.setter!!(thisRef,value)
        } else {
            cached = value
        }


        if (element.mesh.initialConstructionComplete) {
            val dependencies = attribute.dependencies
            emitEvent()

        }


            element.setDirty()
    }

    fun resolveFaceDependency(face: Face,dependency:BaseAttribute<*,*>) {
        val dependencyResolution = when(dependency.name) {
            "normal" -> {
                val v1 = face.vertices[0]
                val v2 = face.vertices[1]
                val v3 = face.vertices[2]
                val p1 = v1.position
                val p2 = v2.position
                val p3 = v3.position
                val v12 = p2.cpy().sub(p1)
                val v13 = p3.cpy().sub(p1)
                val normal = v12.crs(v13).nor()
                normal
            }
            else -> throw IllegalStateException("Unknown dependency")
        }

    }


    fun emitEvent() {
        when (element) {
            is IVertex -> {
                val event =
                    ElementChangeEvent<IVertex, ElementData<IVertex>>(
                        ChangeType.MODIFICATION,
                        element,
                        element.elementData,
                        element.index
                                                                     )
                element.mesh.emitVertxChanged(event)
            }

            is IFace   -> {
                val event =
                    ElementChangeEvent<IFace, ElementData<IFace>>(
                        ChangeType.MODIFICATION,
                        element,
                        element.elementData,
                        element.index
                                                                 )
                element.mesh.emitFaceChanged(event)
            }

            is IEdge   -> {
                val event =
                    ElementChangeEvent<IEdge, ElementData<IEdge>>(
                        ChangeType.MODIFICATION,
                        element,
                        element.elementData,
                        element.index
                                                                 )
                element.mesh.emitEdgeChanged(event)
            }

            else       -> throw IllegalStateException("Unknown element type")
        }
    }

    override fun retrieve(): T {
        if (checkDirty()) {
            store(resolveValue())
            onDirty(element,cached)
        }
        return cached
    }

}




typealias DerivedFromSetter<E,T> = (E,T)->T

interface ValueObserver<T,Prop> {

      fun onDirty(value: T, property: Prop)
}

interface ElementObserver<T:Element,Property> : ValueObserver<T,Property>

interface ElementAttributeObserver<E: Element,T> : ElementObserver<E,T>


interface ComputedValueNotifer<T:DirtySyncronized,Prop> {

    val observers: MutableList<ValueObserver<T,Prop>>

    fun notifty(value: T, property: Prop) {
        observers.forEach { it.onDirty(value,property) }
    }

    fun listen(fn: (T,Prop)->Unit) {
        observers.add(object : ValueObserver<T,Prop> {
            override fun onDirty(value: T, property: Prop) {
                fn(value,property)
            }
        })
    }

}

interface ComputedNotifier<E:Element,T> : ElementAttributeObserver<E,T> {

    val attr: BaseAttribute<E,T>

    fun observers() : MutableList<ElementAttributeObserver<E,T>> {
        return attr.observers
    }

    override fun onDirty(element: E, value: T) {
        observers().forEach { it.onDirty(element,value) }
    }

    fun listen(fn: (E,T)->Unit) {
        observers().add(object : ElementAttributeObserver<E,T> {
            override fun onDirty(element: E, value: T) {
                fn(element,value)
            }
        })
    }

}

fun interface RecomputeFunction<E: Element,ReturnValue,Ref: CachedReference<E, *>> {
    fun compute(ref: Ref,element:E) : ReturnValue
}

fun <E: Element,T> E.attribute(attribute: BaseAttribute<E,T>, recompute: RecomputeFunction<E, *, CachedReference<E, T>>? = null) : ElementAttributeDelegate<E,T> {

    val delegate = ElementAttributeDelegate(this,attribute)

    delegate.listen {
        element, value ->
        println("Created ${attribute.name} for ${element.index}")
    }

    if (recompute != null) {
        delegate.listen { element, value ->
            println("Recompouting ${attribute.name} for ${element.index}")
            recompute.compute(delegate,element)
        }
    }
    return delegate
}
