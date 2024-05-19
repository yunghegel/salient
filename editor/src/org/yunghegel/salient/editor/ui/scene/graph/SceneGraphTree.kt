package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.Selection
import com.badlogic.gdx.utils.Align.right
import com.badlogic.gdx.utils.OrderedSet
import com.ray3k.stripe.PopTable
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.editor.scene.GameObjectSelectionManager
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.flags.GameObjectFlag
import org.yunghegel.salient.engine.events.scene.*
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.scene3d.component.LightComponent
import org.yunghegel.salient.engine.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.scene3d.component.MeshComponent
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.scene3d.events.GameObjectAdded
import org.yunghegel.salient.engine.scene3d.events.onGameObjectAdded
import org.yunghegel.salient.engine.scene3d.events.onGameObjectRemoved
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.singleton
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.tree.TreeActor
import org.yunghegel.salient.engine.ui.tree.TreeNode
import org.yunghegel.salient.engine.ui.tree.TreeWidget
import org.yunghegel.salient.engine.ui.widgets.menu.ContextMenu
import kotlin.math.max
import kotlin.reflect.KClass

class SceneGraphTree(private var graph: SceneGraph) : TreeWidget<TreeNode<GameObject,SceneGraphTree.GameObjectTable>,GameObject,SceneGraphTree.GameObjectTable>(graph.root) {





    val nodeMap = mutableMapOf<GameObject, GameObjectNode>()


    override val resolveParent: (GameObject) -> GameObject? = { go -> go.getParent() }

    override val resolveChildren: (GameObject) -> List<GameObject> = { go ->
        if (go.getChildren().isEmpty)  emptyList<GameObject>()
        go.getChildren().toList()
    }

    override var root: TreeNode<GameObject, GameObjectTable> = GameObjectNode("root",graph.root,GameObjectTable(graph.root))z


    private var tmp : Selection<GameObject> = Selection()

    val widthSupplier : ()->Float  = { width - plusMinusWidth()-(indentSpacing)}

    val selectionManager : GameObjectSelectionManager = GameObjectSelectionManager(tmp)

    init {
        singleton(this)
        setIconSpacing(5f, 2f)
        setPadding(5f, 0f)
        addListeners()
        selection.multiple = true
        graph.sceneTree = this


    }


    fun build(obj: GameObject, parent: GameObject?=null) {
        val node = constructNode(obj)
        if (parent == null) {
            root.add(node)
        } else {
            val parentNode = map[parent] ?: return
            parentNode.add(node)
        }

        for (child in obj.getChildren()) {
            build(child,obj)
        }
    }

    override fun handleSelection(selection: OrderedSet<TreeNode<GameObject, GameObjectTable>>) {
        selection.forEach { node ->
            if (node is GameObjectNode) {
                selectionManager.select(node.obj)
            }
        }
    }

    override fun constructNode(obj: GameObject): TreeNode<GameObject, GameObjectTable> {
        val table = GameObjectTable(obj)
        table.buildActor(obj)
        val node = GameObjectNode(obj.id.toString(),obj,table)
        map[obj] = node
        obj.components.filterIsInstance<EntityComponent<*>>().forEach { component ->
            val componentTable = SceneGraphTree.ComponentTable(component,obj)
            componentTable.buildActor(obj)
            val componentNode = ComponentObjectNode(component::class.simpleName!!,obj,component,componentTable)
            node.add(componentNode)
        }

        obj.getChildren()?.forEach { child ->
            val childNode = constructNode(child)
            node.add(childNode)
        }
        return node
    }

    private fun plusMinusWidth() : Float {
        var width = max(style.plus.minWidth, style.minus.minWidth)
        if (style.plusOver != null) width = max(width, style.plusOver.minWidth);
        if (style.minusOver != null) width = max(width, style.minusOver.minWidth);
        return width
    }




