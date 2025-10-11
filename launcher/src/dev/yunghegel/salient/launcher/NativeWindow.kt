package dev.yunghegel.salient.launcher

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener
import dev.jamiecrown.gdx.state.eventbus.EventBus

class NativeWindow : Lwjgl3WindowListener {

    override fun created(window: Lwjgl3Window?) {

    }

    override fun iconified(isIconified: Boolean) {
        TODO("Not yet implemented")
    }

    override fun maximized(isMaximized: Boolean) {
        TODO("Not yet implemented")
    }

    override fun focusLost() {
        TODO("Not yet implemented")
    }

    override fun focusGained() {
        TODO("Not yet implemented")
    }

    override fun closeRequested(): Boolean {
        TODO("Not yet implemented")
    }

    override fun filesDropped(files: Array<out String?>?) {
        TODO("Not yet implemented")
    }

    override fun refreshRequested() {
        TODO("Not yet implemented")
    }


}