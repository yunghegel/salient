package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.ray3k.stripe.PopTable
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.api.ecs.TagsComponent
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.scene3d.component.MeshComponent
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.TreeNodeTable
import org.yunghegel.salient.engine.ui.widgets.menu.ContextMenu

abstract class SNode<T, A: Actor,V>(actor:A,val obj:V) : Tree.Node<SNode<T, A,V>,T,A>(actor) {

    abstract val name : String
    abstract val iconName : String

    var index = 0

    init {
        isExpanded = true
    }

    override fun setExpanded(expanded: Boolean) {
        super.setExpanded(expanded)
    }

    companion object {

        fun loadIcon(name: String): SImageButton {
            val icon = SImageButton(name)
            val copy = ImageButtonStyle(icon.style)
            copy.up = null
            copy.down = null
            copy.checked = null
            icon.style = copy
            return icon
        }

        fun resolveComponentIcon(component: BaseComponent) : String {
            return when (component::class) {
                MaterialsComponent::class -> "material_object"
                ModelComponent::class -> "model-geometry"
                TransformComponent::class -> "transform_object"
                MeshComponent::class -> "mesh_object"
                else -> "cube"
            }
        }

        fun resolveGameObjectIcon(go: GameObject) : String {

            val tags = go[TagsComponent::class] ?: return "transform_object"




            if (tags.taggedAny("point_light", "spot_light", "directional_light")) return "light_object"
            if (tags tagged "camera") return "camera_object"
            if (tags tagged "model") return "geometry"
            if (tags tagged "camera") return "camera_object"
            if (tags tagged "root") return "scene_tree"
                else return "transform_object"

        }



    }

}

open class GameObjectNode(val go: GameObject) : SNode<SNode<*, *, GameObject>, TreeNodeTable, GameObject>(TreeNodeTable(go),go) {

    override val name: String = go.name

    override val iconName: String = resolveGameObjectIcon(go)

    val map = mutableMapOf<BaseComponent, ComponentNode>()

    val listener = ContextMenu()
    val menu : PopTable = listener.popTable

    init {
        actor.createIcon(loadIcon(iconName))
        actor.createLabel(name)
        actor.build()
//        actor.add(loadIcon(iconName)).right()
//        actor.add(name).growX()
        actor.addListener(listener)

    }

    override fun add(node: SNode<SNode<*, *, GameObject>, TreeNodeTable, GameObject>?) {
        if  (node is ComponentNode) {
            if (SceneGraphTree.valid(node.component))
            map[node.component] = node

        }
        super.add(node)
    }

    override fun remove(node: SNode<SNode<*, *, GameObject>, TreeNodeTable, GameObject>?) {
        node.takeIf { node is ComponentNode }?.let { it as ComponentNode
            val comp =  it.component
            map.remove(comp)
        }
        super.remove(node)
    }



}

class ComponentNode(val component: BaseComponent,val go: GameObject) : SNode<SNode<*, *, GameObject>, TreeNodeTable, GameObject>(
    TreeNodeTable(component),go) {

        override val name: String = "${component::class.simpleName}"

        override val iconName: String = resolveComponentIcon(component)

        init {
            actor.add(loadIcon(iconName)).right()
            actor.add(name).growX()
        }
}

