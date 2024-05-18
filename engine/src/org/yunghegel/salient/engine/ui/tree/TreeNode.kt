package org.yunghegel.salient.engine.ui.tree

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.gdx.utils.ui.TreeEx
import org.yunghegel.salient.engine.ui.DragAndDropActor
import org.yunghegel.salient.engine.ui.scene2d.SLabel

@Suppress("UNCHECKED_CAST")
abstract class TreeNode<T,A : TreeActor<T>>(val id: String? = null, var obj :T, actor: A) : TreeEx.Node<TreeNode<T,A>,T,TreeActor<T>>(actor), DragAndDropActor<TreeActor<T>> {

    var nodeName = id ?: obj!!::class.simpleName ?: "Unknown"
        set(value) {
            field = value
            label.setText(value)

        }

    val label = SLabel(nodeName)
    override fun setExpanded(expanded: Boolean) {

        super.setExpanded(expanded)
        if (tree != null) tree.fire(ChangeEvent())
    }

    val map : MutableMap<out TreeNode<T,A>, Any> = mutableMapOf()

    var dragging = false

    var hovered = false

    override val actorClass: Class<TreeActor<T>> = TreeActor::class.java as Class<TreeActor<T>>


    init {
        actor.node = this
    }

    override val target: DragAndDrop.Target = object : DragAndDrop.Target(actor) {
        override fun drag(
            source: DragAndDrop.Source?,
            payload: DragAndDrop.Payload?,
            x: Float,
            y: Float,
            pointer: Int
        ): Boolean {
            return shouldAcceptDrag(source?.actor as DragAndDropActor<*>)
        }

        override fun drop(
            source: DragAndDrop.Source?,
            payload: DragAndDrop.Payload?,
            x: Float,
            y: Float,
            pointer: Int
        ) {
            shouldAcceptDrop(source?.actor as DragAndDropActor<*>)
        }
    }
    override val validDropTargets: List<Class<out DragAndDropActor<out Actor>>> = listOf(TreeActor::class.java as Class<out DragAndDropActor<out Actor>>)
    override val validDropSources: List<Class<out DragAndDropActor<out Actor>>> = listOf(TreeActor::class.java as Class<out DragAndDropActor<out Actor>>)


    override fun onDrop(source: DragAndDropActor<*>, payLoad: DragAndDrop.Payload, x: Float, y: Float) {
        val sourceNode = source as TreeNode<T,A>
        val sourceActor = sourceNode.actor
        val sourceParent = sourceNode.parent
        sourceParent.remove(sourceNode)
        add(sourceNode)

    }

    override fun onDrag(payLoad: DragAndDrop.Payload, x: Float, y: Float) {
        actor.drag(x.toInt(),y.toInt())
    }
}