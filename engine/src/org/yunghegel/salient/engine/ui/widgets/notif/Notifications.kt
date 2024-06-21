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
import com.badlogic.gdx.utils.ObjectSet
import com.kotcrab.vis.ui.util.ToastManager
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.toast.Toast
import com.kotcrab.vis.ui.widget.toast.ToastTable
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
import java.io.PrintStream
import java.util.*
import javax.swing.text.StyleConstants.setAlignment
import kotlin.collections.HashSet
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import kotlin.math.max


class Notifications(val stage: Stage,group: Group) : ToastManager(group) {

    private val pending : Stack<SToast> = Stack()


    var current : SToast? = null
    val set = ObjectSet<SToast>()
    var active = 0
        get() = set.size

    val gui : EditorFrame by lazy { UI.root }




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

    override fun remove(toast: Toast?): Boolean {
        if (toast is SToast) {
            toast.onClose()
        }
        return super.remove(toast)
    }

    fun push(notification: SToast) {
        if(toasts.size >= 3) {
            val last = toasts.first()
            remove(last)
        }

        show(notification, 5f)
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
    val toast = toast(false, title = severity.name, severity = severity) {
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
    val toast = SToast(strategy,severity,content,title)
    if (submitButton) {

        content.add(content.submitButton).pad(4f)
    }

    UI.notifications.push(toast)
    return toast
}

class SToast(val strategy:AlertStrategy, val severity: Severity,val content: Table, title:String?=null) : Toast(content) {
    constructor(content: Table) : this(AlertStrategy.IMMEDIATE, Severity.INFO, content)
    constructor(content:Table, title:String) : this(AlertStrategy.IMMEDIATE, Severity.INFO, content) {
        titleText = title
    }
    var titleText = title ?: severity.name
        set(value) {
            field = value
            title.setText(value)
        }
    var onClose : ()->Unit = {}
    var useResult : (Result)->Unit = {}
    var configItem = true

    val title = SLabel(titleText).apply {
        color = when(severity) {
            Severity.INFO -> Color.WHITE
            Severity.WARNING -> Color.YELLOW
            Severity.ERROR -> Color.ORANGE
            Severity.CRITICAL -> Color.RED
        }
    }

    val result : Result?
        get() = if (content is InputTable) content.result else null

    var closed = false

    var closedCb : ()->Unit = {}

    init {
        build()
    }

    fun build() {
        mainTable.clearChildren()
        val exit = VisImageButton("toast")
        exit.onChange { fadeOut() }
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
            content.submitButton.onChange { fadeOut() }
        }
    }

    fun closeToast() {
        close()
    }



    override fun close() {
        result?.let { useResult(it) }
        super.close()
    }



}