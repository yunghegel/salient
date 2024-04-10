package org.yunghegel.gdx

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.graphics.GL20
import com.sun.jdi.Mirror
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import net.mgsx.gltf.scene3d.scene.MirrorSource
import net.mgsx.gltf.scene3d.scene.Scene
import net.mgsx.gltf.scene3d.scene.SceneManager
import net.mgsx.gltf.scene3d.scene.TransmissionSource
import org.yunghegel.gdx.util.*

class PBRTest  : App() {

    lateinit var kit : TestKit
    lateinit var sceneManager: SceneManager

    override fun create() {
        sceneManager = quickSceneManagerSetup()
        kit = TestKit {
            val models = SampleModels.entries
            val scenes = models.map { GLTFLoader().load(Gdx.files.internal("models/gltf/${it.model}.gltf"))!!.scene }

            val mdls = scenes.forEachIndexed { index, it ->

                sceneManager.addScene(Scene(it).also {
                    when (index%2==0) {
                        true -> it.modelInstance.transform.setToTranslation(- index * 1.5f, 0f, 0f)
                        false -> it.modelInstance.transform.setToTranslation(index * 1.5f, 0f, 0f)
                    }
                })
                env = sceneManager.environment
            }
            sceneManager.camera = cam
            cam.position.set(5f,1f,5f)
            cam.near=0.1f
            cam.far = 100f
            cam.lookAt(0f,0f,0f)
            setupIBL(sceneManager)
            sceneManager.setMirrorSource(MirrorSource())
        }
    }

    override fun render() {
        super.render()
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT or GL20.GL_COLOR_BUFFER_BIT)

        sceneManager.update(Gdx.graphics.deltaTime)
        sceneManager.render()
    }


}

fun main() {
    PBRTest().run()
}
