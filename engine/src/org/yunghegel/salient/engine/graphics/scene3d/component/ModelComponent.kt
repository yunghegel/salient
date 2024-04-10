package org.yunghegel.salient.engine.graphics.scene3d.component

import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

class ModelComponent(model:Model,go: GameObject) : EntityComponent<Model>(Model::class.java,model,go)