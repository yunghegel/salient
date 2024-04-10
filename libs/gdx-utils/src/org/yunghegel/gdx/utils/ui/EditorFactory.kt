package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.scenes.scene2d.Actor
import org.yunghegel.gdx.utils.ui.widgets.*
import org.yunghegel.gdx.utils.reflection.Accessor


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
        register(Vector::class.java) {
            VectorEditor().create(it)
        }
        register(Color::class.java) {
            ColorEditor().create(it)
        }


    }

}
