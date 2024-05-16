package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Selection
import ktx.actors.onChange
import ktx.collections.GdxArray
import ktx.collections.toGdxArray
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.gdx.utils.ext.singleOrNull
import org.yunghegel.salient.editor.scene.GameObjectSelectionManager
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.engine.api.UI_SOURCE
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.scene.*
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.scene3d.component.LightComponent
import org.yunghegel.salient.engine.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.scene3d.component.MeshComponent
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.scene3d.events.onGameObjectAdded
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.singleton
import org.yunghegel.salient.engine.ui.UI
import kotlin.math.max
import kotlin.reflect.KClass

class SceneGraphTree(private var graph: SceneGraph) : Tree<SNode<*, *, GameObject>, Any>(UI.skin) {



    val nodeMap = mutableMapOf<GameObject, GameObjectNode>()

    var root : GameObjectNode

    private var tmp : Selection<GameObject> = Selection()

    val widthSupplier : ()->Float  = { width - plusMinusWidth()-(indentSpacing)}

    val selectionManager : GameObjectSelectionManager = GameObjectSelectionManager(tmp)

    init {
        singleton(this)
        setIconSpacing(5f, 2f)
        setPadding(5f, 0f)
        addListeners()
        selection.multiple = true
        root = GameObjectNode(graph.root)
        buildTree(graph.root,null)

        onSingleGameObjectSelected { go ->
            if (!selection.contains(nodeMap[go])) {
                selection.add(nodeMap[go])
            }
            selection.set(nodeMap[go])
        }

        onChange {

            if (selection.isEmpty) {
                selectionManager.selection.items().forEach {
                    selectionManager.deselect(it)
                }
            } else {
                selectionManager.selection.clear()
                for (node in selection.items().toList()) {
                    selectionManager.select(node.obj,true)
                }
            }

        }

        onGameObjectAdded { event ->
          validateOrCreate(event.go)
        }
        graph.sceneTree = this
    }

    private fun plusMinusWidth() : Float {
        var width = max(style.plus.minWidth, style.minus.minWidth)
        if (style.plusOver != null) width = max(width, style.plusOver.minWidth);
        if (style.minusOver != null) width = max(width, style.minusOver.minWidth);
        return width
    }

    private fun buildTree(go: GameObject, parent: GameObjectNode?, index : Int = 0) {
        info("Building tree for ${go.name}")
        val node = GameObjectNode(go)
        node.actor.widthSupplier = widthSupplier

        nodeMap[go] = node
        populateComponents(node)
        if (parent != null) {
            parent.add(node)
        } else {
            add(node)
        }
        go.getChildren().forEach {
            buildTree(it,node)
        }
    }

    fun rebuild(root: GameObject) {
        clearChildren()
        nodeMap.clear()
        buildTree(root,null)
    }
    
    

    private fun validateOrCreate(go: GameObject) {
        val parentNode : GameObjectNode?
        parentNode = if (go.getParent()!=null) {
            nodeMap[go.getParent()]
        } else null
        val node =  nodeMap[go] ?: GameObjectNode(go)
        if(parentNode==null) root.add(node) else parentNode.add(node)
        populateComponents(node)
    }


    private fun populateComponents(go: GameObjectNode) {
        go.clearChildren()
        go.go.components.filter { comp -> valid(comp)}.forEach { component ->
            val node = go.map[component] ?: ComponentNode(component,go.go)
            go.add(node)
        }
    }

    private fun populateChildren(go: GameObjectNode) {
        go.go.getChildren().forEach {
            val node = GameObjectNode(it)
            nodeMap[it] = node
            populateComponents(node)
            go.add(node)
        }
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
                } else if (node is ComponentNode) {
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

    fun handleComponentSelect(node: ComponentNode,right: Boolean) {

    }

    fun handleGameObjectSelect(node: GameObjectNode,right: Boolean) {


    }


    companion object  {

        private val validComponents : List<KClass<out EntityComponent<*>>> = listOf(
            ModelComponent::class,
            LightComponent::class,
            MaterialsComponent::class,
            MeshComponent::class)
        val valid = { comp : Component ->  (comp is EntityComponent<*>)  and  (validComponents.contains(comp::class))  }

    }

}