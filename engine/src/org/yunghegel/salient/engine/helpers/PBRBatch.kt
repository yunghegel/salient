package org.yunghegel.salient.engine.helpers

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import org.yunghegel.gdx.renderer.shader.PBRShaderProvider

class PBRBatch() : ModelBatch(PBRShaderProvider(), DefaultRenderableSorter()){
}