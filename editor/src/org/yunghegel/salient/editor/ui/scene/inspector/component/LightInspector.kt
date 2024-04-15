package org.yunghegel.salient.editor.ui.scene.inspector.component

import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import ktx.collections.GdxArray
import org.yunghegel.salient.editor.ui.scene.inspector.ComponentInspector
import org.yunghegel.salient.engine.graphics.scene3d.component.LightComponent

class LightInspector : ComponentInspector<LightComponent, GdxArray<BaseLight<*>>>(LightComponent::class.java, "Lighting", "light_object") {


    override fun populate(component: LightComponent?) {
    }

    override fun createLayout() {

    }
}