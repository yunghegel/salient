package org.yunghegel.salient.engine.graphics.scene3d

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.api.Tagged
import org.yunghegel.salient.engine.api.UpdateRoutine
import org.yunghegel.salient.engine.api.dto.GameObjectDTO
import org.yunghegel.salient.engine.api.dto.component.ModelComponentDTO
import org.yunghegel.salient.engine.api.dto.datatypes.Matrix4Data
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.scene.GameObjectChildAddedEvent
import org.yunghegel.salient.engine.events.scene.GameObjectComponentAddedEvent
import org.yunghegel.salient.engine.events.scene.GameObjectComponentRemovedEvent
import org.yunghegel.salient.engine.graphics.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.graphics.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.graphics.scene3d.graph.Spatial
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject
import java.util.*
import kotlin.reflect.KClass

class GameObject(name: String, id: Int? = null,val scene:EditorScene) : Spatial<GameObject>(name), Iterable<GameObject>, UpdateRoutine, Tagged {

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

    override fun add(component: Component): Entity {
        post(GameObjectComponentAddedEvent(this,component))
        super.add(component)
        return this
    }

    override fun <T : Component?> remove(componentClass: Class<T>?): T {
        getComponent(componentClass)?.let { GameObjectComponentRemovedEvent(this, it) }
        return super.remove(componentClass)
    }

    override fun addChild(child: GameObject) {

        post(GameObjectChildAddedEvent(this,child))
        super.addChild(child)
    }

    override fun removeChild(child: GameObject) {
        post(GameObjectChildAddedEvent(this,child))
        super.removeChild(child)
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


    companion object {
        fun fromDTO(dto: GameObjectDTO, scene: EditorScene): GameObject {
            val go = GameObject(dto.name,dto.id.toInt(),scene)
            go.combined.set(Matrix4Data.toMat4(dto.transform))
            dto.tags.forEach { go.tag(it) }
            dto.children.forEach { go.addChild(fromDTO(it,scene)) }
            dto.components.each {
                info("Component type: ${it.type}")
                when(it.type) {
                    "ModelComponent" -> ModelComponent.fromDTO(it as ModelComponentDTO,scene,go)?.let { it1 ->
                        go.add(it1)
                        info("ModelComponent added to GameObject")
                    }
                }
            }
            return go
        }

        fun toDTO(model: GameObject): GameObjectDTO {
            val dto = GameObjectDTO()
            dto.name = model.name
            dto.id = model.id.toString()
            dto.transform = Matrix4Data.fromMat4(model.combined)
            model.tags.forEach { dto.tags.add(it) }
            model.getComponents().forEach {
                when(it) {
                    is ModelComponent ->{
                        dto.components.add(ModelComponent.toDTO(it,model))
                        info("ModelComponent added to DTO")
                    }
                }
            }
            model.children.forEach { dto.children.add(toDTO(it)) }

            return dto
        }
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

