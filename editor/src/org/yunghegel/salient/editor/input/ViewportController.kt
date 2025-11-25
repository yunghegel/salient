package org.yunghegel.salient.editor.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Plane
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.gdx.utils.data.EnumBitmask
import org.yunghegel.gdx.utils.ext.distanceFalloff
import org.yunghegel.gdx.utils.ext.eachApply
import org.yunghegel.salient.editor.modules.ui

import org.yunghegel.salient.editor.plugins.picking.systems.HoverSystem
import org.yunghegel.salient.engine.input.Control
import org.yunghegel.salient.engine.input.Control.*
import org.yunghegel.salient.engine.system.inject

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
    var scrollUnits  = 100f
    var rotationSteps = 90f

    var rotateAngle = 360f
    var homeDistance  = 10f

    var maxDist = 50f
    var minDist = 2f

    var moveDuration = 0f

    var delegates : MutableList<InputProcessor> = mutableListOf()

    var controls = EnumBitmask(Control::class.java).apply {
        set(ROTATE,PAN,ZOOM,HOME,RESET,value = true)
    }

    fun disableControls(vararg control: Control) {
        control.forEach {
            controls.set(it,false)
            when(it) {
                PAN -> {
                    button = -1
                }
                ROTATE -> {
                    button = -1
                }
                ZOOM -> {
                    button = -1
                }

                HOME -> {
                    home = -1
                }
                RESET -> {
                    reset = -1
                }
            }
        }
    }

    fun enableControls(vararg control: Control) {
        control.forEach {
            controls.set(it,true)
            when(it) {
                PAN -> {
                    button = Input.Buttons.LEFT + Input.Keys.SHIFT_LEFT
                }
                ROTATE -> {
                    button = Input.Buttons.LEFT
                }
                ZOOM -> {
                    button = Input.Buttons.MIDDLE
                }

                HOME -> {
                    home = Input.Keys.CONTROL_LEFT
                }
                RESET -> {
                    reset = Input.Keys.PERIOD
                }
            }
        }
    }

    fun pause(vararg control: Control) {
        (actor as Actor).removeListener(this)
    }

    fun resume() {
        (actor as Actor).addListener(this)
    }

    val hover : HoverSystem by lazy {inject()}

    val clickListener = object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            if (tapCount ==2) {
                val result = intersectRayXZ(x,y) ?: return
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
        delegates.eachApply { touchDown(x.toInt(), y.toInt(), pointer, button) }
        return super.touchDown(event, x, y, pointer, button)
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        this.button = -1
        delegates.eachApply {touchUp(x.toInt(), y.toInt(), pointer, button) }
        super.touchUp(event, x, y, pointer, button)
    }

    override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
        delegates?.eachApply { mouseMoved(x.toInt(), y.toInt()) }
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
        zoom(amountY * scrollUnits)
        delegates.eachApply { scrolled(amountX, amountY) }
        return super.scrolled(event, x, y, amountX, amountY)
    }

    fun moveTo(x: Float, y: Float, z: Float, duration: Float) {
        prevTarget.set(target)
        prevPosition.set(camera.position)
        moveTarget = Vector3(x, y, z)
        movePosition.set(x, y, z)
        timeElapsed = 0f
        moveDuration = duration
    }

    fun moveTo(target: Vector3, duration: Float) {
        moveTo(target.x, target.y, target.z, duration)
    }



    override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
        val stage : Stage = inject()
        this.key = keycode
        stage.keyboardFocus = actor
        processKey(keycode)
        delegates.eachApply { keyDown(keycode) }
        return super.keyDown(event, keycode)
    }

    override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
        this.key = -1
        delegates.eachApply { keyUp(keycode) }
        return super.keyUp(event, keycode)
    }

    fun zoom(amount: Float): Boolean {
        camera.translate(tmpV1.set(camera.direction).scl(amount  * Gdx.graphics.deltaTime))
        update()
        return false
    }

    fun translate(deltaX: Float, deltaY: Float): Boolean {
        camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits))
        camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits))
        camera.position.add(tmpV1).add(tmpV2)
        update()
        return false
    }

    fun rotate(deltaX: Float, deltaY: Float): Boolean {
        tmpV1.set(camera.direction).crs(camera.up).y = 0f
        camera.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle)
        camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle)
        update()
        return false
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


        while(moveTarget != null) {
            timeElapsed += Gdx.graphics.deltaTime
            val t = timeElapsed / moveDuration
            if (t >= 1f) {
                camera.position.set(movePosition)
                moveTarget = null
            } else {
                smoothed.set(prevPosition).lerp(movePosition, t)
                camera.position.set(smoothed)
                camera.lookAt(prevTarget)

                timeElapsed = 0f
            }



        }

        tangent.set(camera.direction).crs(camera.up).nor()
        translateUnits = camera.position.dst2(target) * 0.1f

        camera.update()

    }
}

fun delegateInput(controller: ViewportController = inject(), onlyThis:Boolean = false,listener: InputProcessor?) {
   controller.delegates.add(listener!!)
    if (onlyThis) {
        controller.pause()
    }
}

fun undelegateInput(controller: ViewportController = inject(), onlyThis:Boolean = false, listener: InputProcessor) {
    controller.delegates.remove(listener)
    if (onlyThis) {
        controller.resume()
    }
}

fun pauseViewportControls(controller: ViewportController = inject(),vararg allowed: Control = arrayOf(PAN,ZOOM,HOME,RESET))   {
    controller.disableControls(*allowed)
}

fun resumeViewportControls(controller: ViewportController = inject()) {
    controller.enableControls(ROTATE,PAN,ZOOM,HOME,RESET)
}