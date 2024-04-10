package org.yunghegel.salient.engine.helpers.reflect.annotation

import org.yunghegel.salient.engine.helpers.reflect.Type

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Persistent(val key:String, val type: Type)
