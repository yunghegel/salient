package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.sun.beans.editors.EnumEditor
import org.yunghegel.salient.common.reflect.Accessor
import org.yunghegel.salient.modules.io.debug
import org.yunghegel.salient.modules.ui.edit.widgets.*

typealias FactoryFunction = (Accessor) -> Actor

object EditorFactory {

    val map = mutableMapOf<Class<*>, FactoryFunction>()

    val validTypes = mutableListOf<Class<*>>()

    fun register(clazz: Class<*>, factory: FactoryFunction) {
        validTypes.add(clazz)
        map[clazz] = factory
    }

    fun create(accesor: Accessor): Actor? {
        if (!validTypes.contains(accesor.getType())) {
            debug("No editor for ${accesor.getType()}")
            return null

        }
        val factory = map[accesor.getType()]
        return factory?.invoke(accesor) ?: throw RuntimeException("No editor for ${accesor.getType()}")
    }

    init {
        register(Boolean::class.java) {
            BooleanEditor().create(it)
        }
        register(String::class.java) {
            StringEditor().create(it)
        }
        register(Float::class.java) {
            FloatEditor().create(it)
        }
        register(Int::class.java) {
            IntEditor().create(it)
        }
        register(Short::class.java) {
            ShortEditor().create(it)
        }
        register(Long::class.java) {
            LongEditor().create(it)
        }

    }

}
