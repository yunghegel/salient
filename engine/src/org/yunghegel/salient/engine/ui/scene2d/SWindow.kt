package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisWindow
import org.yunghegel.salient.engine.ui.UI

open class SWindow(title: String,stylename: String = "custom",closeButton: Boolean = true) :VisWindow(title,stylename) {

    constructor(title: String,closeButton:Boolean) : this(title,"default",closeButton) {

    }

    init {
        if(closeButton) {
            addCloseButton("close")
        }
    }

    var onClose : ()->Unit = {}

    fun addCloseButton(style: String) {
        val titleLabel = titleLabel
        val titleTable = titleTable
        val closeButton = SImageButton(style)
        titleTable.add<Actor>(closeButton).padRight(-padRight + 1f).size(25f, 26f).padRight(5f)
        closeButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                remove()
                onClose()
            }
        })
        closeButton.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                event.cancel()
                return true
            }
        })
        if (titleLabel.labelAlign == Align.center && titleTable.children.size == 2) titleTable.getCell(titleLabel)
            .padLeft(closeButton.width * 2)
    }

    fun showWindow(stage: Stage, x: Float, y: Float) {
        stage.addActor(this)
        setPosition(x, y)
    }

}
