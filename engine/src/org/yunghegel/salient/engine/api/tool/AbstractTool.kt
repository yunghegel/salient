package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import org.yunghegel.gdx.utils.data.EnumBitmask
import org.yunghegel.salient.engine.graphics.RenderUsage
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.ui.UI

abstract class AbstractTool(override val name: String, override val activationKey: Int = -1) : InputMultiplexer(),
    Tool {

    override val renderMask: EnumBitmask<RenderUsage> = EnumBitmask(RenderUsage::class.java)

    override var active: Boolean = false

    override val icon: String? = null

    override fun update(delta: Float) {

    }

    override fun render(renderer: ShapeRenderer) {

    }

    override fun render(batch: Batch) {

    }

    override fun render(batch: ModelBatch, environment: Environment) {

    }

    override fun activate() {
        debug("Activating tool $name")
        addProcessor(UI)
//        engine.addEntity(entity)
        active = true
        if (actor is Button) (actor as Button).isChecked = false
        if (actor is Label) (actor as Label).color = UI.skin.get("white", Color::class.java)
    }

    override fun deactivate() {
        debug("Deactivating tool $name")
        removeProcessor(UI)
//        engine.removeEntity(entity)
        active = false
        if (actor is Button) (actor as Button).isChecked = false
        if (actor is Label) (actor as Label).color = UI.skin.get("soft_white", Color::class.java)


    }

}