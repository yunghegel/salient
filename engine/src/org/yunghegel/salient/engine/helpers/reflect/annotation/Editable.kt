package org.yunghegel.salient.engine.helpers.reflect.annotation

import org.yunghegel.salient.engine.helpers.reflect.Type

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Editable(val name:String, val group: String = "none",val type: Type)
