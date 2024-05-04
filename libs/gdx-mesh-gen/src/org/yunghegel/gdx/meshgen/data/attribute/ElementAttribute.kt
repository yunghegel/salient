package org.yunghegel.gdx.meshgen.data.attribute

import org.yunghegel.gdx.meshgen.data.base.Element

interface ElementAttribute<E: Element,Data,Array, T> {

    val ref : ElementAttributeReference<E>

    val name: String

    val type: Class<*>

    val numComponents: Int


    var data: Array?

    fun get(element:E):Data

    fun set(element:E, data:Data)

    fun clear(element:E)

    fun indexOf(element:E):Int

    fun indexOf(element:E,component:Int):Int

    fun copy(element:E,other:E)

    fun realloc(size:Int, copyLength:Int)

    fun allocReplace(size:Int):Array?



}