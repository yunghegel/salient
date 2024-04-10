package org.yunghegel.salient.engine.graphics.scene3d

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.yunghegel.salient.engine.api.Tagged
import org.yunghegel.salient.engine.api.UpdateRoutine
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.graphics.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.graphics.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.graphics.scene3d.graph.Spatial
import org.yunghegel.salient.engine.io.inject
import java.util.*
import kotlin.reflect.KClass

class GameObject(name: String, id: Int? = null) : Spatial<GameObject>(name), Iterable<GameObject>, UpdateRoutine, Tagged {

    override val tags: MutableSet<String> = mutableSetOf()

    override val id: Int = id ?: generateID()

    val components : List<EntityComponent<*>>
        get() = getComponents().filterIsInstance<EntityComponent<*>>()

    private val engine : Engine = inject()

    init {
        add(TransformComponent(this))
        engine.addEntity(this)
        tag(name)
    }

    override fun update(delta: Float) {
        children.forEach { it.update(delta) }
    }

    fun render(delta: Float, batch: ModelBatch, context: SceneContext) {
        components.filter { it.renderer }.forEach {
            (it).render(batch, context.perspectiveCamera, context)
        }
        children.forEach { it.render(delta,batch,context )}
    }

    override fun iterator(): Iterator<GameObject> {
        return GameObjectIterator(this)
    }

    fun <T:Component> get(type:KClass<T>)  : T? {
        return getComponents().find { it::class == type } as T?
    }

    private class GameObjectIterator(root: GameObject) : Iterator<GameObject> {

        private val stack: Stack<GameObject>

        init {
            stack = Stack()
            stack.push(root)
        }

        override fun hasNext(): Boolean {
            return !stack.isEmpty()
        }

        override fun next(): GameObject {
            val top = stack.pop()
            if (top.getChildren().notEmpty()) {
                top.children.forEach(stack::push)
            }

            return top
        }

    }

}

