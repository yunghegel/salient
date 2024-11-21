package org.yunghegel.salient.editor.cmd

import jdk.jfr.Name
import org.yunghegel.gdx.cli.arg.Arg
import org.yunghegel.gdx.cli.arg.Argument
import org.yunghegel.gdx.cli.arg.Command
import org.yunghegel.gdx.cli.arg.Namespace
import org.yunghegel.salient.engine.system.InjectionContext.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.widgets.notif.Notifications
import org.yunghegel.salient.engine.ui.widgets.notif.notify

@Namespace("ui")
class UICommands {

    val notifs: Notifications by lazy { inject() }
    val ui : UI by lazy { UI }

    @Command("push_toast")
    fun pushToast(@Argument("title") title : String, @Argument("message") message : String) {
        notify(message=message)
    }

    @Command("show_notifications")
    fun showNotifications() {
        notifs.showNotificationsMenu()
    }

}