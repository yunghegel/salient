package org.yunghegel.salient.editor.ui.scene.inspector

import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.ui.scene2d.SLabel

class SelectionView : BaseInspector("Selection","settings") {

    init {

    }

    override fun createLayout() {
    }

    fun populate(gameObject: GameObject) {
        add(SLabel("Name: ${gameObject.name}"))
    }

}