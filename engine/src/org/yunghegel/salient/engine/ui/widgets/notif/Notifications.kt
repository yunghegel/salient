package org.yunghegel.salient.engine.ui.widgets.notif

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.ToastManager
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.toast.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import ktx.actors.onChange
import ktx.async.KtxAsync
import ktx.async.interval
import ktx.async.schedule
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.child
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.layout.PanelWindow
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.widgets.InputTable
import org.yunghegel.salient.engine.ui.widgets.Result
import org.yunghegel.salient.engine.ui.widgets.viewport.button
import java.util.*
import javax.swing.text.StyleConstants.setAlignment
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import kotlin.math.max


class Notifications(val stage: Stage,group: Group) : ToastManager(group) {

    private val pending : Stack<SToast> = Stack()
    val shown : Stack<SToast> = Stack()
    var current : SToast? = null

    val gui : EditorFrame by lazy { UI.root }

    override fun show(toast: Toast?,timeSec:Float) {
        if (current != null) (current as SToast?)!!.closeToast()
        current = toast as SToast
        current?.onClose = {
            current = null
        }

        super.show(toast, timeSec)
    }



    var button : SImageButton = SImageButton("notifications")

    val menu = PanelWindow("Notifications","notifications")

    fun showNotificationsMenu() {
        while (!pending.isEmpty()) {
            val notif = pending.pop()
            menu.add(notif.mainTable).growX().row()
        }

        UI.addActor(menu)
    }

    init  {
        setAlignment(BOTTOM_LEFT)
    }



    fun push(notification: SToast) {
        pending.push(notification)
        if (notification.configItem) { current?.closeToast(); show(notification) }
        else show(notification, 5f)

    }

    fun post(notification: SToast, next: SToast? = if (pending.isNotEmpty()) pending.pop() else null) {
        pending.push(notification)
    }

    class NotificationButton(icon: String,val queryAlerts: ()->Int) : WidgetGroup() {
        val buttonIcon = SImageButton(icon)
        var numberAlerts : Int = 0
        val label = SLabel(numberAlerts.toString(),"default-small")
        val stack = com.badlogic.gdx.scenes.scene2d.ui.Stack()

        init {
            addActor(stack)
            stack.add(buttonIcon)
            stack.add(label)
        }


        fun update() {
            numberAlerts  = queryAlerts()
        }

        override fun layout() {
            label.setOrigin(5f,5f)
        }

        override fun getPrefWidth(): Float {
            return 18f
        }

        override fun getPrefHeight(): Float {
            return 18f
        }

    }

}

fun notify(message: String, severity: Severity = Severity.INFO, strategy: AlertStrategy = AlertStrategy.INDICATED) {
    val toast = toast(false, title = severity.name) {
        add(SLabel(message).apply {
            wrap = true
        }).minWidth(150f).growX().row()
    }
    toast.configItem = false
    UI.notifications.push(toast)
}

fun notify(message: String) {
    notify(message, Severity.INFO, AlertStrategy.INDICATED)
}

fun alert(message:String) {
    notify(message, Severity.WARNING, AlertStrategy.IMMEDIATE)
}

fun toast(submitButton:Boolean = true, strategy:AlertStrategy= AlertStrategy.IMMEDIATE, severity: Severity = Severity.INFO,title : String = "Notification", build: InputTable.()->Unit) : SToast {
    val content = InputTable(title)
    content.config(build)
    val toast = SToast(content,title)
    if (submitButton) {

        content.add(content.submitButton).pad(4f)
    }

    UI.notifications.push(toast)
    return toast
}

class SToast(val strategy:AlertStrategy, val severity: Severity,val content: Table) : Toast(content) {
    constructor(content: Table) : this(AlertStrategy.IMMEDIATE, Severity.INFO, content)
    constructor(content:Table, title:String) : this(AlertStrategy.IMMEDIATE, Severity.INFO, content) {
        titleText = title
    }
    var titleText = severity.name
        set(value) {
            field = value
            title.setText(value)
        }
    var onClose : ()->Unit = {}
    var useResult : (Result)->Unit = {}
    var configItem = true

    val title = SLabel(titleText).apply {
        when(severity) {
            Severity.INFO -> color = Color.WHITE
            Severity.WARNING -> color = Color.YELLOW
            Severity.ERROR -> color = Color.ORANGE
            Severity.CRITICAL -> color = Color.RED
        }
    }

    val result : Result?
        get() = if (content is InputTable) content.result else null

    init {
        build()
    }

    fun build() {
        mainTable.clearChildren()
        val exit = VisImageButton("toast")
        exit.onChange { close() }
        mainTable.add(table {
            child {
                add(title).growX().left()
                add(exit).right().row()
                background = skin.drawable("tab_down", Color.WHITE)
            }.growX().row()
            child {
                add(content).grow().padVertical(5f).row()
                setBackground("panel_body_background")
            }.grow().row()
        }).grow()
        if (content is InputTable) {
            useResult = content.submit
            content.submitButton.onChange { closeToast() }
        }
    }

    fun closeToast() {
        close()
    }

    override fun close() {
        onClose()
        result?.let { useResult(it) }
        super.close()
    }
}