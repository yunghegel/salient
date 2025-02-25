package org.yunghegel.salient.engine.helpers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.FlushablePool
import com.badlogic.gdx.utils.Pool
import org.yunghegel.salient.engine.AutoremoveEntiy
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.api.ecs.ObjectEntity
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object Pools {
    val vector2Pool: Pool<Vector2> = object : Pool<Vector2>() {
        override fun newObject(): Vector2 {
            return Vector2()
        }

        override fun reset(`object`: Vector2) {
            `object`[0f] = 0f
        }
    }

    val vector3Pool: Pool<Vector3> = object : Pool<Vector3>() {
        override fun newObject(): Vector3 {
            return Vector3()
        }

        override fun reset(`object`: Vector3) {
            `object`[0f, 0f] = 0f
        }
    }

    /**
     * Convenience method, free array of objects
     * @param objects objects to free
     */
    fun free(vararg objects: Vector2) {
        for (`object` in objects) {
            vector2Pool.free(`object`)
        }
    }

    val quaternionPool: Pool<Quaternion> = object : Pool<Quaternion>(2) {
        override fun newObject(): Quaternion {
            return Quaternion()
        }

        override fun reset(`object`: Quaternion) {
            `object`.idt()
        }
    }

    val renderablePool  = object : FlushablePool<Renderable>() {
        override fun newObject(): Renderable {
            return Renderable()
        }

        override fun obtain(): Renderable {
            val renderable = super.obtain()
            renderable.environment = null
            renderable.material = null
            renderable.meshPart["", null, 0, 0] = 0
            renderable.shader = null
            renderable.userData = null
            return renderable
        }
    }

    class EntityPool : Pool<Entity>() {

        val defaultComponents: MutableList<KClass<out BaseComponent>> = mutableListOf()

        override fun newObject(): Entity {
            val entity = ObjectEntity()
            defaultComponents.mapNotNull { it.primaryConstructor?.call() }.forEach(entity::add)
            return entity
        }

        override fun reset(entity: Entity) {
            val removable = entity.components.filter { it::class !in defaultComponents }
            removable.forEach { entity.remove(it::class.java) }
        }

        fun defaults(vararg component: KClass<out BaseComponent>) {
            defaultComponents.addAll(component)
        }
    }

    val entityPool = EntityPool()

    val autotoremoveEntityPool = object : Pool<AutoremoveEntiy>() {

            override fun newObject(): AutoremoveEntiy {
                return AutoremoveEntiy()
            }

            override fun reset(entity: AutoremoveEntiy) {
                entity.removeAll()
            }
    }

    init {
        entityPool.fill(16)
        autotoremoveEntityPool.fill(16)
        
    }


}
