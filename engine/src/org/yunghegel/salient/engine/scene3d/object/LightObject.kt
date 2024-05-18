package org.yunghegel.salient.engine.scene3d.`object`

import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import org.yunghegel.salient.engine.api.properties.Subtype
import org.yunghegel.salient.engine.api.properties.Type
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.Icon

class LightObject<T:BaseLight<T>>(val light:T, name: String, scene : EditorScene = inject()) : GameObject(name, scene = scene), Icon,
    Subtype {

        override val identifier = "light"

        override val iconName: String = "light_object"


}