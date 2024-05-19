package org.yunghegel.salient.launcher.exe

import com.badlogic.gdx.LifecycleListener
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener
import org.yunghegel.gdx.utils.ui.IconType
import org.yunghegel.gdx.utils.ui.MessageType
import org.yunghegel.gdx.utils.ui.messageBox
import org.yunghegel.salient.editor.app.configs.Settings
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.FilesDroppedEvent
import org.yunghegel.salient.engine.events.lifecycle.ShutdownEvent
import org.yunghegel.salient.engine.events.lifecycle.StartupEvent
import org.yunghegel.salient.engine.helpers.Serializer
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.file.Paths
import kotlin.system.exitProcess

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

       messageBox("Exit", "Save before exit?", MessageType.YES_NO,IconType.QUESTION,true) {
           if (it == null) return@messageBox
            if (it) {
                Settings.i.configurations.forEach { config->
                config.sync()
                debug("${config::class.simpleName} synced")
                }

                save(Paths.CONFIG_FILEPATH.path) { Serializer.yaml.encodeToString(Settings.serializer(),Settings.i) }
                exitProcess(0)
            } else {
                exitProcess(0)
            }
        }

        return false
    }

    override fun filesDropped(files: Array<out String>) {
        post(FilesDroppedEvent(files))
    }

    override fun refreshRequested() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {

    }

    fun addShutdownHook(hook: ()->Unit) {
        Runtime.getRuntime().addShutdownHook(Thread(hook))
    }
}