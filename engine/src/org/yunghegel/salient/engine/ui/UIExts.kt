package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import org.yunghegel.salient.engine.ui.UI.SInput

import org.yunghegel.salient.engine.ui.scene2d.SPopup

fun Stage.popup(show: Boolean = true, build: SPopup.PopupBuilder.() -> Unit): SPopup {
    SInput.pauseExcept(this)
    val builder = SPopup.PopupBuilder()
    builder.build()
    val popup = builder.create()
    if (show) showDialog(popup)
    return popup
}

fun Stage.showDialog(dialog: SPopup) {
    SInput.pauseExcept(this)
    dialog.closed = {
        SInput.resumeExcept(this)
    }
    dialog.show(this)
}