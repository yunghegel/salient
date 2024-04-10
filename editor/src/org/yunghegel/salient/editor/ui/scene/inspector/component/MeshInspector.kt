package org.yunghegel.salient.editor.ui.scene.inspector.component

import com.badlogic.gdx.graphics.Mesh
import ktx.collections.GdxArray
import org.yunghegel.salient.editor.ui.scene.inspector.ComponentInspector
import org.yunghegel.salient.engine.graphics.scene3d.component.MeshComponent

class MeshInspector : ComponentInspector<MeshComponent, GdxArray<Mesh>>(MeshComponent::class.java, "Mesh", "mesh_object") {

    override fun populate(component: MeshComponent) {

    }

    override fun createLayout() {

    }
}