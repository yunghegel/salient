package types

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Screen
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import org.yunghegel.salient.editor.app.stage
import org.yunghegel.salient.engine.scene3d.TestBundle


interface Test {

    var execCreate : ()->Unit

    var execRender : ()->Unit

    var name: String

    companion object {

        var current : Test? = null

        internal val tests = mutableMapOf<String, Test>()
    }


}