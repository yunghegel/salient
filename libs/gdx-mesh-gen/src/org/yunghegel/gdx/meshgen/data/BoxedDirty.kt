package org.yunghegel.gdx.meshgen.data

import kotlin.reflect.*

class BoxedDirty<T> (var value:T,dirty: DirtySyncronized) :  DirtySyncronized by dirty {

    var shouldSetDirty : ()->Boolean = { false }

    operator fun getValue(thisRef: Any?, property: KProperty<*>?): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>?, value: T) {
        if (shouldSetDirty()) {
            setDirty()
        }
        this.value = value
    }

}
