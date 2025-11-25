package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.actors.onChange
import org.yunghegel.gdx.utils.data.EnumBitmask
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.graphics.RenderUsage
import org.yunghegel.salient.engine.ui.ToolButton
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel

interface Tool : Named {

    val activationKey: Int
    val icon: String?
    var active: Boolean
    val renderMask: EnumBitmask<RenderUsage>

    val actor: Actor
        get() {
            return if (icon == null) SLabel(name)
            else ToolButton(icon!!, this).apply {
                onChange {
                    if (active) {
                        active = false
                        deactivate()
                    } else {
                        active = true
                        activate()
                    }
                }
            }
        }

    fun toggle() {
        if (active) {
            active = false
            deactivate()
        } else {
            active = true
            activate()
        }
    }

    fun activate()

    fun deactivate()

    fun update(delta: Float)

    fun render(shapeRenderer: ShapeRenderer)

    fun render(batch: Batch)

    fun render(batch: ModelBatch, environment: Environment)


}