package org.yunghegel.salient.engine.system

import ktx.inject.Context
import ktx.inject.register
import ktx.reflect.Reflection

object InjectionContext : Context() {


}

inline fun <reified T:Any> inject() = InjectionContext.inject<T>()

inline fun <reified T:Any> injectUnsafe(clazz:Class<T> = T::class.java) : T{
    return inject<T>()
}

@OptIn(Reflection::class)
inline fun <reified T:Any> singleton(singleton: T) = InjectionContext.bindSingleton<T>(singleton)

@OptIn(Reflection::class)
inline fun <reified T:Any> provide(noinline provider: ()->T) = InjectionContext.setProvider(T::class.java, provider)

@OptIn(Reflection::class)
inline fun register(action: Context.()->Unit) = InjectionContext.register { InjectionContext.action() }