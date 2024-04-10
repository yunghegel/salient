package org.yunghegel.salient.engine.ui.widgets.aux

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import org.yunghegel.salient.engine.io.Log
import org.yunghegel.salient.engine.io.LogHandler
import org.yunghegel.salient.engine.io.LogLevel
import org.yunghegel.salient.engine.io.LogReference
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable

class Console : STable(), LogHandler {

    var stack: Stack = Stack()
    val scroll: ScrollPane

    val MAX_ENTRIES = 250
    val entries : Entries

    init {
        add(stack).grow()
        entries = Entries()
        scroll = ScrollPane(entries)
        scroll.setOverscroll(false, true)
        scroll.setSmoothScrolling(true)
        scroll.setScrollbarsVisible(true)
        stack.add(scroll)
        Log.addHandler(this)
        setBackground("selection-dark")
        buildListener()
    }

    override fun handle(logRef: LogReference) {
        entries.addEntry(logRef)
        refresh()
    }

    private fun refresh() {
        scroll.validate()
        scroll.scrollPercentY = 1f
        scroll.scrollPercentX = 0f
        scroll.setOverscroll(true, true)
    }

    fun buildListener() {
        val clickListener = object : ClickListener() {
            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                if (stage.scrollFocus != null)
                {
                    if (toActor !=null && toActor.isDescendantOf(this@Console))
                    {
                        stage.setScrollFocus(scroll)

                    } else
                    {
                        stage.setScrollFocus(null)
                    }
                }

                super.exit(event, x, y, pointer, toActor)
            }

            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                if (stage.scrollFocus == null)
                {
                    stage.setScrollFocus(scroll)
                }
                super.enter(event, x, y, pointer, fromActor)
            }
        }
        addListener(clickListener)
    }

    class Entry(var ref:LogReference) : STable() {

        val typeLabel = SLabel("")
        val sourceLabel = SLabel("")
        val messageLabel = SLabel("")

        init {
            pad(1f)
            setRef()
            setBackground("border-light-y")

            if(ref.level != LogLevel.EVENT)
            {
                add(typeLabel).padBottom(2f).padTop(2f).padLeft(5f)
                add(sourceLabel).padBottom(2f).padTop(2f).padLeft(5f)
                add(messageLabel).padBottom(2f).padTop(2f).padLeft(5f).growX().row()
            } else
            {
                add(messageLabel).padBottom(2f).padTop(2f).padLeft(5f)
                add(sourceLabel).padBottom(2f).padTop(2f).padLeft(5f).growX().row()
            }

        }

        fun setRef() {
            typeLabel.setText(ref.level.name)
            typeLabel.color = ref.level.color
            sourceLabel.setText("[${ref.className}::${ref.methodName}]")
            messageLabel.setText(ref.msg)
            if (ref.level == LogLevel.EVENT) {
                messageLabel.color = ref.level.color
            }
        }

    }

    inner class Entries : STable() {

        val entries = mutableListOf<Entry>()

        fun addEntry(ref: LogReference) {
            val entry = Entry(ref)
            validateEntry(entry)
            entries.add(entry)
            add(entry).growX().row()
        }

        fun validateEntry(entry: Entry) {
            if (entries.size > MAX_ENTRIES) {
                entries.removeAt(0)
                removeActorAt(0, true)
            }
        }

    }



}