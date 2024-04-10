package org.yunghegel.salient.editor.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.input.GestureDetector.GestureListener
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Plane
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.viewport.Viewport
import org.yunghegel.salient.editor.app.dto.datatypes.Vector3Data

class EditorCamera(var perspectiveCamera: PerspectiveCamera, var viewport: Viewport) {

    init {

    }

    fun setEnabled(enabled: Boolean) {
        perspectiveCameraController.enabled = enabled
    }

    val perspectiveCameraController: PerspectiveCameraController =
        PerspectiveCameraController(perspectiveCamera, viewport)

    data class Config(
            var velocity: Float = 30f,
            val target: Vector3Data = Vector3Data(
                0f,
                0f,
                0f
                                                 ),
            var distanceToHome: Float = 10f,
            var homeTarget: Vector3Data = Vector3Data(
                0f,
                0f,
                0f
                                                     )
                     )

    data class Keybinds(
            var rotate: Int = Input.Buttons.LEFT,
            var pan: Int = Input.Buttons.MIDDLE,
            var zoom: Int = Input.Buttons.FORWARD,
            var home: Int = Input.Keys.CONTROL_LEFT,
            var reset: Int = Input.Keys.PERIOD,
            var button: Int = Input.Buttons.LEFT
                       )

    abstract class CameraController(var camera: PerspectiveCamera):InputAdapter(), GestureListener {

        var config = Config()
        var keybinds = Keybinds()
        var cam = camera
        var tmp: Keybinds = keybinds.copy()
        var changed: Boolean = false

        abstract fun update(delta: Float)

        fun pauseInput() {
            tmp = keybinds.copy()
            keybinds.pan = -1
            keybinds.rotate = -1
            keybinds.zoom = -1
            keybinds.home = -1
        }

        fun resumeInput() {
            keybinds = tmp.copy()
        }

    }

    class PerspectiveCameraController(camera: PerspectiveCamera, var viewport: Viewport):CameraController(camera) {

        val rayCaster = object:Raycaster.Raycast {
            override fun castRay(ray: Ray, pos: Vector3, callback: Raycaster.RaycastCallback): Raycaster.RaycastResult {
                var result = Raycaster.RaycastResult()
                var plane = Plane(Vector3.Y, 0f)
                if (Intersector.intersectRayPlane(ray, plane, result.pos)) {
                    callback.onRaycast(Raycaster.RaycastResult(true, pos, Vector3.Y))
                }
                else {
                    callback.onRaycast(Raycaster.RaycastResult(false, pos, Vector3.Y))
                }
                return result
            }
        }
        val raySupplier = object:Raycaster.SupplyRay {
            override fun supplyRay(x: Float, y: Float): Ray {
                return viewport.getPickRay(x, y)
            }
        }

        var tangent = Vector3()
        var prevTarget = Vector3()
        var prevPosition = Vector3()
        var movePosition = Vector3()
        var oldCameraDir = Vector3()
        var oldCameraPos = Vector3()

        var tmpV1 = Vector3()
        var tmpV2 = Vector3()

        var moveTarget: Vector3? = null
        var target = Vector3(config.target.x, config.target.y, config.target.z)
        var homeTarget = Vector3(config.homeTarget.x, config.homeTarget.y, config.homeTarget.z)

        var time = 0f
        var homeDistance = config.distanceToHome
        var velocity = config.velocity
        var pinchZoomFactor = 10f
        var translateUnits = 30f
        var scrollFactor = -0.1f
        var previousZoom = 0f
        var rotateAngle = 360f
        var startX = 0f
        var startY = 0f

        var enabled = true

        override fun update(delta: Float) {
            if (!enabled) return
            target.y = 0f
            if (Gdx.input.isKeyPressed(keybinds.reset)) {
                moveTarget = Vector3(homeTarget)
                prevTarget.set(target)
                prevPosition.set(camera.position)
                movePosition = Vector3().set(homeTarget).sub(prevPosition).nor().scl(-homeDistance).add(homeTarget)
                time = 0f
            }
            if (moveTarget != null) {

                time += delta * 2f
                target.set(prevTarget).lerp(moveTarget, time)
                camera.position.set(prevPosition).lerp(movePosition, time)
                if (time >= 1f) {
                    moveTarget = null
//                    keybinds.pan = tmp.pan
//                    keybinds.rotate = tmp.rotate
//                    keybinds.zoom = tmp.zoom
//                    keybinds.home = tmp.home
                }
                camera.up.set(Vector3.Y)
                camera.lookAt(target)
                changed = true
            }

            tangent.set(camera.direction).crs(camera.up).nor()
            translateUnits = cam.position.dst(target)

        }

