package org.yunghegel.salient.editor.ui.scene.inspector.component

import com.badlogic.gdx.graphics.g3d.Material
import ktx.collections.GdxArray
import org.yunghegel.salient.editor.ui.scene.inspector.ComponentInspector
import org.yunghegel.salient.engine.graphics.scene3d.component.MaterialsComponent

class MaterialInspector : ComponentInspector<MaterialsComponent, GdxArray<Material>>(MaterialsComponent::class.java, "Materials", "material_object") {

    override fun populate(component: MaterialsComponent?) {

    }

    override fun createLayout() {

    }
}