package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider

class OutlinePixel {

    val program : ShaderProgram

    init {
        program = ShaderProgram(Gdx.files.internal("shaders/object_outline.vert").readString(), Gdx.files.internal("shaders/object_outline.frag").readString())
        if (!program.isCompiled) {
            throw Exception("Compile error:\n ${program.log}")
        }
    }

}