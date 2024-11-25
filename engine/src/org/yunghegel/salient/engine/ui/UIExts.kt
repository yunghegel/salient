package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.ui.scene2d.SPopup

fun Stage.popup(show: Boolean = true, build: SPopup.PopupBuilder.() -> Unit): SPopup {
    Input.pauseExcept(this)
    val builder = SPopup.PopupBuilder()
    builder.build()
    val popup = builder.create()
    if (show) showDialog(popup)
    return popup
}

fun Stage.showDialog(dialog: SPopup) {
    Input.pauseExcept(this)
    dialog.closed = {
        Input.resumeExcept(this)
    }
    dialog.show(this)
}