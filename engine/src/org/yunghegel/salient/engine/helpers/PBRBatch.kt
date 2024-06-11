package org.yunghegel.salient.engine.helpers

import com.badlogic.gdx.graphics.g3d.ModelBatch
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider

class PBRBatch : ModelBatch(PBRShaderProvider(PBRShaderProvider.createDefaultConfig()), SceneRenderableSorter())