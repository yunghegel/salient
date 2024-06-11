package lwjgl3

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.instances
import kotlinx.coroutines.launch
import ktx.actors.onChange
import ktx.app.clearScreen
import ktx.async.KtxAsync
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.core.graphics.util.OutlineDepth
import org.yunghegel.salient.editor.plugins.outline.lib.Outliner
import org.yunghegel.salient.engine.graphics.GFX.spriteBatch
import org.yunghegel.salient.engine.helpers.SampleModels
import types.lwjgl3test
@OptIn(kotlin.ExperimentalStdlibApi::class)
object OutlineTest {

    val test  = lwjgl3test("OutlineTest") {

        lateinit var outliner : OutlineDepth

        execCreate = {
            outliner = OutlineDepth()
            bundle.init2D()
            bundle.init3D()
            choice("model",*SampleModels.entries.toTypedArray(), map = {it -> it.name}, cb = { model ->
                with(bundle) {
                    instances.clear()
                    KtxAsync.launch {
                        model.loadAsync(ext="gltf") { mdl ->
                            val i = mdl!!.instance
                            instances.add(i)
                            i.transform.setTranslation(0f,0f,0f)
                        }
                    }

                }

            } )
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

    @JvmStatic
    fun main(args: Array<String>) {
        test()
    }
}

