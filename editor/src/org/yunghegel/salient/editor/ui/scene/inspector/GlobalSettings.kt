package org.yunghegel.salient.editor.ui.scene.inspector

import org.yunghegel.gdx.utils.ui.ReflectionBasedEditor
import org.yunghegel.salient.engine.helpers.Grid
import org.yunghegel.salient.engine.io.inject

class GlobalSettings : BaseInspector("Global","config") {

    override fun createLayout() {
        val grid : Grid = inject()

        val config = grid.config

        add(ReflectionBasedEditor(config)).growX()

    }
}