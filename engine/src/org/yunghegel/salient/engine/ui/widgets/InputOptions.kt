package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import org.yunghegel.salient.engine.ui.widgets.value.widgets.LabeledFloatField

interface InputOptions {

    val entries: MutableMap<String, InputEntry<*>>

    fun float(name: String, default: Float = 0f, change: (Float) -> Unit = {}): InputEntry<Float>

    fun int(name: String, default: Int = 0, change: (Int) -> Unit = {}): InputEntry<Int>

    fun string(name: String, default: String = "", change: (String) -> Unit = {}): InputEntry<String>

    fun bool(name: String, default: Boolean = false, change: (Boolean) -> Unit = {}): InputEntry<Boolean>

    fun <T> input(name: String, default: T, change: (T) -> Unit = {}): InputEntry<T> {
        val entry = InputEntry(default, change)
        entries[name] = entry
        return entry
    }

}

data class InputEntry<T>(val default: T, var handler: (T) -> Unit) {

    var value: T = default
        set(value) {
            field = value
            handler(value)
        }
}