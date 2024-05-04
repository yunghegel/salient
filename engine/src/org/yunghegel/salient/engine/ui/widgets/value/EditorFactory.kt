package org.yunghegel.salient.engine.ui.widgets.value

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.salient.engine.ui.widgets.value.widgets.*

typealias FactoryFunction = (FieldAccessor) -> DefaultFieldEditor

object EditorFactory {

    val map = mutableMapOf<Class<*>, FactoryFunction>()

    val validTypes = mutableListOf<Class<*>>()

    fun register(clazz: Class<*>, factory: FactoryFunction) {
        validTypes.add(clazz)
        map[clazz] = factory
    }

    fun checkType(clazz: Class<*>): Boolean {
        return validTypes.contains(clazz)
    }

    fun create(accesor: FieldAccessor): DefaultFieldEditor? {
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
        register(Vector2::class.java) {
            VectorEditor().create(it)
        }
        register(Vector3::class.java) {
            VectorEditor().create(it)
        }
        register(Vector4::class.java) {
            VectorEditor().create(it)
        }
        register(Color::class.java) {
            ColorEditor().create(it)
        }


    }

}