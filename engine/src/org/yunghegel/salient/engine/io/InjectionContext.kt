package org.yunghegel.salient.engine.io

import ktx.inject.Context
import ktx.reflect.Reflection

object InjectionContext : Context() {



}inline fun <reified T:Any> inject() = InjectionContext.inject<T>()

@OptIn(Reflection::class)
inline fun <reified T:Any> singleton(singleton: T) = InjectionContext.bindSingleton<T>(singleton)

@OptIn(Reflection::class)
inline fun <reified T:Any> provide(noinline provider: ()->T) = InjectionContext.setProvider(T::class.java, provider)