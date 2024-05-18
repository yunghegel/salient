package org.yunghegel.salient.engine.scene3d.`object`

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Matrix4
import org.yunghegel.salient.engine.api.properties.Subtype
import org.yunghegel.salient.engine.api.properties.Type
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.Icon

class ModelObject(val model : Model, name:String, scene: EditorScene = inject(),  transform: Matrix4 = Matrix4()) : GameObject(name,  transform = transform, scene = scene),
    Subtype, Icon {

        override val identifier = "model"

        override val iconName: String = "model_object"

}
