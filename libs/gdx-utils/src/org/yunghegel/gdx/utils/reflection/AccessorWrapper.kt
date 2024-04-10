package org.yunghegel.gdx.utils.reflection

import org.yunghegel.gdx.utils.reflection.Accessor

abstract class AccessorWrapper(protected var original: Accessor) : AccessorBase() {

    override fun <T : Annotation> config(annotation: Class<T>?): T? {
        return original.config<T>(annotation)
    }
}
