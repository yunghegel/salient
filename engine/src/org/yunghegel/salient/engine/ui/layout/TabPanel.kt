package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import com.kotcrab.vis.ui.widget.Separator.SeparatorStyle
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ext.alpha
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.widgets.notif.alert

typealias NewTabFunction = () -> Pair<String,Actor>

class TabPanel(val factory: NewTabFunction? = null) : STable() {

    val tabs : MutableMap<String, Tab> = mutableMapOf()

    private val actorsTable = table {
        align(Align.right)
        pad(0f,4f,0f,4f)
    }

    private val tabTable = STable()

    val tabsContainer = STable()
    val addButton = SImageButton("Add","button-rounded-edge-over").apply {
        style.over = UI.skin.getDrawable("scroll")
    }

    private val highlight = UI.skin.getDrawable("white-pixel")
    private val content = STable()


    fun addTitleActor(actor: Actor) {
        actorsTable.add(actor).padHorizontal(3f).right().height(16f).padBottom(2f)
    }

    fun insertTitleActor(actor: Actor, last:Boolean) {
        if (last) {
            actorsTable.add(actor).padHorizontal(3f).right().height(16f).padBottom(2f)
        } else {
            val tmp = actorsTable.children.toMutableList()
            actorsTable.clearChildren()
            addTitleActor(actor)
            tmp.forEach {
                addTitleActor(it)
            }

        }
    }

    fun removeTitleActor(actor: Actor) {
        actorsTable.removeActor(actor)
    }

    var active : Tab? = null
        set(value) {
            field = value

            content.clearChildren()
            content.add(value?.content).pad(0f,3f,3f,3f).grow()
            tabs.filter { it.value != value }.forEach {
                it.value.label.color = Color.GRAY
                it.value.setBackground(it.value.up)
            }
            value?.label?.color = Color.WHITE
        }

    init {
        align(Align.topLeft)
        add(tabTable).height(20f).padTop(3f).growX().row()
        add(Separator()).growX().height(1f).row()
        tabsContainer.align(Align.left)
        tabTable.align(Align.left)

        val wrapper = table {
            add(tabsContainer)
            add(addButton).padHorizontal(5f).size(16f).left()
        }
        tabTable.add(wrapper)
        tabTable.add(actorsTable).growX().row()

        add(content).grow().row()

        addButton.onClick {
           active = if (factory != null) {
                addTab((factory.invoke()).first,(factory.invoke()).second)
            } else {
                addTab("New Scene", STable())
            }

        }
    }

    fun addTab(title: String, actor: Actor) : Tab{
        if (tabs.containsKey(title)) {
            alert("Tab with title $title already exists")
            return tabs[title]!!
        }

        val tab = Tab(title,actor)
        tabs[title] = tab
        tabsContainer.add(tab).padHorizontal(3f)
        if (active == null) {
            active = tab
        }
        return tab
    }

    inner class Tab(val title:String,val content:Actor) : STable() {

        val label = SLabel(title)
        val close = SImageButton("remove")

        var down = UI.skin.getDrawable("tab_up")
        var up = UI.skin.getDrawable("tab_down")

        init {
            add(label).growX()
            add(close).size(16f).padHorizontal(5f)

            setBackground(up)
            onClick {
                if(active != this) {
                    active = this
                }
            }

        }

        override fun drawBackground(batch: Batch?, parentAlpha: Float, x: Float, y: Float) {


            super.drawBackground(batch, parentAlpha, x, y)
            if (active == this) {
//                draw underline
                label.color = Color.WHITE
                batch?.color = Color.SKY.alpha(0.25f)
                highlight.draw(batch, x+2 ,y+2, width-4, 1f)
            }
        }




    }



}