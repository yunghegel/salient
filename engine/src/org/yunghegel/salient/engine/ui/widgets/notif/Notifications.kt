package org.yunghegel.salient.engine.ui.widgets.notif

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.util.ToastManager
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import java.util.*


class Notifications(val stage: Stage) : ToastManager(stage) {

    private val pending : Stack<Notification> = Stack()

    val gui : EditorFrame = inject()

    init  {
        setAlignment(BOTTOM_LEFT)


    }

    var indicatorActive = false
        get() = (pending.any { notif -> notif.strategy == AlertStrategy.INDICATED })

    var offset : Vector2 = Vector2()


    fun push(notification: Notification) {
        when(notification.strategy) {
            AlertStrategy.IMMEDIATE -> {
                notification.x = gui.x
                notification.y = gui.y
                show(notification, 5f)
            }
            AlertStrategy.INDICATED -> pending.push(notification)
            AlertStrategy.SILENT -> pending.push(notification)
        }
    }

    fun calcPadding(notification: Notification) {
        val align = BOTTOM_RIGHT
        val innerBounds : Rectangle = Rectangle()
        gui.centerContent.getBounds(innerBounds)
        println(innerBounds)
        val bottomRight = innerBounds.bottomLeft()
        val padding = 10f


        val insetFromWindow = Vector2()
        val windowW = Gdx.graphics.width.toFloat()
        val windowH = Gdx.graphics.height.toFloat()



        insetFromWindow.x = windowW - bottomRight.x + padding
        insetFromWindow.y = bottomRight.y + padding




        setScreenPadding(bottomRight.x.toInt(), bottomRight.y.toInt())
    }

}

fun notify(message: String, severity: Severity = Severity.INFO, strategy: AlertStrategy = AlertStrategy.INDICATED) {
    val notifs : Notifications = inject()
    notifs.push(Notification(severity, strategy, message))
}

fun notify(message: String) {
    notify(message, Severity.INFO, AlertStrategy.INDICATED)
}

fun alert(message:String) {
    notify(message, Severity.WARNING, AlertStrategy.IMMEDIATE)
}