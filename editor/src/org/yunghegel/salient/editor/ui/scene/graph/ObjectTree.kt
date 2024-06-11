package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.OrderedSet
import ktx.actors.onChange
import ktx.collections.isNotEmpty
import ktx.math.div
import ktx.math.minus
import ktx.math.plus
import org.yunghegel.gdx.utils.ext.getBounds
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.engine.api.flags.GameObjectFlag
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.events.onGameObjectAdded
import org.yunghegel.salient.engine.scene3d.events.onGameObjectRemoved
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.tree.TreeWidget
import java.security.GuardedObject

class ObjectTree(val graph : SceneGraph = inject()) : TreeWidget<ObjectNode,GameObject,ObjectTable> (graph.root) {

    override val resolveParent: (GameObject) -> GameObject? = { it.getParent() }

    override val resolveChildren: (GameObject) -> List<GameObject>? = { it.getChildren().toList() }

    override fun handleSelection(selection: OrderedSet<ObjectNode>) {
       val objects = selection.map { it.obj }
        objects.forEach { it.set(GameObjectFlag.SELECTED) }
        root.obj.forEach { if (it !in objects) it.clear(GameObjectFlag.SELECTED) }
    }

    override fun constructNode(obj: GameObject): ObjectNode {
        val node =  ObjectNode(obj)
        node.populate()
        node.actor.node = node
        return node
    }

    var visualOrder = mutableSetOf<ObjectNode>()

    init {
        setIconSpacing(5f, 2f)
        setPadding(5f, 0f)
        setYSpacing(2f)

        onGameObjectAdded { event ->
            if (event.go == graph.root) return@onGameObjectAdded
            val parent = event.parent
            val go = event.go
            val node = constructNode(go)
            val parentNode = map[parent]
            if (parentNode != null) {
                parentNode.add(node)
            } else {
                root.add(node)
            }
            map[go] = node
        }

        onGameObjectRemoved { event ->
            val go = event.go
            val node = map[go]
            if (node != null) {
                node.remove()
                map.remove(go)
            }
        }
        buildTree(root,graph.root)
        root.isExpanded = true

        root.isSelectable = false
        root.actor.touchable = Touchable.childrenOnly

        var count = 0
        onChange {
            visualOrder.clear()
            traverseVisible(root) { node ->
                (node as ObjectNode).actor.visualIndex = ++count
                visualOrder.add(node)
            }
            visualCount = count
            count =0
        }

        visualOrder.clear()
        traverseVisible(root) { node ->
            (node as ObjectNode).actor.visualIndex = ++count
            visualOrder.add(node)
        }
        visualCount = count
        count =0
//        iterateVisible(root) { it.actor.title.text.append("$count") }
    }

    override fun getPrefHeight(): Float {
        val frame = UI.root
        if (frame is Gui) {
            val pref = frame.sceneTree.graphPane.height
            return pref
        } else {
            return super.getPrefHeight()
        }
    }

    private fun validateOrCreate(go: GameObject) {
        val parentNode : ObjectNode?
        parentNode = if (go.getParent()!=null) {
            map[go.getParent()]
        } else null
        val node =  (map[go] as ObjectNode?)  ?: ObjectNode(go)
        if(parentNode==null) root.add(node) else parentNode.add(node)
        map[go] = node

        if (go.getChildren().isNotEmpty()) {
            go.getChildren().forEach {
                validateOrCreate(it)
            }
        }
    }

    fun traverseVisible(node: ObjectNode, action: (ObjectNode) -> Unit) {
        action(node)
        if (node.isExpanded) {
            node.children?.let { it.forEach { traverseVisible(it as ObjectNode, action) }  }
        }
    }


    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }
}