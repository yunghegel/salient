package org.yunghegel.salient.editor.app.configs.ui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.app.configs.Configuration
import org.yunghegel.salient.engine.events.lifecycle.onEditorInitialized
import org.yunghegel.salient.engine.system.inject

@Serializable
class UIConfig(
    val leftLayout: LayoutConfig = LayoutConfig(splitAmount = 0.2f, splitType = "horizontal", activePanel = "Project"),
    val rightLayout: LayoutConfig = LayoutConfig(splitAmount = 0.8f, splitType = "horizontal", activePanel = "Scene"),
    val bottomLayout: LayoutConfig = LayoutConfig(splitAmount = 0.8f, splitType = "vertical", activePanel = "Log"),
) :Configuration(){

    @Transient
    var configured = false

    init {
        registerSyncAction {

            val gui : Gui = inject()

            with(gui) {
                with(split) {
                    leftLayout.splitAmount = cache.get(0, leftLayout.splitAmount+0.02f)
                    leftLayout.hidden = gui.left.hidden

                    rightLayout.splitAmount = cache.get(1, rightLayout.splitAmount-0.02f)
                    rightLayout.hidden = gui.right.hidden
                }

                with(centerSplit) {
                    bottomLayout.splitAmount = split
                    bottomLayout.hidden = gui.center.hidden
                }
            }
        }

        onEditorInitialized {
            val gui : Gui = inject()
            gui.split.setSplitInternal(0, leftLayout.splitAmount)
            gui.split.setSplitInternal(1, rightLayout.splitAmount)
            gui.centerSplit.setSplitAmount(bottomLayout.splitAmount)  }
        }



}