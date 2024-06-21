package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.gdx.utils.selection.Pickable
import org.yunghegel.gdx.utils.selection.PickerColorEncoder
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.api.ecs.ComponentCloneable
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.flags.*
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.ModelRenderable
import org.yunghegel.salient.engine.scene3d.SceneContext
import kotlin.reflect.KClass

class PickableComponent(val pickable: Pickable,go: GameObject) : EntityComponent<Pickable>(pickable,go), Pickable by pickable, ComponentCloneable<PickableComponent> {

    val allowPick : Boolean
        get() = go.has(ALLOW_SELECTION)

    private var source : Any? = null

    override val type: KClass<out BaseComponent> = PickableComponent::class

    override val id: Int = go.id


    init {
        implements(depth)
        depthCondition { go.getComponent(SelectedComponent::class.java) != null }
    }

    override fun apply(comp: PickableComponent, target: GameObject) {
        target.add(comp)
    }

    override fun clone(target: GameObject): PickableComponent {
       val model = target[ModelComponent::class] ?: throw Exception("Model must be present first")

        val mat = model.value?.materials?.forEach { mat ->
            val attr = PickerColorEncoder.encodeRaypickColorId(target.id)
            mat.set(attr)
        }
        return PickableComponent(pickable,target)
    }

    override fun onComponentRemoved(go: GameObject) {
        go.set(ALLOW_SELECTION)
    }

    override fun onComponentAdded(go: GameObject) {
        go.clear(ALLOW_SELECTION)
    }

    context(SceneContext) override fun renderDepth(delta: Float) {
        if (pickable is ModelRenderable) {
            depthBatch.render(pickable)
        }
    }

    override fun setSource(obj: Any) {
        source = obj
    }

    override fun getSource(): Any? {
        return source
    }

}