    private fun addListeners() {
        addListener(object:ClickListener(){

            var doubleClick = {tapCount==2}

            var rightClick = button == Input.Buttons.RIGHT

            var key = -1

            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val node = getNodeAt(y) ?: return

                if (doubleClick()) {
                    if (selection.contains(node)) {
                        selection.remove(node)
                    } else {
                        selection.add(node)
                    }
                }

                if (key == Input.Keys.SHIFT_LEFT) {
                    if (selection.multiple) {
                        selection.add(node)
                    }
                }

                if (node is GameObjectNode) {
                    handleGameObjectSelect(node,rightClick)
                } else if (node is ComponentObjectNode) {
                    handleComponentSelect(node,rightClick)
                }
            }

            override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                key = keycode
                return super.keyDown(event, keycode)
            }

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
                key = -1
                return super.keyUp(event, keycode)
            }

        })
    }

    fun handleComponentSelect(node: ComponentObjectNode, right: Boolean) {

    }

    fun handleGameObjectSelect(node: GameObjectNode,right: Boolean) {


    }

    open class GameObjectTable(val gameObject: GameObject) : TreeActor<GameObject>(gameObject) {

        override var title: SLabel = SLabel(gameObject.name)
        override var icon: Drawable? = UI.drawable(gameObject.iconName)

        open val overflow = SImageButton("overflow-menu")
        open val visibleToggle = SImageButton("eye")
        open val lockToggle = SImageButton("lock")

        open val left = table()
        open val right = table()



        override fun buildActor(obj: GameObject) {
            iconImage = SImage(icon!!, 18)
            left.add(iconImage).size(20f)
            left.add(title)
            add(left).pad(1f)
            add(right).growX().padHorizontal(3f)
            add(overflow).padHorizontal(2f)
            right.add(lockToggle).padHorizontal(2f)
            right.add(visibleToggle).padHorizontal(2f)
            lockToggle.onChange { if (lockToggle.isChecked) {
                obj.set(GameObjectFlag.LOCKED)
            } else {
                obj.clear(GameObjectFlag.LOCKED)
            } }
            visibleToggle.onChange { if (visibleToggle.isChecked) {
                obj.set(GameObjectFlag.RENDER)
            } else {
                obj.clear(GameObjectFlag.RENDER)
            } }
        }

    }
    open class ComponentTable(val component: EntityComponent<*>, obj : GameObject) :GameObjectTable(obj) {

        override var title: SLabel = SLabel("${component::class.simpleName}".substringBeforeLast("Component"))
        override var icon: Drawable? = UI.drawable(component.iconName)
        override var iconImage: SImage = SImage(icon!!, 18)

        override val overflow = SImageButton("overflow-menu")
        override val visibleToggle = SImageButton("eye")
        override val lockToggle = SImageButton("lock")

        override val left = table()
        override val right = table()

        override fun buildActor(obj: GameObject) {
            left.add(iconImage).size(20f)
            left.add(title)
            add(left).pad(1f)
            add(right).growX().padHorizontal(3f)
            add(overflow).padHorizontal(2f)
            right.add(lockToggle).padHorizontal(2f)
            right.add(visibleToggle).padHorizontal(2f)
            lockToggle.onChange { if (lockToggle.isChecked) {
                obj.set(GameObjectFlag.LOCKED)
            } else {
                obj.clear(GameObjectFlag.LOCKED)
            } }
            visibleToggle.onChange { if (visibleToggle.isChecked) {
                obj.set(GameObjectFlag.RENDER)
            } else {
                obj.clear(GameObjectFlag.RENDER)
            } }
        }

    }

    open class GameObjectNode(name:String, obj: GameObject, actor: GameObjectTable) : TreeNode<GameObject,GameObjectTable>(obj.id.toString()!!,obj,actor) {

        val componenttMap = mutableMapOf<EntityComponent<*>, ComponentObjectNode>()

        val listener = ContextMenu()
        val menu : PopTable = listener.popTable

        init {

        }

        fun create() {
            actor.buildActor(obj)
            actor.addListener(listener)
            populateChildrenComponents()
        }

        fun populateChildrenComponents() {
            obj.components.filterIsInstance<EntityComponent<*>>().forEach { component ->
                val componentTable = ComponentTable(component,obj)
                componentTable.buildActor(obj)
                val componentNode = ComponentObjectNode(component::class.simpleName!!,obj,component,componentTable)
                add(componentNode)
            }
        }

        override fun add(node: TreeNode<GameObject, GameObjectTable>) {
            if (node is ComponentObjectNode) {
                componenttMap[node.value] = node
            }
            super.add(node)
        }

        override fun remove(node: TreeNode<GameObject, GameObjectTable>) {
            if (node is ComponentObjectNode) {
                componenttMap.remove(node.value)
            }
            super.remove(node)
        }

    }
    class ComponentObjectNode(name:String,go:GameObject, val value: EntityComponent<*>, actor: ComponentTable) : TreeNode<GameObject, SceneGraphTree.GameObjectTable>(name,go,actor)
    companion object  {

        private val validComponents : List<KClass<out EntityComponent<*>>> = listOf(
            ModelComponent::class,
            LightComponent::class,
            MaterialsComponent::class,
            MeshComponent::class)
        val valid = { comp : Component ->  (comp is EntityComponent<*>)  and  (validComponents.contains(comp::class))  }

    }

}