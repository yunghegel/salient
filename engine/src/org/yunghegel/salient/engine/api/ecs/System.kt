package org.yunghegel.salient.engine.api.ecs

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.gdx.utils.data.Named

abstract class System<Project:EditorProject<Project,Scene>,Scene:EditorScene>(prio : Int = 0, family:Family= Family.all().get()) : IteratingSystem(family,prio), Named
