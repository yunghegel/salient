package org.yunghegel.salient.engine.scene3d

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.yunghegel.salient.engine.RendererRoutine
import org.yunghegel.salient.engine.UpdateRoutine
import org.yunghegel.salient.engine.ecs.EntityComponent
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.scene3d.graph.Spatial
import java.util.*

class GameObject(name: String, id: Int? = null) : Spatial<GameObject>(name), Iterable<GameObject>, UpdateRoutine {

    override val id: Int = id ?: generateID()

    val components : List<EntityComponent<*>>
        get() = getComponents().filterIsInstance<EntityComponent<*>>()

    init {
        add(TransformComponent(combined, this))
    }

    override fun update(delta: Float) {
        children.forEach { it.update(delta) }
    }

    fun render(delta: Float, batch: ModelBatch, camera: Camera, context: SceneContext) {
        components.filter { it is RenderableComponent}.forEach {
            (it as RenderableComponent).render(batch, camera, context)
        }
        children.forEach { it.render(delta,batch,camera,context )}
    }

    override fun iterator(): Iterator<GameObject> {
        return GameObjectIterator(this)
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