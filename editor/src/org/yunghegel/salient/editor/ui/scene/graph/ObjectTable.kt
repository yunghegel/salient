package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.createColorPixel
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.api.flags.GameObjectFlag
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.tree.TreeActor
import org.yunghegel.salient.engine.ui.widgets.EditableTextField
import kotlin.math.min

open class ObjectTable(obj: GameObject) : TreeActor<GameObject>(obj) {

    open val button : SImageButton = SImageButton(obj.iconName)
    open val label : EditableTextField = EditableTextField(obj.name, { obj.name = it })


    val buttonLabelContainer = STable()
    val otherActorsTable = STable()
    val visibleToggle = SImageButton("eye")
    val lockToggle = SImageButton("lock")

    override val pixel: TextureRegion = createColorPixel(Color(0.18f, 0.18f, 0.18f, 1f))

    init {
        otherActorsTable.align(Align.right)

    }

    fun initOverflowOptions() {
        addOverflowOption("Delete", "Cleanup") {
            obj.scene.graph.removeGameObject(obj)
            node.remove()
        }
        addOverflowOption("Duplicate", "Copy") {
            val clone = obj.clone()
            obj.scene.graph.addGameObject(clone, obj.getParent())
        }

    }

    override fun buildActor(obj: GameObject) {
        button.style.up = null
        button.style.down = null
        button.style.checked = null
        button.style.over = null

        buttonLabelContainer.add(button).padRight(5f).size(20f)
        buttonLabelContainer.add(label)
        add(buttonLabelContainer).pad(1f)
        add(otherActorsTable).growX().padHorizontal(3f)
        createOverflowMenu() {
            cell ->
            cell.size(16f)
        }
        if (this is ComponentTable) return
        otherActorsTable.add(lockToggle).padHorizontal(2f)
        otherActorsTable.add(visibleToggle).padHorizontal(2f)
        lockToggle.onChange { if (lockToggle.isChecked) {
            obj.set(GameObjectFlag.LOCKED)
            disableItems(true)
        } else {
            obj.clear(GameObjectFlag.LOCKED)
            disableItems(false)
        };
        }
        visibleToggle.onChange { if (!visibleToggle.isChecked) {
            obj.set(GameObjectFlag.RENDER)
        } else {
            obj.clear(GameObjectFlag.RENDER)
        }
        }
        initOverflowOptions()
    }

    override fun getPrefWidth(): Float {
        val tree = node.tree
        return min(tree.width - x - 5, UI.root.right.container.width)
    }

    override fun getPrefHeight(): Float {
        return 22f
    }

    override fun drawBackground(batch: Batch, parentAlpha: Float, x: Float, y: Float) {
        super.drawBackground(batch, parentAlpha, x, y)
//
        batch.draw(pixel, node.tree.x, y-1, node.tree.width, 1f)

        if (visualIndex % 2 == 0) {
        }
//        batch.color.set(0.7f, 0.7f, 0.7f, 1f)
//        batch.draw(pixel, node.tree.x, y-1, node.tree.width, 1f)
//        batch.color.alpha(1f)


    }


    fun disableItems(disabled: Boolean) {
        node.getTree()?.let {  it.setOverNode(null) }
         node.getTree().selection.clear()
        button.isDisabled = disabled
        lockToggle.isDisabled = disabled
        label.color = if (disabled) Color.GRAY else Color.WHITE
        node.setSelectable(!disabled)
        node.getChildren()?.let { children -> children.map { it.actor as ObjectTable }.forEach { it.disableItems(disabled) }
        }
    }
}