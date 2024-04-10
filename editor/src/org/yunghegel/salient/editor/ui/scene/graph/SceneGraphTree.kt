package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.IntMap
import ktx.collections.isNotEmpty
import org.yunghegel.salient.editor.input.Input
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.events.onGameObjectAdded
import org.yunghegel.salient.engine.io.debug
import org.yunghegel.salient.engine.io.info
import org.yunghegel.salient.engine.ui.UI

class SceneGraphTree(private var graph: SceneGraph) : Tree<SNode<*, *>, Any>(UI.skin) {

    val nodeMap = mutableMapOf<GameObject, GameObjectNode>()

    val visualIndices : IntMap<SNode<*, *>> = IntMap()

    var root : GameObjectNode

    init {
        setIconSpacing(5f, 2f)
        setPadding(5f, 0f)
        addListeners()
        selection.multiple = true

        root = GameObjectNode(graph.root)
        buildTree(graph.root,null)
        onGameObjectAdded { event->
            validateOrAdd(event.go,event.parent)
            calculateVisualIndices()
        }
    }

    fun buildTree(go:GameObject, parent: GameObjectNode?, index : Int = 0) {
        info("Building tree for ${go.name}")
        val node = GameObjectNode(go)
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

    fun validateOrAdd(go:GameObject,parent:GameObject?) {
        if (nodeMap.containsKey(go)) {
            info("GameObject already exists in tree")
            return
        }
        info("Adding new GameObject to tree")
        val node = GameObjectNode(go)
        nodeMap[go] = node
        populateComponents(node)
        if (parent != null) {
            nodeMap[parent]?.add(node)
        } else {
            add(node)
        }
    }

    fun allNodesBetween(start: SNode<*, *>, end: SNode<*, *>): List<SNode<*, *>> {
        val nodes = mutableListOf<SNode<*, *>>()
        var node = start
        while (node != end) {
            nodes.add(node)
            node = getPreviousNodeVisual(node) ?: break
        }
        return nodes
    }

    fun getNextNodeVisual(node: SNode<*, *>): SNode<*, *>? {
        val peers = listPeers(node)
        val index = peers.indexOf(node)
        val nodeAfterThis = peers.getOrNull(index + 1)
        if (nodeAfterThis != null) return nodeAfterThis
//        check children
        if (node.children.isNotEmpty()) return node.children.first()
        var parent = node.parent
        while (parent != null) {
            val parentPeers = listPeers(parent)
            val parentIndex = parentPeers.indexOf(parent)
            val parentAfterThis = parentPeers.getOrNull(parentIndex + 1)
            if (parentAfterThis != null) return parentAfterThis
            parent = parent.parent
        }
        return null
    }

    fun getPreviousNodeVisual(node: SNode<*, *>): SNode<*, *>? {
        val peers = listPeers(node)
        val index = peers.indexOf(node)
        val nodeBeforeThis = peers.getOrNull(index - 1)
        if (nodeBeforeThis != null) return nodeBeforeThis
        val parent = node.parent
        if (parent != null) return parent
        return null
    }

    fun calculateVisualIndices(go:GameObject = graph.root,parent: GameObject?=null, index: Int = 0): Int{
        val node = nodeMap[go]
        node?.index = index
        visualIndices.put(index,node)
        var i = index
        println("$i ${go.name}")
        if(node!!.isExpanded) {
            go.components.forEach {
                val componentNode = node.map[it]
                componentNode?.index = i+1
                visualIndices.put(i+1,componentNode)
                println("${i+1} ${it::class.simpleName}")
                i++

            }
        }
        go.getChildren().forEach {
            i = calculateVisualIndices(it,go,i+1)
        }
        return i
    }

    fun hasParent(node: SNode<*, *>): Boolean {
        return node.parent != null
    }

    fun listPeers(node: SNode<*, *>): List<SNode<*, *>> {
        if (!hasParent(node)) return root.children.toList()
        return node.parent.children.toList()
    }

    fun populateComponents(go: GameObjectNode) {
        go.go.components.forEach {
            debug("Adding component node: ${it::class.simpleName}")
            val node = ComponentNode(it,go.go)
            go.map[it] = node
            go.add(node)
        }
    }

    fun addListeners() {
        addListener(object:ClickListener(){

            var doubleClick = {tapCount==2}

            var key = -1

            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val node = getNodeAt(y) ?: return
                println(getNextNodeVisual(node)?.name)
                allNodesBetween(node,root!!).forEach {
                    println(it.name)
                }

                if (doubleClick()) {
                    info("Double click detected")
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
                    handleGameObjectSelect(node)
                } else if (node is ComponentNode) {
                    handleComponentSelect(node)
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

    fun handleComponentSelect(node: ComponentNode) {
        info("Component node selected")
    }

    fun handleGameObjectSelect(node: GameObjectNode) {
        info("GameObject node selected")
    }


}