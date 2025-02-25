package org.yunghegel.salient.engine.api.tool

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.onChange
import org.yunghegel.gdx.utils.data.EnumBitmask
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.ToolLoadedEvent
import org.yunghegel.salient.engine.graphics.GridConfig
import org.yunghegel.salient.engine.graphics.RenderUsage
import org.yunghegel.salient.engine.graphics.util.DebugDrawer
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import kotlin.math.abs


abstract class InputTool(name: String, key: Int = -1) : AbstractTool(name), Named {


    protected val entity = ToolEntity(this)

    fun setRenderUsage(vararg usage: RenderUsage) {
        usage.forEach { renderMask.set(it, true) }
    }

    val engine: Pipeline by lazy { inject() }

    init {
        post(ToolLoadedEvent(this))
        engine.addEntity(entity)
    }


    var group: ToolGroup? = null

    var activator: Family? = null

    val camera: PerspectiveCamera by lazy { inject() }

    val debugDrawer: DebugDrawer by lazy { inject() }

    val sceneContext: SceneContext by lazy { inject() }

    open val blocking = false

    var exclusiveInputSource = false


    protected fun end() {
        group?.end(this)
    }


    protected fun screenToWorldSnap(screenX: Float, screenY: Float, out: Vector2 = Vector2()): Vector2 {
        return snap(unproject(out, screenX, screenY))
    }

    private fun snap(x: Float, size: Float): Float {
        val n: Int = MathUtils.round(x / size)
        val t = abs(x / size - n - 0.5).toFloat()
        return if (t > 0.3f) n * size else x
    }

    protected fun snap(v: Vector2): Vector2 {
        val cfg: GridConfig = inject()
        if (cfg.snap) {
            val size: Float = cfg.gridSize
            v.x = snap(v.x, size)
            v.y = snap(v.y, size)
        }
        return v
    }


    protected fun unproject(result: Vector2, screenX: Float, screenY: Float): Vector2 {
        return unproject(result, sceneContext.viewport, screenX, screenY)
    }

    protected fun project(worldPosition: Vector2): Vector2 {
        val v: Vector3 = camera.project(Vector3(worldPosition.x, worldPosition.y, 0f))
        return Vector2(v.x, v.y)
    }


    protected fun pixelSize(): Vector2 {
        return pixelSize(camera)
    }

    fun allowed(selection: Array<Entity?>): Boolean {
        if (selection.size > 1) return false
        if (activator != null) {
            if (selection.isNotEmpty()) return activator!!.matches(selection.first())
            return false
        }
        return true
    }


    companion object {

        fun unproject(viewport: Viewport, screenX: Float, screenY: Float): Vector2 {
            return unproject(Vector2(), viewport, screenX, screenY)
        }

        fun unproject(result: Vector2, viewport: Viewport, screenX: Float, screenY: Float): Vector2 {
            val base = viewport.project(Vector3())
            val v = viewport.unproject(Vector3(screenX, screenY, base.z))
            return result.set(v.x, v.y)
        }

        private val worldDepth = Vector3()
        private val worldSpace1 = Vector3()
        private val worldSpace2 = Vector3()
        private val pixelSpace = Vector2()

        internal val out = Vector2()
        internal val out3 = Vector3()

        fun pixelSize(camera: Camera): Vector2 {
            // that was the old method for orthographic camera
            // TODO do the same for perspective as well.

            camera.project(worldDepth.setZero())

            camera.unproject(worldSpace1.set(0f, 0f, worldDepth.z))
            camera.unproject(worldSpace2.set(1f, 1f, worldDepth.z))
            worldSpace2.sub(worldSpace1)


            return pixelSpace.set(worldSpace2.x, worldSpace2.y)
        }
    }
}