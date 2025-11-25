package org.yunghegel.salient.engine.graphics

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
import org.yunghegel.salient.engine.scene3d.SceneGraphicsResources
import org.yunghegel.salient.engine.system.inject

interface GraphicsContext : SceneGraphicsResources, SharedGraphicsResources