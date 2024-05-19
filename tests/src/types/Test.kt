package types

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Screen
import org.yunghegel.salient.editor.app.stage
import org.yunghegel.salient.engine.scene3d.TestBundle


interface Test : Screen {

    val name: String

    fun build()

    fun destroy()

    override fun dispose() {
        this@Test.dispose()
        destroy()
    }

    override fun show() {
        current = this
    }

    override fun hide() {
        current = null
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    companion object {

        var current : Test? = null

        internal val tests = mutableMapOf<String, Test>()
    }


}