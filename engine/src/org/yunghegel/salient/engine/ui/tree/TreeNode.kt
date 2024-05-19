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
abstract class TreeNode<T,A : TreeActor<T>>(val id: String? = null, var obj :T, actor: A) : TreeEx.Node<TreeNode<T,A>,T,TreeActor<T>>(actor) {

    var nodeName = id ?: obj!!::class.simpleName ?: "Unknown"
        set(value) {
            field = value
            label.setText(value)
        }

    val label = SLabel(nodeName)
    val icon = SLabel("")

    override fun setExpanded(expanded: Boolean) {
        super.setExpanded(expanded)
        if (tree != null) tree.fire(ChangeEvent())
    }

    val map : MutableMap<out TreeNode<T,A>, Any> = mutableMapOf()

    var dragging = false

    var hovered = false


    init {
        actor.node = this
    }


}