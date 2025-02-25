package org.yunghegel.salient.engine.events

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Event(val alias: String, val params: Array<KClass<*>> = [])