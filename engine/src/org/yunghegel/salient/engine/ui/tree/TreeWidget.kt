package org.yunghegel.salient.engine.ui.tree

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.Layout
import com.badlogic.gdx.utils.OrderedSet
import ktx.actors.onChange
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.ext.createColorPixel
import org.yunghegel.gdx.utils.ext.drawable
import org.yunghegel.gdx.utils.ui.TreeEx
import org.yunghegel.salient.engine.ui.UI
import kotlin.math.max

@Suppress("UNCHECKED_CAST")
abstract class TreeWidget<Node, Object, A >(rootobject: Object) : TreeEx<Node, Object>(UI.skin) where Node : TreeNode<Object,A>, A: TreeActor<Object> {

    val dnd = DragAndDrop()

    var parentRef : Actor? = null

    val pix = createColorPixel(Color(.2f,.2f,.2f, 1f))

    abstract val resolveParent : (Object) -> Object?

    abstract val resolveChildren : (Object) -> List<Object>?
    val row : Drawable = UI.skin.drawable("selection-dark")


    val map : MutableMap<Object,Node> = mutableMapOf()
    open var root: Node = constructNode(rootobject)

    var _padding = 4f

    var visualCount = 0



    val changeActor = object : Actor() {
        init {
            onChange { handleSelection( selection.items()) }
            setPadding(_padding)
            dnd.addSource(object : DragAndDrop.Source(this) {
                override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload? {
                    val payload = DragAndDrop.Payload()
                    val over = getNodeAt(y)
                    payload.dragActor = (over.actor)
                    payload.`object` = over.obj
                    return payload
                }
            })
        }
    }

    init {
        setIconSpacing(5f,2f)
        ySpacing = 4f
        indentSpacing = 15f

        selection.setActor(changeActor)

        map[rootobject] = root

    }

    val rowWidth  = { node : Node ->
        var width = this.width - plusMinusWidth() - (node.level * indentSpacing)
        if (node.actor is Layout) {
            width -= (node.actor as Layout).prefWidth
        }
        width
    }

    abstract fun handleSelection(selection: OrderedSet<Node>)


    fun locate(obj: Object) : Node? {
        return map[obj]
    }

    fun locateParent(obj: Object) : Node? {
        return map[resolveParent(obj)]
    }

    fun checkNodeAdded(node: Node) : Boolean {
       return (findNode(node.obj) != null)
    }

    fun addIfNotPresent(item: Object) {
        if (map[item] == null) {
            val node = constructNode(item)
            map[item] = node
            val parent = locateParent(item)
            if (parent == null) {
                add(node)
            } else {
                parent.add(node)
            }
        }
    }

    abstract fun constructNode(obj: Object) : Node

    fun recurse (node: Node, action: (Node) -> Unit) {
        action(node)
        val children = node.children
        children.forEach {
            recurse(it as Node, action)
        }
    }

    fun buildTree(node: Node?, obj: Object, conf: (Node) -> Unit = {}) {
        resolveChildren(obj)?.forEach {
            val child = constructNode(it)
            conf(child)
            if (node != null) {
                node.add(child)
            } else {
                add(child)
            }
            map[it] = child
            buildTree(child, it)
        }
    }

    private fun plusMinusWidth(): Float {
        val style = style
        var width = max(style.plus.minWidth, style.minus.minWidth)
        if (style.plusOver != null) width = max(width, style.plusOver.minWidth)
        if (style.minusOver != null) width = max(width, style.minusOver.minWidth)
        return width
    }



    fun drawRows(nodes : GdxArray<Node>, batch: Batch, index : Int,alpha : Float) {
        var idx = index
        for (i in 0 until nodes.size) {
            idx++
            val node = nodes[i]
//            if (idx % 2 == 0) node.actor.drawRow(batch, white, x, node.actor.y + ySpacing/2, width, (node.height + ySpacing)-ySpacing/2)
            if (idx%2 ==0) node.actor.setBackground(UI.drawable("white-pixel",Color(1f,1f,1f,0.1f)))
            else node.actor.setBackground(UI.drawable("white-pixel",Color(1f,1f,1f,0.2f)))
            if (node.isExpanded && node.children.size > 0 ) {
                drawRows(node.children as GdxArray<Node>, batch,idx,alpha)
            }
        }
    }


}