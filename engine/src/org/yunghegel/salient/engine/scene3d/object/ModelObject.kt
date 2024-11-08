package org.yunghegel.salient.engine.scene3d.`object`

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Matrix4
import org.yunghegel.gdx.utils.ext.instance
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.api.properties.Subtype
import org.yunghegel.salient.engine.api.properties.Type
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.ModelRenderable
import org.yunghegel.salient.engine.scene3d.graph.ObjectNode
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.Icon

class ModelNode(name:String = "model", model: Model) : ObjectNode<Model>(name,model) {

    override fun emitComponents(obj: Model): List<BaseComponent> {
        val mats = obj.materials
        val mesh = obj.meshes
        val renderable = obj.instance



    }
}
