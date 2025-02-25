package lwjgl3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.sun.tools.javac.tree.TreeInfo.args
import net.mgsx.gltf.scene3d.shaders.PBREmissiveShaderProvider.createConfig
import org.yunghegel.salient.engine.ui.widgets.viewport.Compass
import types.lwjgl3test

val CompassTest = lwjgl3test("CompassTest") {

    lateinit var compass: Compass

    execCreate = {
        init2D()
        init3D(true)
        compass = Compass(bundle.cam)

    }

    execRender = {

        compass.setPos(0.8f, 0.8f)

        compass.update(Gdx.graphics.deltaTime, 0.1f, 0.1f)
        bundle.draw3D()
        bundle.apply {
            compass.render(Gdx.graphics.deltaTime)
        }
    }

}

fun main() = CompassTest()