package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.graphics.scene3d.component.MeshComponent
import org.yunghegel.salient.engine.graphics.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.graphics.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.SImageButton

abstract class SNode<T, A: Actor,V>(actor:A,val obj:V) : Tree.Node<SNode<T, A,V>,T,A>(actor) {

    abstract val name : String
    abstract val iconName : String

    var index = 0

    init {
        isExpanded = true
    }

    override fun setExpanded(expanded: Boolean) {
        if (tree!=null) (tree as SceneGraphTree).calculateVisualIndices()
        super.setExpanded(expanded)
    }

    companion object {

        fun loadIcon(name: String): SImageButton {
            val icon =  SImageButton(name)
            val copy = ImageButtonStyle(icon.style)
            copy.up = null
            copy.down = null
            copy.checked = null
            icon.style = copy
            return icon
        }

        fun resolveComponentIcon(component: EntityComponent<*>) : String {
            return when (component::class) {
                MaterialsComponent::class -> "material_object"
                ModelComponent::class -> "model-geometry"
                TransformComponent::class -> "transform_object"
                MeshComponent::class -> "mesh_object"
                else -> "cube"
            }
        }

        fun resolveGameObjectIcon(go: GameObject) : String {




                if (go.taggedAny("point_light", "spot_light", "directional_light")) return "light_object"
                if (go tagged "camera") return "camera_object"
                if (go tagged "model") return "geometry"
                if (go tagged "camera") return "camera_object"
                if (go tagged "root") return "scene_tree"
                else return "transform_object"

        }



    }

}

open class GameObjectNode(val go:GameObject) : SNode<SNode<*, *,GameObject>, STable,GameObject>(STable(),go) {

    override val name: String = go.name

    override val iconName: String = resolveGameObjectIcon(go)

    val map = mutableMapOf<EntityComponent<*>, ComponentNode>()

    init {
        actor.add(loadIcon(iconName)).right()
        actor.add(name).growX()
    }

}

class ComponentNode(val component: EntityComponent<*>,val go:GameObject) : SNode<SNode<*, *,GameObject>, STable,GameObject>(
    STable(),go) {

        override val name: String = "${component::class.simpleName}"

        override val iconName: String = resolveComponentIcon(component)

        init {
            actor.add(loadIcon(iconName)).right()
            actor.add(name).growX()
        }
}

