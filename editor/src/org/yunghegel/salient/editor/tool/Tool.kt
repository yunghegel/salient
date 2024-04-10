package org.yunghegel.salient.editor.tool

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import ktx.actors.onChange
import org.yunghegel.debug.Drawable
import org.yunghegel.salient.editor.app.App
import org.yunghegel.salient.editor.app.storage.Registry
import org.yunghegel.salient.editor.input.Input
import org.yunghegel.salient.editor.plugins.events.ToolLoadedEvent

import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.graphics.GridConfig

import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import kotlin.math.abs


abstract class Tool(val name:String) : InputAdapter() {

    val registry : Registry = inject()

    init {

        registry.indexTool(this)
        post(ToolLoadedEvent(this))
    }

    var app = inject<App>()

    val engine: Engine = inject()

    var projectManager = app.projectManager
    var sceneManager = app.sceneManager
    var assetManager = app.assetManager

    var group: ToolGroup? = null

    var activator: Family? = null

    val camera : PerspectiveCamera by lazy { inject() }

    open val blocking = false

    internal var active = false


    fun createActor(icon:com.badlogic.gdx.scenes.scene2d.utils.Drawable) : SImageButton {
        val button = SImageButton(icon)
        button.onChange {
            if(active)deactivate()
            else activate()
        }
        return button
    }

    fun createActor(drawableName :String) : SImageButton {
        val button = SImageButton(UI.skin.getDrawable(drawableName))
        button.onChange {
            if(active)deactivate()
            else activate()
        }
        return button
    }

    protected fun end() {
        group?.end(this)
    }



    open fun activate() {
        if(blocking) Input.pause(this)
        else Input.addProcessor(this)
        active = true
    }

    open fun deactivate() {
        if(blocking) Input.resume(this)
        else Input.removeProcessor(this)
        active = false
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
        return unproject(result, camera, screenX, screenY)
    }

    protected fun project(worldPosition: Vector2): Vector2 {
        val v: Vector3 =camera.project(Vector3(worldPosition.x, worldPosition.y, 0f))
        return Vector2(v.x, v.y)
    }




    open fun update(deltaTime: Float) {

    }

    open fun render(renderer: ShapeRenderer) {

    }

    open fun render(batch: Batch) {

    }

    open fun render(modelBatch: ModelBatch) {

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

        fun unproject(camera: Camera, screenX: Float, screenY: Float): Vector2 {
            return unproject(Vector2(), camera, screenX, screenY)
        }

        fun unproject(result: Vector2, camera: Camera, screenX: Float, screenY: Float): Vector2 {
            val base = camera.project(Vector3())
            val v = camera.unproject(Vector3(screenX, screenY, base.z))
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