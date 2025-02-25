package org.yunghegel.salient.editor.app.configs.ui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.engine.api.Configuration
import org.yunghegel.salient.engine.events.lifecycle.onEditorInitialized
import org.yunghegel.salient.engine.system.inject

@Serializable
class UIConfig(
    val leftLayout: LayoutConfig = LayoutConfig(splitAmount = 0.2f, splitType = "horizontal", activePanel = "Project"),
    val rightLayout: LayoutConfig = LayoutConfig(splitAmount = 0.8f, splitType = "horizontal", activePanel = "Scene"),
    val bottomLayout: LayoutConfig = LayoutConfig(splitAmount = 0.8f, splitType = "vertical", activePanel = "Log"),
) :Configuration() {

    @Transient
    var configured = false

    init {

        registerSyncAction {
            val gui: Gui = inject()
            gui.export(this)
        }

        onEditorInitialized {
            val gui : Gui = inject()
            gui.restore(this)
        }
    }


}
