package org.yunghegel.salient.launcher

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.LifecycleListener
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener
import com.kotcrab.vis.ui.util.dialog.Dialogs
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.lifecycle.ShutdownEvent
import org.yunghegel.salient.engine.events.lifecycle.StartupEvent
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.ui.UI

class NativeService : Lwjgl3WindowListener, LifecycleListener {

    override fun created(window: Lwjgl3Window?) {
        Bus.post(StartupEvent())
    }

    override fun iconified(isIconified: Boolean) {

    }

    override fun maximized(isMaximized: Boolean) {

    }

    override fun focusLost() {

    }

    override fun focusGained() {

    }

    override fun closeRequested(): Boolean {
        Bus.post(ShutdownEvent())
        val result = Dialogs.showConfirmDialog<Boolean>(
            UI,"Exit", "Save before exit?", arrayOf("Yes", "No","Cancel"), arrayOf(true,true,false)
        ) {
            if (it) {
                Gdx.app.exit()
            }
            false
        }.apply {
            isModal = true
            isMovable = false
        }
        return false
    }

    override fun filesDropped(files: Array<out String>?) {

    }

    override fun refreshRequested() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {

    }
}