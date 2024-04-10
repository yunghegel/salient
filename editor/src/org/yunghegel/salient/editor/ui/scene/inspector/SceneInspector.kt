package org.yunghegel.salient.editor.ui.scene.inspector

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.editor.ui.scene.inspector.component.LightInspector
import org.yunghegel.salient.editor.ui.scene.inspector.component.MaterialInspector
import org.yunghegel.salient.editor.ui.scene.inspector.component.MeshInspector
import org.yunghegel.salient.editor.ui.scene.inspector.component.TransformInspector
import org.yunghegel.salient.engine.ui.scene2d.STable

class SceneInspector : STable() {

    val types : List<Class<out BaseInspector>> =  listOf(
        GlobalSettings::class.java,
        SelectionView::class.java,
        TransformInspector::class.java,
        MaterialInspector::class.java,
        LightInspector::class.java,
        MeshInspector::class.java,
    )
    private val content = STable()

    val inspectorSidebar = InspectorSidebar(content)

    val inspectors : List<BaseInspector> = types.map { it.getDeclaredConstructor().newInstance() }

    fun set(inspector: BaseInspector) {
        inspectorSidebar.buttonGroup.buttons.forEach {
            if (it.actor == inspector) {
                it.fire(ChangeEvent())
            }
        }
    }

    init {
        align(Align.center)
        add(inspectorSidebar).growY().width(22f)
        add(Separator()).growY()
        add(content).grow()
        content.align(Align.center)
        inspectors.forEachIndexed { index, inspector ->

            inspectorSidebar.addOption(inspector.icon,inspector)
            if (index==1) inspectorSidebar.add(Separator()).growX().padHorizontal(5f).height(2f).row()
        }
        inspectorSidebar.buttonGroup.buttons[2].apply {
            fire(ChangeEvent())
            isChecked = true
        }

    }

}