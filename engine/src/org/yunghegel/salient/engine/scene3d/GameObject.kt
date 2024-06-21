package org.yunghegel.salient.engine.scene3d

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Matrix4
import org.yunghegel.gdx.utils.data.EnumBitmask
import org.yunghegel.gdx.utils.data.EnumMask
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.api.Store
import org.yunghegel.salient.engine.api.Tagged
import org.yunghegel.salient.engine.api.UpdateRoutine
import org.yunghegel.salient.engine.api.dto.GameObjectDTO
import org.yunghegel.salient.engine.api.dto.component.ModelComponentDTO
import org.yunghegel.salient.engine.api.dto.datatypes.Matrix4Data
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.api.flags.*
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.scene.GameObjectChildAddedEvent
import org.yunghegel.salient.engine.events.scene.GameObjectComponentAddedEvent
import org.yunghegel.salient.engine.events.scene.GameObjectComponentRemovedEvent
import org.yunghegel.salient.engine.scene3d.graph.Spatial
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.gdx.utils.ui.Hoverable
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.dto.component.MaterialComponentDTO
import org.yunghegel.salient.engine.api.ecs.ComponentCloneable
import org.yunghegel.salient.engine.scene3d.component.*
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.ifSuccess
import org.yunghegel.salient.engine.ui.Icon
import java.util.*
import kotlin.reflect.KClass

const val VISIBLE = 1


