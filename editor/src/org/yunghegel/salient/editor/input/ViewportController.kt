package org.yunghegel.salient.editor.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Plane
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.gdx.utils.ext.distanceFalloff
import org.yunghegel.salient.editor.app.stage
import org.yunghegel.salient.editor.app.ui
import org.yunghegel.salient.engine.io.inject

class ViewportController : DragListener() {

    var rotate: Int = Input.Buttons.LEFT
    var pan: Int = Input.Buttons.LEFT + Input.Keys.SHIFT_LEFT
    var zoom: Int = Input.Buttons.MIDDLE
    var home: Int = Input.Keys.CONTROL_LEFT
    var reset: Int = Input.Keys.PERIOD
    var btn: Int = Input.Buttons.LEFT

    val viewport : ScreenViewport = inject()

    var moveTarget: Vector3? = null
    var homeTarget: Vector3 = Vector3()
    val oldCameraDir = Vector3()
    val oldCameraPos = Vector3()
    val target = Vector3()
    val tangent = Vector3()

    var prevTarget = Vector3()
    var prevPosition = Vector3()
    var movePosition = Vector3()

    var startX = 0f
    var startY = 0f
    var dX = 0f
    var dY = 0f

    var tmpV1 = Vector3()
    var tmpV2 = Vector3()
    var smoothed = Vector3()

    var lastDeltaX = 0f
    var lastDeltaY = 0f
    var numEventsPolled = 0
    var timeElapsed = 0f

    var translateUnits = 15f
    var scrollUnits  = 50f
    var rotationSteps = 90f

    var rotateAngle = 360f
    var homeDistance  = 10f

    var maxDist = 50f
    var minDist = 2f

    val clickListener = object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            if (tapCount ==2) {
                val result = intersectRayXZ(x,y) ?: return
                println("Clicked at $result")

            }
        }
    }

    fun intersectRayXZ(x: Float, y: Float): Vector3? {
        val ray = camera.getPickRay(x, y)
        val plane = Plane(Vector3.Y, 0f)
        val intersection = Vector3()
        val success = Intersector.intersectRayPlane(ray, plane, intersection)
        return if (success) intersection else null

    }



    val camera : PerspectiveCamera = inject()
    var key = -1

    var actor : Actor? = null

    init {
        setDistanceToTarget()
        update()
    }

    fun setDistanceToTarget() {
        camera.position.set(target).add(camera.direction.scl(-homeDistance))
        camera.lookAt(target)
    }

    override fun handle(e: Event?): Boolean {
        return super.handle(e)
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        this.button = button
        startX = x
        startY = y
        oldCameraDir.set(camera.direction)
        oldCameraPos.set(camera.position)
        timeElapsed = 0f
        numEventsPolled = 0
        lastDeltaX = 0f
        lastDeltaY = 0f



        return super.touchDown(event, x, y, pointer, button)
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        this.button = -1
        super.touchUp(event, x, y, pointer, button)
    }

    override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
        return super.mouseMoved(event, x, y)
    }

    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        ui.setScrollFocus(actor)
        super.enter(event, x, y, pointer, fromActor)
    }

    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        super.exit(event, x, y, pointer, toActor)
    }

    override fun scrolled(event: InputEvent?, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
        zoom(amountY * scrollUnits * Gdx.graphics.deltaTime * (1/camera.distanceFalloff(target,maxDist,minDist)))
        return super.scrolled(event, x, y, amountX, amountY)
    }

    fun moveTo(x: Float, y: Float, z: Float, duration: Float) {
        prevTarget.set(target)
        prevPosition.set(camera.position)
        moveTarget = Vector3(x, y, z)
        movePosition.set(x, y, z)
        timeElapsed = 0f
    }



    override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
        this.key = keycode
        stage.keyboardFocus = actor
        processKey(keycode)
        return super.keyDown(event, keycode)
    }

    override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
        this.key = -1
        return super.keyUp(event, keycode)
    }

    fun zoom(amount: Float): Boolean {
        camera.translate(tmpV1.set(camera.direction).scl(amount))
        update()
        return true
    }

    fun translate(deltaX: Float, deltaY: Float): Boolean {
        camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits))
        camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits))
        camera.position.add(tmpV1).add(tmpV2)
        update()
        return true
    }

    fun rotate(deltaX: Float, deltaY: Float): Boolean {
        tmpV1.set(camera.direction).crs(camera.up).y = 0f
        camera.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle)
        camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle)
        update()
        return true
    }

    override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
        val deltaX = (x - startX) / Gdx.graphics.width
        val deltaY = (startY - y) / Gdx.graphics.height
        startX = x
        startY = y
        processMouse(deltaX, deltaY, Gdx.graphics.deltaTime)
        super.touchDragged(event, x, y, pointer)
    }

    override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
        val deltaX = (x - startX) / Gdx.graphics.width
        val deltaY = (startY - y) / Gdx.graphics.height
        startX = x.toFloat()
        startY = y.toFloat()
        processMouse(deltaX, deltaY, Gdx.graphics.deltaTime)

        super.drag(event, x, y, pointer)
    }

    fun processKey(keycode: Int) {
        when(keycode) {
            home -> {
                camera.position.set(homeTarget)
                camera.lookAt(target)
                update()
            }
            reset -> {
                camera.position.set(oldCameraPos)
                camera.direction.set(oldCameraDir)
                update()
            }
        }
    }

    fun processMouse(x: Float, y: Float, dt:Float) {
        if(button == rotate) {
            rotate(x, y)
        } else if(button == Input.Buttons.MIDDLE  && Gdx.input.isKeyPressed( Input.Keys.SHIFT_LEFT)) {
            translate(x, y)
        } else if(button == zoom) {
            zoom(y)
        }
    }

    fun update() {


        while (moveTarget != null) {

            timeElapsed += Gdx.graphics.deltaTime * 2f
            target.set(prevTarget).lerp(moveTarget, timeElapsed)
            camera.position.set(prevPosition).lerp(movePosition, timeElapsed)
            if (timeElapsed >= 1f) {
                moveTarget = null
//                    keybinds.pan = tmp.pan
//                    keybinds.rotate = tmp.rotate
//                    keybinds.zoom = tmp.zoom
//                    keybinds.home = tmp.home
            }
            camera.up.set(Vector3.Y)
            camera.lookAt(target)

        }

        tangent.set(camera.direction).crs(camera.up).nor()
        translateUnits = camera.position.dst(target)

        camera.update()

    }



}