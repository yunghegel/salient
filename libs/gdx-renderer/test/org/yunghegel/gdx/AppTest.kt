/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.yunghegel.gdx

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation
import com.badlogic.gdx.graphics.Pixmap
import org.yunghegel.gdx.renderer.mrt.*
import org.yunghegel.gdx.renderer.mrt.TextureAttachment.Companion.toFbo
import org.yunghegel.gdx.util.TestApplication
import org.yunghegel.gdx.util.launch
import org.yunghegel.gdx.util.maskOf
import kotlin.test.Test
import kotlin.test.assertNotNull

class EnumTextureAttachmentTest {
    val mask = maskOf(TextureAttachment.DIFFUSE, TextureAttachment.NORMAL)

    @Test fun enumTest() {
        val mask = maskOf(TextureAttachment.DIFFUSE, TextureAttachment.NORMAL)

        assert(mask.contains(TextureAttachment.DIFFUSE) && mask.contains(TextureAttachment.NORMAL))

        TestApplication (createContext = {
            val fbo = mask.toFbo(mask)
            assertNotNull(fbo)
            fbo.textureAttachments.forEach {
                println(it.textureData.format)
                assert(it.textureData.format == Pixmap.Format.RGBA8888)
            }
        }, render = {}).launch { cfg -> cfg.setOpenGLEmulation(GLEmulation.GL32,3,2)}


    }

}
