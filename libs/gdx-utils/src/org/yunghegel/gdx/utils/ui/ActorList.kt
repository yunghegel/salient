package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import org.yunghegel.gdx.utils.ext.alpha
import org.yunghegel.gdx.utils.ext.padHorizontal

interface LabelSupplier {
    var label : String?
}

open class ActorList(skin: Skin, val style : ActorListStyle = ActorListStyle()) : Table(skin) {

    val items = mutableListOf<Actor>()

    var labelSupplier: LabelSupplier? = null

    val keys = mutableListOf<String>()



    init {
        items.forEach {
            addItem(it).height(style.lineHeight).row()
        }
    }

    fun addItem(actor: Actor,config: (Cell<out Actor>.() -> Unit)? = null) : Cell<out Actor> {
        if(items.size >= style.maxItems) {
            items.removeAt(0)
            clearChildren()
            items.forEach { actorItem ->
                buildTable(actor,config)
            }
        }
        items.add(actor)
        val cell = buildTable(actor,config)
        return cell
    }



    private fun buildLabel(string: String) : Label {
        val label = object : Label(string,skin) {
            override fun getPrefWidth(): Float {
                return Math.min(this@ActorList.width/2,super.getPrefWidth())
            }
        }
        label.setAlignment(Align.left)
        label.setEllipsis(true)
        return label
    }

    private fun buildTable(actor:Actor,config: (Cell<out Actor>.() -> Unit)? = null) : Cell<out Actor> {
        val table = object : Table() {
            override fun getWidth(): Float {
                return this@ActorList.width
            }
        }
        val index = items.indexOf(actor)
        if(index % 2 == 0 ) {
            table.background = style.even
        } else {
            table.background = style.odd
        }

        if (table.background == null) {
            if (index!=0) {
                table.add(Separator().apply {color.alpha(0.5f)}).growX().height(1f).colspan(2)
                table.row()
            }



        }

        table.align(Align.right)
        var label : Label? = null
        var labelString = ""
        if (actor is LabelSupplier) {
            labelString = actor.label!!
            label = buildLabel(labelString)
        } else if(actor.userObject is LabelSupplier) {
            labelString = (actor.userObject as LabelSupplier).label!!
            label = buildLabel(labelString)
        }
        if(label != null) {
            table.add(label).growX().padHorizontal(5f).left().height(style.lineHeight)
        }

        table.add(actor).right().height(style.lineHeight-4f).width(70f).padHorizontal(5f)

        val cell = add(table).height(style.lineHeight).growX()
        row()
        if (config != null) {
            cell.apply(config)
        }


        return cell
    }

    fun assertUnique(table: Table, proposed:String) : Boolean {
        if (table.userObject is String) {
            val label = table.userObject as String
            if (label == proposed) {

               return false
            }
        }
        return true
    }


    fun insertTable(table:Table) {
        table.row()
        table.add(Separator()).growX().height(1f).row()
        add(table).height(style.lineHeight).fillX().row()
    }

    fun rebuild () {
        clearChildren()
        items.forEach {
            addItem(it).height(style.lineHeight).row()
        }
    }

    fun sortBy(comparator: Comparator<Actor>) {
        items.sortWith(comparator)
        clearChildren()
        items.forEach {
            addItem(it).height(style.lineHeight).row()
        }
    }



    private fun drawBackground(batch: Batch, parentAlpha: Float) {
        style.background?.draw(batch, x, y, width, height)
    }

    private fun drawActorBackground(batch: Batch,parentAlpha: Float,actor:Actor) {
        if (indexOf(actor) % 2 == 0) {
            style.even?.draw(batch, actor.x, actor.y, actor.width, actor.height)
        } else {
            style.odd?.draw(batch, actor.x, actor.y, actor.width, actor.height)
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.let { b ->
            drawBackground(b, parentAlpha)
            forEach { drawActorBackground(b, parentAlpha, it) }
        }
        super.draw(batch, parentAlpha)
    }

    fun setItems(actors: Array<Actor>, config: (Cell<out Actor>.() -> Unit)? = null) {
        items.clear()
        clearChildren()
        keys.clear()
        val filteredUnique = actors.filter { actor ->
            if(actor.userObject is LabelSupplier) {
                val label = (actor.userObject as LabelSupplier).label!!
                if(keys.contains(label)) {
                    return@filter false
                }
                keys.add(label)
            }
            return@filter true
        }
        filteredUnique.forEach {
            addItem(it,config).height(style.lineHeight).row()
        }

    }

    private fun getTable(): Table {
        val table = Table()

        return table
    }

    fun remove(actor: Actor) {
        items.remove(actor)
        clearChildren()
        items.forEach {
            addItem(it).height(style.lineHeight).row()
        }
    }

    override fun clear() {
        items.clear()
        clearChildren()
    }

    fun contains(actor: Actor): Boolean {
        return items.contains(actor)
    }

    fun size(): Int {
        return items.size
    }

    fun get(index: Int): Actor {
        return items[index]
    }

    fun forEach(action: (Actor) -> Unit) {
        items.forEach(action)
    }

    fun forEachIndexed(action: (Int, Actor) -> Unit) {
        items.forEachIndexed(action)
    }

    fun indexOf(actor: Actor): Int {
        return items.indexOf(actor)
    }

    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return items.isNotEmpty()
    }

    fun last(): Actor {
        return items.last()
    }

    fun first(): Actor {
        return items.first()
    }

    fun removeAt(index: Int): Actor {
        val actor = items.removeAt(index)
        clearChildren()
        items.forEach {
            addItem(it).height(style.lineHeight).row()
        }
        return actor
    }

    fun removeLast(): Actor {
        val actor = items.removeLast()
        clearChildren()
        items.forEach {
            addItem(it).height(style.lineHeight).row()
        }
        return actor
    }

    fun removeFirst(): Actor {
        val actor = items.removeFirst()
        clearChildren()
        items.forEach {
            addItem(it).height(style.lineHeight).row()
        }
        return actor
    }

    fun removeFirstOrNull(): Actor? {
        if(items.isEmpty()) return null
        val actor = items.removeFirst()
        clearChildren()
        items.forEach {
            addItem(it).height(style.lineHeight).row()
        }
        return actor
    }

    class ActorListStyle {
        var lineHeight = 20f
        var maxItems = 15
        var autoSort = true
        var selected : Drawable? = null
        var odd : Drawable? = null
        var even : Drawable? = null
        var background : Drawable? = null
    }



}