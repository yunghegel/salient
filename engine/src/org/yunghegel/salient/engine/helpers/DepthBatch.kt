package org.yunghegel.salient.engine.helpers

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider

class DepthBatch (depthProvider : DepthShaderProvider): ModelBatch(depthProvider)