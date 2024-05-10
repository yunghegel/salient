package org.yunghegel.salient.engine.ui.tree

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.Layout
import com.badlogic.gdx.utils.OrderedSet
import dev.lyze.gdxtinyvg.utils.WhitePixelUtils
import ktx.actors.onChange
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.ext.alpha
import org.yunghegel.gdx.utils.ext.createColorPixel
import org.yunghegel.gdx.utils.ui.TreeEx
import org.yunghegel.salient.engine.ui.UI
import kotlin.math.max
import kotlin.math.min

@Suppress("UNCHECKED_CAST")
abstract class TreeWidget<Node, Object, A >(rootobject: Object) : TreeEx<Node, Object>(UI.skin) where Node : TreeNode<Object,A>, A: TreeActor<Object> {

    val dnd = DragAndDrop()

    var parentRef : Actor? = null

    val white = createColorPixel(Color(1f,1f,1f, 0.2f))

    abstract val resolveParent : (Object) -> Object?

    abstract val resolveChildren : (Object) -> List<Object>?

    var root : Node = constructNode(rootobject)

    val map : MutableMap<Object,Node> = mutableMapOf()

    var _padding = 4f

    val changeActor = object : Actor() {

        init {
            onChange { handleSelection( selection.items()) }
            setPadding(_padding)

        }



    }

    init {
        setIconSpacing(5f,2f)
        ySpacing = 4f
        indentSpacing = 15f

        selection.setActor(changeActor)

        add(root)
        map[rootobject] = root
        buildTree(root, rootobject)
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

    fun buildTree(node: Node, obj: Object) {
        resolveChildren(obj)?.forEach {
            val child = constructNode(it)
            node.add(child)
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