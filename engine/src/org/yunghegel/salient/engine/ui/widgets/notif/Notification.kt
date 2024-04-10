package org.yunghegel.salient.engine.ui.widgets.notif

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.toast.Toast
import com.kotcrab.vis.ui.widget.toast.ToastTable
import ktx.actors.onClick
import ktx.scene2d.*
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import java.text.SimpleDateFormat
import java.util.*

data class Notification(val severity: Severity = Severity.INFO, val strategy: AlertStrategy = AlertStrategy.INDICATED, val message: String) : ToastTable() {

    val date: String

        init {
            touchable = Touchable.enabled
            date = SimpleDateFormat("HH:mm:ss").format(Date())
        }

    val content = STable()

    init {
        setToast(Toast(content))
        onClick { toast.fadeOut()}

        toast.mainTable.onClick { toast.fadeOut()}
        toast.mainTable.children[1].remove()

        pad(2f,2f,10f,2f)

        val severityLabel = SLabel(severity.name)
        val dateLabel = SLabel(date)

        val inlineHeader = STable()
        inlineHeader.add(severityLabel).growX().align(Align.left)
        inlineHeader.add(dateLabel).growX().align(Align.right)

        content.add(inlineHeader).growX().row()

        val messageLabel = SLabel(message)
        val messageWrapper = STable()
        messageWrapper.add(messageLabel).growX().pad(10f).row()
        messageWrapper.setBackground("button-rounded-edge-over")

        content.add(messageWrapper).growX().padTop(10f).row()



        add(toast.mainTable).grow().colspan(2).row()

        layout()
    }
}
