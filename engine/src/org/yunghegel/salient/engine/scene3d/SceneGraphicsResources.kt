package org.yunghegel.salient.engine.scene3d

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.salient.engine.graphics.debug.DebugContext
import org.yunghegel.salient.engine.helpers.BlinnPhongBatch
import org.yunghegel.salient.engine.helpers.DepthBatch
import org.yunghegel.salient.engine.helpers.WireBatch

interface SceneGraphicsResources {

    val modelBatch: ModelBatch
    val blinnPhongBatch : BlinnPhongBatch
    val pbrBatch : ModelBatch
    val depthBatch : DepthBatch
    val wireBatch : WireBatch
    val perspectiveCamera: PerspectiveCamera
    val orthographicCamera: OrthographicCamera
    val viewport: ScreenViewport
    val debugContext : DebugContext
    val shapeCache : ShapeCache
    val environment: Environment

}