package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.salient.engine.ecs.EntityComponent

class ModelComponent(model:Model) : EntityComponent<Model>(Model::class.java,model)