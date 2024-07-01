package org.yunghegel.salient.engine.system

import ktx.inject.Context
import ktx.inject.register
import ktx.reflect.Reflection
import org.yunghegel.gdx.utils.reflection.newInstance
import kotlin.properties.Delegates

object InjectionContext : Context() {


}

inline fun <reified T:Any> inject() =
   try { InjectionContext.inject<T>() } catch (e:Exception) {
       info("Injection failed for ${T::class.simpleName} - ${e.message}")
       newInstance(T::class)
   }

inline fun <reified T:Any> injectUnsafe(clazz:Class<T> = T::class.java) : T{
    return inject<T>()
}

@OptIn(Reflection::class)
inline fun <reified T:Any> singleton(singleton: T) {
    InjectionContext.contains(T::class.java).let { if (it) InjectionContext.remove<T>() }.also {
        InjectionContext.bindSingleton<T>(singleton)
    }
}

@OptIn(Reflection::class)
inline fun <reified T:Any> provide(noinline provider: ()->T) = InjectionContext.setProvider(T::class.java, provider)

@OptIn(Reflection::class)
inline fun register(action: Context.()->Unit) = InjectionContext.register { InjectionContext.action() }

//inline fun <reified T:Any> lazyInject() =