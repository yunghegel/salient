package org.yunghegel.salient.editor.ui.scene.graph

import assimp.format.obj.Object
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import imgui.demo.ShowDemoWindowLayout.`Widgets Width`.f
import kool.a
import org.yunghegel.gdx.utils.ext.uniqueBy
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.*
import org.yunghegel.salient.engine.ui.Icon
import org.yunghegel.salient.engine.ui.tree.TreeNode
import kotlin.reflect.KClass

open class ObjectNode(go: GameObject, table: ObjectTable? = null, name: String = "${go.name}:${go.id}" ) :  TreeNode<GameObject,ObjectTable>(name, go,table ?: ObjectTable(go)) ,
    Icon by go {

        val componentNodes = mutableMapOf<KClass<out EntityComponent<*>>, ObjectComponentNode>()

        fun populate() {
            actor.buildActor(obj)
            if (this is ObjectComponentNode) return
            obj.components.filterIsInstance<EntityComponent<*>>().uniqueBy { it::class }.forEach { component ->
                val accepted = listOf(TransformComponent::class,ModelComponent::class,MaterialsComponent::class,LightComponent::class,MeshComponent::class,)
                if (component::class !in accepted) return@forEach
                val node = ObjectComponentNode(component)
                componentNodes[component::class] = node
                add(node)
            }
            componentNodes.values.forEach { it.populate(); it.actor.node = it }
        }

    override fun setExpanded(expanded: Boolean) {
        super.setExpanded(expanded)
        tree?.let { it.fire(ChangeEvent()) }
    }
}