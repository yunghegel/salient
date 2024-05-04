package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.graphics.g2d.Batch
import com.kotcrab.vis.ui.widget.Separator

class PredicateSeparator(val predicate: ()->Boolean) : Separator() {

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if(predicate()) {
            super.draw(batch, parentAlpha)
        }
    }

}