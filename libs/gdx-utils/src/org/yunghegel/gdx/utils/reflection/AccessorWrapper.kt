package org.yunghegel.salient.common.reflect

abstract class AccessorWrapper(protected var original: Accessor) : AccessorBase() {

    override fun <T : Annotation> config(annotation: Class<T>?): T? {
        return original.config<T>(annotation)
    }
}
