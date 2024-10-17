package org.yunghegel.salient.editor.plugins.base.systems

import com.badlogic.gdx.Gdx
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.engine.system.inject

class HotkeySystem : BaseSystem("HotkeySystem") {

    val hotkeys = mutableMapOf<Int,()->Unit>()

    var paused = false

    override fun update(deltaTime: Float) {
        if (paused) return
        hotkeys.forEach { (key,action) ->
            if (Gdx.input.isKeyPressed(key)) {
                action()
            }
        }
        super.update(deltaTime)
    }
}

fun registerHotkey(key:Int,action:()->Unit) {
    val system: HotkeySystem = inject()
    system.hotkeys[key] = action
}

fun pauseHotkeys() {
    val system: HotkeySystem = inject()
    system.paused = true
}

fun resumeHotkeys() {
    val system: HotkeySystem = inject()
    system.paused = false
}