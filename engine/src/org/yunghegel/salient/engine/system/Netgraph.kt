package org.yunghegel.salient.engine.system

import com.badlogic.gdx.scenes.scene2d.ui.Table

object Netgraph {

    private val values = mutableMapOf<String, ()->String>()

    val listeners = mutableListOf<(String, ()->String) -> Unit>()

    fun listen(block: (String, ()->String) -> Unit) {
        listeners.add(block)
    }

    fun add(key: String, value: ()->String) {
        values[key] = value
        listeners.forEach { it(key, value) }
    }

    fun each(block: (String, ()->String) -> Unit) {
        for ((key, value) in values) {
            block(key, value)
        }
    }

    fun Table.create() {
        clearChildren()

    }

}