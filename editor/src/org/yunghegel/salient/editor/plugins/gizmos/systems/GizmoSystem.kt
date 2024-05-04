package org.yunghegel.salient.editor.plugins.gizmos.systems

import com.badlogic.ashley.core.Family
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.engine.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.scene3d.component.TransformComponent

class GizmoSystem : BaseSystem("gizmo_system",0, Family.all(TransformComponent::class.java, SelectedComponent::class.java).get()) {
}