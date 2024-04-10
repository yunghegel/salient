package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.SImageButton

open class EditorFrame : STable() {

    val split = MultiSplitPaneEx(false)
    val centerContent = TabPanel()

    val menubarSlot = STable()
    val footerSlot = STable()

    val left = PanelGroup(LEFT)
    val right = PanelGroup(RIGHT)
    val center = PanelGroup(CENTER)

    val centerSplit = SplitPaneEx(centerContent, center.container, true)

    init {
        create()
        footerSlot.align(Align.left)
    }

    fun create() {
        split.setWidgets(left.container, centerSplit, right.container)
        add(menubarSlot).growX().colspan(5).row()
        add(Separator().apply{color.alpha(0.5f)}).growX().colspan(5).row()
        add(left.toolbar).growY().width(22f).align(Align.top)
        add(Separator()).growY()
        add(split).expand().fill()
        add(Separator()).growY()
        add(right.toolbar).growY().width(22f).row()
        add(Separator()).growX().colspan(5).row()
        add(center.toolbar).growX().colspan(5)


        split.registerComputed(1) { (split.width-300)/split.width }
        split.registerComputed(0) { 220f/split.width }

        split.setSplit(0, 0.2f )
        split.setSplit(1, 0.8f)

        centerSplit.setSplitAmount(0.2f)
        centerSplit.setComputation { (((Gdx.graphics.height-300f))/Gdx.graphics.height) }
    }

    fun setMenubarTable(table: STable) {
        menubarSlot.clearChildren()
        menubarSlot.add(table).growX()
    }

    fun <T:Actor> addFooterItem(actor: T) : Cell<T> {
       return center.toolbar.extras.add(actor)
    }

    fun setContent(content: Actor,title: String) {
        centerContent.addTab(title,content)
    }

    fun addLeft(icon:String, title:String, content: Actor, overflow: ()->Unit={}) {
        left.addPanel(PanelContent(icon, title, content, overflow))
    }

    fun addRight(icon:String, title:String, content: Actor, overflow: ()->Unit={}) {
        right.addPanel(PanelContent(icon, title, content, overflow))
    }

    fun addCenter(icon:String, title:String, content: Actor, overflow: ()->Unit={}) {
        center.addPanel(PanelContent(icon, title, content, overflow))
    }

    fun hide(pos: Int) {
        when(pos) {
            LEFT ->  {
                split.hide(0, LEFT)
                left.hidden = true
                left.container.touchable = Touchable.disabled
            }
            RIGHT -> {
                split.hide(1, RIGHT)
                right.hidden = true
                right.container.touchable = Touchable.disabled
            }
            CENTER -> {
                centerSplit.hide(BOTTOM)
                center.hidden = true
                center.container.touchable = Touchable.disabled
            }
        }

    }

    fun show(pos: Int) {
        when(pos) {
            LEFT -> {
                split.restoreSplit(0)
                left.hidden = false
                left.container.touchable = Touchable.enabled
            }
            RIGHT -> {
                split.restoreSplit(1)
                right.hidden = false
                right.container.touchable = Touchable.enabled
            }
            CENTER -> {
                centerSplit.restoreSplit()
                center.hidden = false
                center.container.touchable = Touchable.enabled
            }
        }
    }

    data class PanelContent(val icon:String, val title:String, val content: Actor,val overflow: ()->Unit={}) {
        var hidden = true
        var button : PanelGroup.ToolbarButton? = null
    }

