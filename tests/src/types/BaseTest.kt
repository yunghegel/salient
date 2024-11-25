package types

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.yunghegel.salient.engine.ui.UI

@OptIn(kotlin.ExperimentalStdlibApi::class)
abstract class BaseTest(override var name: String) : ApplicationAdapter(), Test {

    val debug = DebugContext()

    lateinit var fontBatch: SpriteBatch
    lateinit var font: BitmapFont

    fun createDebugCtx() {
        fontBatch = SpriteBatch()
        font = UI.font
        debug.initialized = true
    }

    fun debug(name: String, value: () -> String) {
        if (!debug.initialized) createDebugCtx()
        debug.define(name, value)
    }

    fun drawDebug() {
        if (!debug.initialized) return
        fontBatch.begin()
        debug.keys.forEachIndexed { index, key ->
            font.draw(fontBatch, "${key.name}: ${key.value()}", 10f, 20f + index * 20f)
        }
        fontBatch.end()
    }


}

class DebugContext() {

    var initialized = false

    val keys = mutableListOf<Key>()

    fun define(name: String, value: () -> String) {
        keys.add(Key(name, value))
    }

    data class Key(val name: String, val value: () -> String)

}