package org.yunghegel.salient.engine.ecs

import com.badlogic.ashley.core.EntitySystem
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.sys.inject

abstract class System<Project:EditorProject<Project,Scene>,Scene:EditorScene>(prio : Int) : EntitySystem(prio) {

}