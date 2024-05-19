import ktx.app.clearScreen
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.core.graphics.util.OutlineDepth
import org.yunghegel.salient.editor.plugins.outline.lib.Outliner
import types.lwjgl3test

val OutlineTest  = lwjgl3test("OutlineTest") {

    lateinit var outliner : OutlineDepth

    execCreate = {
        outliner = OutlineDepth()
        bundle.init2D()
        bundle.init3D()
    }

    execRender = {
        use {

            val tex = pass(fbo0) {
                clearScreen(0f,0f,0f,0f)
                depth.begin(cam)
                depth.render(instances,env)
                depth.end()
            }

            val tex2 = pass(fbo1) {
                clearScreen(0f,0f,0f,0f)
                batch.begin(cam)
                batch.render(instances, env)
                batch.end()
            }



            tex2.draw(spriteBatch)
            Outliner.settings.run {
                outliner.render(spriteBatch,tex,cam)
            }






        }
    }



}

