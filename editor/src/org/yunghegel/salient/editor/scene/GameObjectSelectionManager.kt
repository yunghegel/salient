package org.yunghegel.salient.editor.scene

import com.badlogic.gdx.scenes.scene2d.utils.Selection
import imgui.ImGui.isKeyPressed
import ktx.collections.GdxArray
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.ui.scene.graph.SceneGraphTree
import org.yunghegel.salient.editor.ui.scene.inspector.SceneInspector
import org.yunghegel.salient.engine.api.BaseSelectionManager
import org.yunghegel.salient.engine.api.flags.SELECTED
import org.yunghegel.salient.engine.api.undo.SelectionListener
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.scene.SingleGameObjectDeselectedEvent
import org.yunghegel.salient.engine.events.scene.SingleGameObjectSelectedEvent
import org.yunghegel.salient.engine.helpers.Ignore
import org.yunghegel.salient.editor.modules.InputModule
import org.yunghegel.salient.engine.input.Keys
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.inject

class GameObjectSelectionManager(selection : Selection<GameObject>) : BaseSelectionManager<GameObject>(selection) {

    @Ignore
    val tree: SceneGraphTree by lazy { inject() }
    val inspector: SceneInspector by lazy { inject() }

    val gui: Gui by lazy { inject() }
    val input: InputModule by lazy { inject() }

    override val allowMultiple: Boolean
        get() {
            return (input.isKeyPressed(Keys.SHIFT_LEFT) || input.isKeyPressed(Keys.SHIFT_RIGHT)) ||
                    (input.isKeyPressed(Keys.CONTROL_LEFT) || input.isKeyPressed(Keys.CONTROL_RIGHT))
        }
    override val listeners: GdxArray<SelectionListener<GameObject>> = GdxArray()

    override fun select(go: GameObject, append: Boolean): Boolean {
        val result = super.select(go, append)
        if (result) {
            go.set(SELECTED)
            post(SingleGameObjectSelectedEvent(go))
        }
        return result
    }

    override fun deselect(go: GameObject): Boolean {
        val result = super.deselect(go)
        if (result) {
            go.clear(SELECTED)
            post(SingleGameObjectDeselectedEvent(go))
        }
        return result
    }



}