    inner class PanelGroup(val pos: Int) {
        val container = STable()
        private val header = STable()
        private val content = STable()

        val toolbar = ToolbarGroup(pos)
        var current : PanelContent? = null


        var hidden = false

        private val panels = mutableListOf<PanelContent>()

        init {
            container.add(header).growX()
            container.row()
            container.add(Separator()).growX().row()
            container.add(content).grow()
            content.setBackground("background-pixel")
            header.setBackground("background-pixel")

        }

        private fun setPanel(panel: PanelContent) {
            if(hidden) {
                show(pos)
            } else if(current==panel) {
                if (!hidden) hide(pos)
                else show(pos)
            } else {
                current?.hidden = true
            }

            current = panel
            content.clearChildren()
            content.add(panel.content).grow()
            createContentHeader(panel)
        }


        private fun createContentHeader(panel :PanelContent) {
            header.clear()
            val icon = SImageButton(panel.icon)
            val title = SLabel(panel.title)
            val overflow = SImageButton("overflow-menu")
            header.add(icon).width(22f).padRight(5f)
            header.add(title).growX()
            header.add(overflow).width(22f)
        }

        private fun createToolbarButton(panel: PanelContent) : ToolbarButton {
           panel.button =  ToolbarButton(panel) { from->
                setPanel(from)
                println("${panel.title} clicked")
            }
            return panel.button!!
        }

        fun addPanel(panel: PanelContent) {
            panels.add(panel)
            toolbar.addPanel(createToolbarButton(panel))
            if (current == null) setPanel(panel)
        }

        inner class ToolbarButton(val from: PanelContent,clicked:(PanelContent)->Unit) : STable() {
            val btn = SImageButton(from.icon)
            val title = SLabel(from.title)
            var bg = skin.getDrawable("lighter-gray")
            var bounds = Rectangle()
            init {
                align(Align.left)
                isTransform = true
                add(btn).size(20f).padRight(5f)
                add(title)
                onClick {
                    clicked(from)
                }
                layout()
                computeDimensions()
            }

            override fun draw(batch: Batch?, parentAlpha: Float) {
                if (current!=from) {
                    super.draw(batch, 0.75f)
                } else{
                    super.draw(batch, parentAlpha)
                }

            }
//            override fun drawBackground(batch: Batch?, parentAlpha: Float, x: Float, y: Float) {
//                if (current==from) {
//                    if ( pos == RIGHT) bg.draw(batch, x-6f, y, bounds.width+6f, bounds.height)
//                    else if( pos == LEFT )bg.draw(batch, x-2f, y, bounds.width+8f, bounds.height)
//                    else bg.draw(batch, x, y, width+14f, height)
//                }
//            }


            private fun computeDimensions() {

                when (pos) {
                    LEFT -> {
                        bounds.set(x-6f,y,title.width+btn.width+14f, title.width+btn.width)
                    }
                    RIGHT -> {
                        bounds.set(x-6f,y+6f,title.width+btn.width+14f, title.width+btn.width+6f)
                    }
                    CENTER -> {
                        bounds.set(x,y,width,height)
                    }
                }
            }

            fun orient(pos : Int) {
                when(pos) {
                    LEFT -> {

                        rotateBy(-270f)
                        setOrigin(11f,11f)

                    }
                    RIGHT -> {
                        rotateBy(-90f)
                        setOrigin(11f,11f)

                    }
                }
                computeTransform()
            }

            override fun layout() {
                super.layout()
                computeDimensions()
            }
        }



        inner class ToolbarGroup(val pos:Int) : STable() {

            val buttonGroup = STable()
            val extras = STable()

            init {
                touchable = Touchable.enabled
                extras.align(Align.right)
                add(buttonGroup)
                add(extras).growX()
                setBackground("dark-gray")

                when(pos) {
                    LEFT -> align(Align.top)
                    RIGHT -> align(Align.top)
                    CENTER -> align(Align.left)
                }

                addListener(object:ClickListener(){
                    override fun touchDown(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                        getActorAt(y)
                        return super.touchDown(event, x, y, pointer, button)
                    }
                })
            }

            fun addPanel(panel: ToolbarButton) {
                panel.orient(pos)
                when(pos) {
                    LEFT -> buttonGroup.add(panel).width(24f).padTop(panel.title.width+panel.btn.width).row()
                    RIGHT -> buttonGroup.add(panel).width(24f).padBottom(panel.title.width+panel.btn.width).padTop(6f).row()
                    CENTER -> buttonGroup.add(panel).height(22f).padRight(30f).left().growX()
                }
            }

            fun getActorAt(y:Float) {
                buttonGroup.children.forEach {
                    val bounds = Rectangle()
                    if (it.y < y && it.y+it.height > y) {
                        println("clicked ${it::class.simpleName}")
                    }

                    if (it is ToolbarButton) {
                        it.getBounds(bounds)
                        with(it) {
                            println("$x $y $width $height")
                        }

                    }

                }
            }

        }

    }




}