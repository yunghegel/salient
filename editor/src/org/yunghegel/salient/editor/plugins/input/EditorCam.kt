package org.yunghegel.salient.editor.plugins.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import org.yunghegel.salient.editor.tool.Tool
import org.yunghegel.salient.engine.io.inject
import kotlin.math.abs
import kotlin.math.tan


class EditorCamera {
    var isActive: Boolean = false
        private set
    private var orthoMode = false

    private var orthographicCamera: OrthographicCamera = inject()
    private var perspectiveCamera: PerspectiveCamera = inject()

    init {
        createCamera()

    }

    private fun createCamera() {
        isActive = true

        val pc = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        pc.position[0f, 0f] = 10f
        pc.up[0f, 1f] = 0f
        pc.lookAt(0f, 0f, 0f)
        pc.near = 1f
        pc.far = 3000f
        pc.update()

        perspectiveCamera = pc

        orthographicCamera = OrthographicCamera()
    }

    fun zoom(rate: Float) {
        var rate = rate
        rate *= Tool.pixelSize(perspectiveCamera).x * Gdx.graphics.width // 100 world unit per pixel TODO pixelSize !!

        if (orthoMode) {
            perspectiveCamera!!.position.set(orthographicCamera!!.position)
            perspectiveCamera!!.translate(0f, 0f, -rate)
            perspectiveCamera!!.update(false)
            syncOrtho(true)
        } else {
            perspectiveCamera!!.translate(0f, 0f, -rate)
            perspectiveCamera!!.update(false)
        }
    }

    fun fov(rate: Float) {
        var rate = rate
        if (orthoMode) return

        rate *= 360f // degree scale

        syncPerspective(false)


        // translate camera according to FOV changes (keep sprite plan unchanged !)
        val oldFOV: Float = perspectiveCamera!!.fieldOfView * MathUtils.degreesToRadians * 0.5f

        val hWorld = tan(oldFOV.toDouble()).toFloat() * perspectiveCamera!!.position.z

        perspectiveCamera!!.fieldOfView += rate
        perspectiveCamera!!.update(true)

        val newFOV: Float = perspectiveCamera!!.fieldOfView * MathUtils.degreesToRadians * 0.5f

        val distWorld = hWorld / tan(newFOV.toDouble()).toFloat()

        val deltaZ = distWorld - perspectiveCamera!!.position.z
        perspectiveCamera!!.translate(0f, 0f, deltaZ)
        perspectiveCamera!!.update(false)


        syncOrtho(false)
    }

    private fun syncPerspective(force: Boolean) {
        if (orthoMode || force) {
            perspectiveCamera!!.position.set(orthographicCamera!!.position)
            perspectiveCamera!!.update(true)
        }
    }

    private fun syncOrtho(force: Boolean) {
        if (!orthoMode || force) {
            // sync sprite plan for ortho (working !)
            val objectDepth = perspectiveCamera!!.project(Vector3())

            val a = perspectiveCamera!!.unproject(Vector3(0f, 0f, objectDepth.z))
            val b = perspectiveCamera!!.unproject(Vector3(1f, 1f, objectDepth.z))
            b.sub(a)

            val w = (abs(b.x.toDouble()) * Gdx.graphics.width).toFloat()
            val h = (abs(b.y.toDouble()) * Gdx.graphics.height).toFloat()

            orthographicCamera!!.setToOrtho(false, w, h)
            orthographicCamera!!.position.set(perspectiveCamera!!.position)

            orthographicCamera!!.update(true)
        }
    }

    fun switchCamera() {
        if (!orthoMode) {
            syncOrtho(true)
            orthoMode = true
        } else {
            syncPerspective(true)
            orthoMode = false
        }
    }

    fun switchCameras() {
        isActive = !isActive
    }

    fun resize(width: Int, height: Int) {
        syncPerspective(false)

        perspectiveCamera!!.viewportWidth = Gdx.graphics.width.toFloat()
        perspectiveCamera!!.viewportHeight = Gdx.graphics.height.toFloat()
        perspectiveCamera!!.update(true)

        syncOrtho(false)
    }

    fun camera(): Camera? {
        return if (orthoMode) {
            orthographicCamera
        } else {
            perspectiveCamera
        }
    }

    fun reset() {
        createCamera()
    }

    fun disable() {
        isActive = false
    }

    fun enable() {
        isActive = true
    }
}