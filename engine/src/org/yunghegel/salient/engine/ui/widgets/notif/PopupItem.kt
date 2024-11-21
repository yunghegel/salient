package org.yunghegel.salient.engine.ui.widgets.notif

import com.badlogic.gdx.scenes.scene2d.Stage
import org.yunghegel.salient.engine.ui.scene2d.STable

interface PopupItem {

    val id : Int
    val severity: Severity
    val strategy: AlertStrategy
    val date: String

    val header : STable
    val body : STable

    fun onDismissed()

    fun show(stage: Stage, x: Float, y: Float)

    fun dismiss()

    companion object {

        var idCounter = 0
        fun nextId() = idCounter++

    }

}