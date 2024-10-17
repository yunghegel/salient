package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.gdx.utils.Align
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.widgets.EditableTextField

class ComponentTable(val component :EntityComponent<*>)  : ObjectTable(component.go) {

    override val button: SImageButton = SImageButton(component.iconName)
    override val label = EditableTextField(component::class.simpleName!!.substringBefore("Component")).apply {
        removeListener(this.listener)
    }

    override fun buildActor(obj: GameObject) {
        align(Align.left)
        button.style.up = null
        button.style.down = null
        button.style.checked = null
        button.style.over = null
        buttonLabelContainer.add(button).padRight(5f).size(20f)
        buttonLabelContainer.add(label)
        add(buttonLabelContainer)
    }
}