        fun process(keycode: Int, deltaX: Float, deltaY: Float) {
            if (!enabled) return
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                tmpV1.set(camera.direction).crs(camera.up).y = 0f
                camera.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle)
                camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle)
            }
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits))
                camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits))
                target.add(tmpV1).add(tmpV2)
            }
            if (Gdx.input.isButtonPressed(keybinds.zoom)) {
                camera.translate(tmpV1.set(camera.direction).scl(deltaY * translateUnits))
            }
            if (Gdx.input.isKeyPressed(keybinds.home)) {
                moveTarget = Vector3(homeTarget)
                prevTarget.set(target)
                prevPosition.set(camera.position)
                movePosition = Vector3().set(homeTarget).sub(prevPosition).nor().scl(-homeDistance).add(homeTarget)
                time = 0f
            }
            else {
                moveTarget = null
            }
            camera.up.set(Vector3.Y)
            camera.lookAt(target)
            changed = true

        }

        fun render() {
//
//            var intersection = Vector3()
//            Intersector.intersectRayPlane(
//                UI.gui!!.viewportWidget.viewport.getPickRay(
//                    Gdx.input.x.toFloat(),
//                    Gdx.input.y.toFloat()
//                ), Plane(Vector3.Y, 0f), intersection
//            )

//           with(shapeRenderer){
//               projectionMatrix = camera.combined
//                begin(ShapeRenderer.ShapeType.Line)
//                color = Color.RED
//                line(target, target.cpy().add(Vector3.X.cpy().scl(.1f)))
//                color = Color.GREEN
//                line(target, target.cpy().add(Vector3.Y.cpy().scl(0.1f)))
//                color = Color.BLUE
//                line(target, target.cpy().add(Vector3.Z.cpy().scl(0.1f)))
//                end()
//           }

        }

        override fun keyDown(keycode: Int): Boolean {

            val rotationSteps = 90f / 6
            if (keycode == (Input.Keys.A)) {
                camera.rotateAround(target, Vector3.Y, -rotationSteps)
                changed = true
            }
            if (keycode == (Input.Keys.D)) {
                camera.rotateAround(target, Vector3.Y, rotationSteps)
                changed = true
            }
            if (keycode == (Input.Keys.W)) {
                camera.rotateAround(target, tangent, -rotationSteps)
                changed = true
            }
            if (keycode == (Input.Keys.S)) {
                camera.rotateAround(target, tangent, rotationSteps)
                changed = true
            }
            if (keycode == (Input.Keys.CONTROL_LEFT) || keycode == (Input.Keys.CONTROL_RIGHT)) {

                keybinds.zoom = keybinds.button
            }
            else
                if (keycode == (Input.Keys.SHIFT_LEFT) || keycode == (Input.Keys.SHIFT_RIGHT)) {
                    keybinds.pan = keybinds.button

                }
                else {

                    keybinds.rotate = keybinds.button

                }

            return super.keyDown(keycode)
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            keybinds.button = button
            startX = screenX.toFloat()
            startY = screenY.toFloat()
            oldCameraDir.set(camera.direction)
            oldCameraPos.set(camera.position)

            return super.touchDown(screenX, screenY, pointer, button)
        }

        override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {

            return touchDown(x, y, pointer, button)

        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            keybinds.button = button
            return super.touchUp(screenX, screenY, pointer, button)
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            val result = super.touchDragged(screenX, screenY, pointer)
            if (result || this.keybinds.button < 0) return result
            val deltaX = (screenX - startX) / Gdx.graphics.width
            val deltaY = (startY - screenY) / Gdx.graphics.height
            startX = screenX.toFloat()
            startY = screenY.toFloat()
            process(keybinds.button, deltaX, deltaY)
            return true
        }

        override fun scrolled(amountX: Float, amountY: Float): Boolean {
            return zoom(amountY * scrollFactor * translateUnits)
        }

        override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
            TODO("Not yet implemented")
        }

        override fun longPress(x: Float, y: Float): Boolean {
            TODO("Not yet implemented")
        }

        override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
            TODO("Not yet implemented")
        }

        override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
            TODO("Not yet implemented")
        }

        override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
            TODO("Not yet implemented")
        }

        protected fun pinchZoom(amount: Float): Boolean {
            return zoom(pinchZoomFactor * amount)
        }

        override fun zoom(initialDistance: Float, distance: Float): Boolean {
            val newZoom = distance - initialDistance
            val amount: Float = newZoom - previousZoom
            previousZoom = newZoom
            val w = Gdx.graphics.width.toFloat()
            val h = Gdx.graphics.height.toFloat()
            return pinchZoom(amount / if (w > h) h else w)
        }

        fun zoom(amount: Float): Boolean {
            camera.translate(tmpV1.set(camera.direction).scl(amount))
            return true
        }

        override fun pinch(
                initialPointer1: Vector2?,
                initialPointer2: Vector2?,
                pointer1: Vector2?,
                pointer2: Vector2?
                          ): Boolean {
            TODO("Not yet implemented")
        }

        override fun pinchStop() {
            TODO("Not yet implemented")
        }

    }

}

class Raycaster {

    data class RaycastResult(var hit: Boolean = false, var pos: Vector3 = Vector3(), var normal: Vector3 = Vector3())

    interface Raycast {

        fun castRay(ray: Ray, pos: Vector3, callback: RaycastCallback): RaycastResult
    }

    interface SupplyRay {

        fun supplyRay(x: Float, y: Float): Ray
    }

    interface RaycastCallback {

        fun onRaycast(result: RaycastResult)
    }

}
