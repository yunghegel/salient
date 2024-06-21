package org.yunghegel.salient.engine.ui.widgets.list

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener
import com.badlogic.gdx.scenes.scene2d.utils.Selection
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import com.ray3k.stripe.DraggableList
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.*

abstract class InteractiveList<T,Target>(val fetchTarget: (()->Target)? =null) : STable() {

    var items = GdxArray<T>()
        set(value) {
            field = value

            set(value)
        }

    open val rowHeight = 20f

    abstract val controller: Controller<T,Target>

    private val selection = Selection<T>()

    val changeactor = object : Actor() {
        init {
            addListener(object: ChangeListener(){
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    val selection = selection.first()
                    if (selection != null) println("selected ${itemToString(selection)}") else println("nothing selected")
                    if (selectBox.selected != selection) selectBox.selected = selection
                    controller.selected(selection,fetchTarget.let{it?.invoke()})
                }
            })
        }

    }

    val map = mutableMapOf<T, Row>()


    val selectBox = SSelectBox<T> { item -> itemToString(item) }.apply { onChange {
        selection.set(this.selected)
    } }
    val listItems = DraggableList(true,skin).apply { align(Align.top) }
    val buttons = STable().apply { align(Align.top) }
    val bottomRow = STable()

    val plus = STextButton("+","large")
    val minus = STextButton("-","large")
    val copy = SImageButton("Copy",null)
    val del = SImageButton("Cleanup",null)

    val style = Style()

    init {
        align(Align.top)
        val pane = ScrollPane(listItems)
        pane.setScrollingDisabled(true,false)
        val container = STable().apply {
            align(Align.top)
            add(pane).growX().minHeight(150f)
            add(Separator()).growY()
            add(buttons).growY().width(20f)
        }
        add(container).growX().row()
        add(bottomRow).growX().row()

        with(style) {
            if (top != null) container.background = top
            if (bottom != null) bottomRow.background = bottom
        }

        selection.setActor(changeactor)

        build()

        addListener(object : FocusListener(){
            override fun scrollFocusChanged(event: FocusEvent?, actor: Actor?, focused: Boolean) {
                if (!focused) {
                    overActor = null
                    selectedActor = null
                }
                super.scrollFocusChanged(event, actor, focused)
            }

            override fun keyboardFocusChanged(event: FocusEvent?, actor: Actor?, focused: Boolean) {

                super.keyboardFocusChanged(event, actor, focused)
            }
        })

    }

    private fun build() {
        with(buttons) {
            pad(4f,0f,4f,0f)
            add(plus).size(16f).padBottom(4f).row()
            add(minus).size(16f).padBottom(4f).row()
        }
        with(bottomRow) {
            pad(4f,0f,4f,0f)
            add(selectBox).growX().height(18f).padHorizontal(4f)
            add(copy).size(16f).padHorizontal(4f)
            add(del).size(16f).padHorizontal(4f)
        }

        plus.onChange { controller.create().let { addItem(it) }  }
        minus.onChange { selection.first()?.let { remove(it) } }
        copy.onChange { selection.first()?.let { copy(it) } }
        del.onChange { selection.first()?.let { remove(it) } }
    }

    fun <C:Iterable<T>> set(items: C) {
        listItems.clear()
        map.clear()
        items.forEach { addItem(it) }
    }

    fun remove(item: T) {
        controller.minus(item)
        listItems.removeActor(map[item])
        map.remove(item)
    }

    fun copy(item: T)  {
        val newItem = controller.copy(item)
        addItem(newItem)
    }

    fun addItem(item: T) {
        controller.plus(item)
        val row = if (map.containsKey(item)) map[item] else createRow(item).apply {
            map[item] = this
        }
        row?.let { it.clearChildren(); it.buildActor() }
        listItems.add(row,SLabel(itemToString(row!!.item)),null,null)
    }

    var overActor : Row? = null
        set(value) {
            field?.let {
                it.background = null
            }
            field = value
            value?.let {
                it.background = style.over
            }
        }

    var selectedActor : Row? = null
        set(value) {
//            clear old selection
            field?.let {
                it.background = null
            }
            field = value
            value?.let {
                it.background = style.selected
            }
        }


    abstract fun createRow(item: T): Row

    abstract fun itemToString(item: T): String

    abstract inner class Row(val item: T) : STable() {

        var over = false
            set(value) {
                field = value
                background = if (value) overbg else bg
            }

        var selected = false
            set(value) {
                field = value
                background = if (value) selectedbg else bg
            }

        open val label : SLabel = SLabel(itemToString(item))

        fun trancateLabelWidth() {
            val childrenMinusLabel = children.filter { it != label }
            val reservedSpace = childrenMinusLabel.fold(0f) { acc, actor -> acc + actor.width }
            label.width = width - reservedSpace




        }

        override fun layout() {
            trancateLabelWidth()
            super.layout()
        }

        open val overbg = UI.drawable("border-light")
        open val selectedbg = UI.drawable("button-compact-blue")
        open val bg = UI.drawable("button_underline_dark")

        init {
            touchable = Touchable.enabled
            align(Align.left)
            label.setEllipsis(true)

            label. widthFunction = {
                val childrenMinusLabel = children.filter { it != label }
                val width = childrenMinusLabel.fold(0f) { acc, actor -> acc + actor.width }
                width
            }

            setBackground(bg)
            

            addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    over = true
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    over = false
                    super.exit(event, x, y, pointer, toActor)
                }

                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (tapCount > 1) { selection.clear(); selected = false }
                    else{  selection.set(item); }
                    println(label.width)
                    super.clicked(event, x, y)
                }
            })
        }

        override fun drawBackground(batch: Batch?, parentAlpha: Float, x: Float, y: Float) {
            if (selected) selectedbg.draw(batch, x, y, width, height)
            else if (over) overbg.draw(batch, x, y, width, height)
            else bg.draw(batch, x, y, width, height)
        }

        override fun getPrefHeight(): Float {
            return this@InteractiveList.rowHeight
        }

        fun createListener(actor: Actor ) {
            actor.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (tapCount > 1) selection.clear()
                    else selection.set(item)
                    super.clicked(event, x, y)
                }
            })
        }

        abstract fun buildActor()

    }

    interface Controller<T,Target> {

        fun create() : T

        fun plus(item: T)

        fun minus(item: T)

        fun copy(item: T) : T

        fun selected(item: T?,applyTo: Target?)

    }

    class Style(topName : String = "tab_down", bottomName : String = "tab_panel", ifOver:String ="selection-dark", ifSelect: String = "button-compact-blue", topTint: Color? = null, bottomTint: Color? = Color(0.4f,0.4f,0.4f,1f)) {
        var top : Drawable? = if (topTint != null) UI.drawable(topName, topTint) else UI.drawable(topName)
        var bottom : Drawable? = if (bottomTint != null) UI.drawable(bottomName, bottomTint) else UI.drawable(bottomName)
        var over: Drawable? = UI.drawable(ifOver)
        var selected : Drawable? =  UI.drawable(ifSelect)
    }

}

class TestList(fetchTarget: (() -> GameObject)? = null) : InteractiveList<Material,GameObject>(fetchTarget) {

    override val controller = object : Controller<Material,GameObject> {
        override fun create(): Material {
            return Material()
        }

        override fun minus(item: Material) {
            println("removing $item")
        }

        override fun copy(item: Material): Material {
            println("copying $item")
            return item
        }

        override fun selected(item: Material?,applyTo:GameObject?) {
            println("selected $item")
        }

        override fun plus(item: Material) {
            println("added $item")
        }
    }

    override fun createRow(item: Material): Row {
        return object : Row(item) {
            val icon = UI.drawable("materials_icon")

            override fun buildActor() {
                add(SImage(icon)).size(16f)
                add(label).growX()
                pad(3f)

                onClick {
                    println("clicked ${item.id}")
                }
            }
        }
    }

    override fun itemToString(item: Material): String {
        return item.id ?: "Material${items.indexOf(item)}"
    }
}