open class GameObject(name: String, transform: Matrix4 = Matrix4(), val scene:EditorScene) : Spatial<GameObject>(name), Iterable<GameObject>, UpdateRoutine, Tagged,
    EnumMask<GameObjectFlag>, Store, Hoverable.HoverQueryable, Icon, Cloneable, Deferrable {

    override val iconName: String
        get() = when {
            taggedAny("point_light", "spot_light", "directional_light") -> "light_object"
            tagged("camera") -> "camera_object"
            tagged("model") -> "geometry"
            tagged("root") -> "scene_tree"
            else -> "transform_object"
        }

    override val deferred: Stack<Deferred> = Stack()

    public override fun clone(): GameObject {
        val go = GameObject(name + "Copy", combined.cpy(), scene)
        val modelComp = components.find { it is ModelComponent } as ModelComponent?
        if (modelComp === null) debug("ModelComponent not found in cloned gameObject with id ${go.id}")
        modelComp?.let {
            val copy = if (it.usedAsset == null) ModelComponent(it.meta.id, go) else ModelComponent(it.usedAsset!!, go)
            val renderable = RenderableComponent(it.value!!.instance,go)
            val meshes = MeshComponent(it.value!!.meshes,go)
            val mdlPickable = ModelRenderable(modelComp.value!!.instance, go)
            val pickable = PickableComponent(mdlPickable,go)
            val materials = MaterialsComponent(it.value!!.materials,go)
            val bounds = BoundsComponent(BoundsComponent.getBounds(it.value!!),go)
            val components : List<BaseComponent> = listOf(copy,renderable,meshes,pickable,materials,bounds)
            components.forEach { comp -> go.add(comp); debug("Added ${comp::class.simpleName} to cloned gameObject with id ${go.id}") }
        }




        return go
    }


    override val bitmask = EnumBitmask(GameObjectFlag::class.java)

    override val tags: MutableSet<String> = mutableSetOf()

    override val map: MutableMap<String, String> = mutableMapOf()

    override var id: Int = getUniqueID()

    val pipeline: Pipeline by lazy { inject() }

    val components: List<BaseComponent>
        get() = getComponents().filterIsInstance<BaseComponent>()

    private val engine: Engine = inject()


    init {
        add(TransformComponent(this))
        engine.addEntity(this)
        tag(name)
        setAll(DRAW_BOUNDS, DRAW_ORIGIN, RENDER, ALLOW_SELECTION)

    }

    override fun add(component: Component): Entity {
        post(GameObjectComponentAddedEvent(this, component))
        if (component is BaseComponent) component.onComponentAdded(this)
        if (component is ModelComponent) tag("model")
        if (component is PickableComponent) tag("pickable")
        if (component is LightComponent) tag("light")
        super.add(component)
        return this
    }

    fun recurse(fn: (GameObject) -> Unit) {
        fn(this)
        children.forEach { it.recurse(fn) }
    }

    override fun <T : Component> remove(componentClass: Class<T>?): T? {
        getComponent(componentClass)?.let { GameObjectComponentRemovedEvent(this, it) }
        val cmp = super.remove(componentClass)
        if (cmp is BaseComponent) cmp.onComponentRemoved(this)
        return cmp
    }

    override fun addChild(child: GameObject) {

        post(GameObjectChildAddedEvent(this, child))
        super.addChild(child)
    }

    override fun removeChild(child: GameObject) {
        post(GameObjectChildAddedEvent(this, child))
        super.removeChild(child)
    }

    override fun update(delta: Float) {
        children.forEach { it.update(delta) }
        execDeferred()
    }

    fun renderDepth(delta: Float, batch: ModelBatch, context: SceneContext) {
        components.filter { it.implDepth }.forEach { cmp ->
            with(context) {
                cmp.renderDepth(delta)
            }
        }
        children.forEach { it.renderDepth(delta, batch, context) }
    }

    fun renderColor(delta: Float, batch: ModelBatch, context: SceneContext) {
        components.filter { it.implColor }.forEach { cmp ->
            if (cmp.shouldRenderColor) {
                with(context) {
                    cmp.renderColor(delta)
                }
            }
        }
        children.forEach { it.renderColor(delta, batch, context) }
    }

    fun renderDebug(context: SceneContext) {
        components.filter { it.implDebug }.forEach { cmp ->
            if (cmp.shouldRenderDebug) {
                with(context) {
                    cmp.renderDebug(delta)
                }
            }
        }
        children.forEach { it.renderDebug(context) }
    }

    override fun iterator(): Iterator<GameObject> {
        return GameObjectIterator(this)
    }

    fun <T : Component> get(type: KClass<T>): T? {
        return getComponents().find { it::class == type } as T?
    }
    override fun toString(): String {
        return "GameObject[$name:$id:(${tags.joinToString { "," }})]"
    }

    companion object {

        private val trackedIDs = mutableSetOf<Int>()

        fun checkID(id: Int) = trackedIDs.contains(id)

        fun getUniqueID(): Int {
            return gameObjectCount
        }

        private var gameObjectCount = 0
            get() {
                val id = field++
                println("GameObject count: $id")
                return id
            }

        fun fromDTO(dto: GameObjectDTO, scene: EditorScene): GameObject {

            val go = GameObject(dto.name, Matrix4(), scene)
            go.id = dto.id.toInt()
            go.combined.set(Matrix4Data.toMat4(dto.transform))
            dto.tags.forEach { go.tag(it) }
            dto.children.forEach { go.addChild(fromDTO(it, scene)) }
            dto.components.each {
                info("Component type: ${it.type}")
                when (it.type) {
                    "ModelComponent" -> ModelComponent.fromDTO(it as ModelComponentDTO, scene, go)?.let { it1 ->
                        go.add(it1)
                        info("ModelComponent added to GameObject")
                    }
                    "MaterialsComponent" -> {
                        val matdto = it as MaterialComponentDTO
                        go.get(MaterialsComponent::class)?.let { comp ->
                            matdto.usages.forEach { id ->

                            }
                        }
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
            model.components.forEach {
                when (it) {
                    is ModelComponent -> {
                        dto.components.add(ModelComponent.toDTO(it, model))
                        info("ModelComponent added to DTO")
                    }
                    is MaterialsComponent -> {
                        val matdto = MaterialComponentDTO()
                        it.materials.forEach {
                            info("serializing material ${it.id}")
                            matdto.usages.add(it.id)
                        }
                        matdto.type = "MaterialsComponent"
                        dto.components.add(matdto)
                    }
                }
            }
            model.children.forEach { dto.children.add(toDTO(it)) }

            return dto
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
    override var isHovered: (x: Float, y: Float) -> Boolean = {  x, y -> false }

}

