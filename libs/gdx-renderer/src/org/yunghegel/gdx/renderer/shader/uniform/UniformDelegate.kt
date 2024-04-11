package org.yunghegel.gdx.renderer.shader.uniform

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class UniformDelegate<T> (val program : ShaderProgram, val type : GLSLType): ReadWriteProperty<Any, T> {

    init {


    }




    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        throw NotImplementedError()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        throw NotImplementedError()
    }
}