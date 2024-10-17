package lwjgl3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.engine.helpers.SampleModels
import types.lwjgl3test
val DepthBufferTest = lwjgl3test("DepthBufferTest") {



        execCreate = {
            bundle.init2D()
            bundle.init3D(false,SampleModels.ROUND_CUBE)
        }

        execRender =  with(bundle) {
            {

                cam.near=1f
                val depth = pass(fbo0) {
                    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
                    depth.begin(cam)
                    depth.render(instances,env)
                    depth.end()
                }
                spriteBatch.begin()
                spriteBatch.draw(fbo0.colorBufferTexture,0f,0f,800f,600f)
                spriteBatch.end()



            }
        }

    }





