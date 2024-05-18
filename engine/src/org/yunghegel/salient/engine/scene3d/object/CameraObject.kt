package org.yunghegel.salient.engine.scene3d.`object`

import com.badlogic.gdx.graphics.Camera
import org.yunghegel.salient.engine.api.properties.Subtype
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.ui.Icon

class CameraObject (val camera: Camera, name: String, scene : EditorScene) : GameObject(name, scene = scene, transform = camera.view) , Icon, Subtype {

    override val identifier = "camera"
    override val iconName: String = "camera_object"

}