package org.yunghegel.salient.engine.ui.layout.panel

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ext.BOTTOM
import org.yunghegel.gdx.utils.ext.LEFT
import org.yunghegel.gdx.utils.ext.RIGHT
import org.yunghegel.salient.engine.ui.layout.EditorFrame.PanelContent
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable

class ToolbarButton(val from: PanelContent, val context: PanelContext) : STable() {

    private val actors = object {
        val btn = SImageButton(from.icon)
        val title = SLabel(from.title)
    }

    var bounds = Rectangle()

    var clicked:(PanelContent)->Unit = {}


    init {
        align(Align.left)
        isTransform = true
        add(actors.btn).size(20f).padRight(5f)
        add(actors.title)
        onClick {
            clicked(from)
        }
        layout()
        computeDimensions()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
//        if (context.current=from) {
//            super.draw(batch, 0.75f)
//        } else{
//            super.draw(batch, parentAlpha)
//        }

    }

    private fun computeDimensions() {
        val pos = context.pos
        with(actors) {

                when (pos) {
                    LEFT -> {
                        bounds.set(x - 6f, y, title.width + btn.width + 14f, title.width + btn.width)
                    }

                    RIGHT -> {
                        bounds.set(x - 6f, y + 6f, title.width + btn.width + 14f, title.width + btn.width + 6f)
                    }

                    BOTTOM -> {
                        bounds.set(x, y, width, height)
                    }
                    else -> {
                        bounds.set(x, y, width, height)
                    }
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