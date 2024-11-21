package org.yunghegel.salient.engine.ui.widgets.notif

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ObjectSet
import com.kotcrab.vis.ui.util.ToastManager
import com.kotcrab.vis.ui.widget.Separator
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.toast.Toast
import ktx.actors.onChange
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.child
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.layout.PanelWindow
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.widgets.InputTable
import org.yunghegel.salient.engine.ui.widgets.Result
import java.util.*


class Notifications(val stage: Stage,group: Group) : ToastManager(group) {

    private val pending : Stack<SToast> = Stack()


    var current : SToast? = null
    val set = ObjectSet<SToast>()
    val active: Int
        get() = pending.size

    val gui : EditorFrame by lazy { UI.root }


    val toastGroup : Group
        get() = root




    var button : NotificationButton = NotificationButton("notifications") { active }

    val menu = PanelWindow("Notifications","notifications")

    fun showNotificationsMenu() {
        set.each { toast -> menu.add(
            table {
                child {
                    add(SLabel(toast.titleText)).growX().left()
                    add(SImageButton("close").apply { onChange { set.remove(toast); remove() }}).right().row()
                    add(Separator()).growX().colspan(2)
                }.row()
                child {
                    add(toast.mainTable).growX().padBottom(5f).row()
                }
            }
        ).growX().padBottom(5f).row() }

        toast(false, AlertStrategy.INDICATED, Severity.INFO, "Notifications", time = -1f) {
            add(menu).grow().padBottom(5f).row()
        }.apply { id = "notifications";}


        menu.setKeepWithinStage(true)
        menu.setPosition(Gdx.graphics.width/2f - menu.width/2f, Gdx.graphics.height/2f - menu.height/2f)
        menu.isVisible = true
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

    fun push(notification: SToast, time: Float = 5f) {
        set.add(notification)
        if (current != null) {
            pending.push(notification)
        } else {
            current = notification
            notification.onClose = {
                current = null
                if (pending.isNotEmpty()) {
                    val next = pending.pop()
                    push(next)
                }
            }
            show(notification, time)
        }
    }

    fun post(notification: SToast, next: SToast? = if (pending.isNotEmpty()) pending.pop() else null) {
        pending.push(notification)
    }

    inner class NotificationButton(icon: String,val queryAlerts: ()->Int) : STable() {
        val buttonIcon = SImageButton(icon)
        var numberAlerts : Int = 0
            set(value) {
                field = value
                label.setText(value.toString())
            }
        val label = SLabel(numberAlerts.toString(),"default-small")


        init {
            touchable = Touchable.enabled
            onClick {
                showNotificationsMenu()
            }

            child {
                add(buttonIcon).size(16f).left()
                add(label).padLeft(5f)
            }
        }


        fun update() {
            numberAlerts  = queryAlerts()
        }

        override fun layout() {
            label.setOrigin(5f,5f)
            super.layout()
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
    UI.notifications?.push(toast)
}

fun notify(title: String, message: String, severity: Severity = Severity.INFO, strategy: AlertStrategy = AlertStrategy.INDICATED) {
    val toast = toast(false, title = "${severity.name}: $title", severity = severity) {
        add(SLabel(message).apply {
            wrap = true
        }).minWidth(150f).growX().row()
    }
    toast.configItem = false
    UI.notifications?.push(toast)
}

fun notify(title: String, severity: Severity = Severity.INFO, toast: ()->SToast) {
    val t = toast()
    t.configItem = false
    UI.notifications?.push(t)
}

fun notify(message: String) {
    notify(message, Severity.INFO, AlertStrategy.INDICATED)
}

fun alert(message:String) {
    notify(message, Severity.WARNING, AlertStrategy.IMMEDIATE)
}

fun toast(submitButton:Boolean = true, strategy:AlertStrategy= AlertStrategy.IMMEDIATE, severity: Severity = Severity.INFO,title : String = "Notification",time:Float =5f, build: InputTable.()->Unit) : SToast {
    val content = InputTable(title)
    content.build()
    val toast = SToast(strategy,severity,content,title)
    if (submitButton) {
        content.add(content.submitButton).pad(4f)
    }

    UI.notifications?.push(toast,time)
    return toast
}

class SToast(val strategy:AlertStrategy, val severity: Severity,val content: Table, val title:String?=null) : Toast(content) {
    constructor(content: Table) : this(AlertStrategy.IMMEDIATE, Severity.INFO, content)
    constructor(content:Table, title:String) : this(AlertStrategy.IMMEDIATE, Severity.INFO, content) {
        titleText = title
    }
    var titleText = title ?: severity.name
        set(value) {
            field = value
            titleLabel.setText(value)
        }
    var onClose : ()->Unit = {}
    var useResult : (Result)->Unit = {}
    var configItem = true
    var id = "undefined"

    val notifs : Notifications by lazy { UI.notifications!! }

    val titleLabel = SLabel(titleText).apply {
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

    init {
        build()
    }

    fun markAsRead() {
        notifs.button.update()
    }

    fun build() {
//        mainTable.clearChildren()
        val exit = VisImageButton("toast")
        exit.onChange { fadeOut() }
        contentTable.add(table {
            child {
                add(titleLabel).growX().left()
                add(exit).right().row()
                background = skin.drawable("tab_down", Color.WHITE)
            }.growX().row()
            child {
                add(content).grow().padVertical(5f).row()
                content.setBackground("panel_body_background")
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